package _dpdo;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
                
    public static void main(String args[]) {
        
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            GridService gridService = new GridServiceImpl();
            GridService gridStub = (GridService) UnicastRemoteObject.exportObject(gridService, 0);
            BrushService brushService = new BrushServiceImpl();
            BrushService brushStub = (BrushService) UnicastRemoteObject.exportObject(brushService, 0);

            // Bind the remote object's stub in the registry
            registry.rebind("grid", gridStub);
            registry.rebind("brush", brushStub);


            
            System.out.println("Objects registered.");
        } catch (Exception e) {
            System.err.println("Server exception: " + e);
            e.printStackTrace();
        }
    }
}