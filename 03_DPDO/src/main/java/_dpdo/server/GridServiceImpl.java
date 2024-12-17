package _dpdo.server;

import _dpdo.server.interfaces.GridService;
import _dpdo.Pair;
import _dpdo.PixelArt;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class GridServiceImpl implements GridService {
    //map with the pixels and their color
    Map<Pair<Integer, Integer>, Integer> grid = new HashMap<>();
    //list with the client instances
    List<PixelArt> clients = new ArrayList<>();

    @Override
    public synchronized void register(PixelArt client) throws RemoteException {
        //addition of the new client in the client list
        clients.add(client);
        //the new client receives the grid
        client.receiveGrid(grid);
    }

    @Override
    public synchronized void exit(UUID clientId) {
        //remove the client that left the game from the client list
        clients = clients.stream().filter(c -> {
            try {
                return !c.getClientId().equals(clientId);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public synchronized void setPixel(Integer x, Integer y, Integer color) {
        //update the grid with the information in input
        this.grid.put(new Pair<>(x, y), color);
        //send the updated grid
        sendGrid();
    }

    private synchronized void sendGrid() {
        //send the grid to all the clients
        this.clients.forEach(c -> {
            try {
                c.receiveGrid(this.grid);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
