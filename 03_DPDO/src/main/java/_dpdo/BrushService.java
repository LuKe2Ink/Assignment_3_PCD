package _dpdo;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface BrushService extends Remote {

    void addBrush(PixelArt client) throws RemoteException;
    void removeBrush(UUID clientId, BrushManager.Brush brush) throws RemoteException;
    void receiveMovement(UUID clientId, BrushManager.Brush brush) throws RemoteException;
}
