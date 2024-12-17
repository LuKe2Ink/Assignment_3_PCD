package _dpdo.server;

import _dpdo.BrushManager;
import _dpdo.server.interfaces.BrushService;
import _dpdo.PixelArt;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class BrushServiceImpl implements BrushService {

    //list with the client instances
    private List<PixelArt> clients = new ArrayList<>();
    //map with the brushes
    private final Map<UUID, BrushManager.Brush> brushes = new HashMap<>();

    //the client pass in the parameter added its brush
    @Override
    public synchronized void addBrush(PixelArt client) throws RemoteException {
        clients.add(client);
        brushes.put(client.getClientId(), client.getLocalBrush());
        //service sends the set with all the brushes to the client that join the game
        client.receiveBrushes(new HashSet<>(brushes.values()));
    }

    @Override
    public synchronized void removeBrush(UUID clientId, BrushManager.Brush brush) throws RemoteException {
        //remove from the brush collection the brush of the client that left the game
        clients = clients.stream().filter(c -> {
            try {
                return !c.getClientId().equals(clientId);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        brushes.remove(clientId);
        //send the updated collection to all the clients still in the game
        sendBrushes();
    }

    @Override
    public synchronized void receiveMovement(UUID clientId, BrushManager.Brush brush) throws RemoteException {
        //update the brush that changes its position
        brushes.put(clientId, brush);
        //send the updated collection to all the clients
        sendBrushes();
    }

    private synchronized void sendBrushes() {
        //update all the clients with the new configuration of the brushes
        Set<BrushManager.Brush> brushSet = new HashSet<>(brushes.values());
        clients.forEach(c -> {
            try {
                c.receiveBrushes(brushSet);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
