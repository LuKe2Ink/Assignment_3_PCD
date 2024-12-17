package _dpdo.server.interfaces;

import _dpdo.PixelArt;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface GridService extends Remote {

    void register (PixelArt client) throws RemoteException;
    void exit (UUID clientID) throws RemoteException;

    void setPixel(Integer x, Integer y, Integer color) throws RemoteException;

}
