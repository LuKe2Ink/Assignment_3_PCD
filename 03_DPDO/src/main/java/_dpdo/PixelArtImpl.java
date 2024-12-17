package _dpdo;

import _dpdo.server.interfaces.BrushService;
import _dpdo.server.interfaces.GridService;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class PixelArtImpl extends UnicastRemoteObject implements PixelArt, Serializable {

  // Generate a random UUID
  private static final UUID serialVersionUID = UUID.randomUUID();
  private final BrushManager brushManager = new BrushManager();
  private final BrushManager.Brush localBrush = new BrushManager.Brush(0, 0, randomColor());
  private final PixelGrid grid = new PixelGrid(40, 40);
  private final Map<Pair<Integer, Integer>, Integer> coloredPixels = new HashMap<>();
  private final PixelGridView view = new PixelGridView(grid, brushManager, 800, 600);
  private GridService gridService;
  private BrushService brushService;
  public PixelArtImpl() throws IOException {
  }

  //configuration of RMI and graphic details. Set of the listeners
  public void configuration()  {
    try {
      Registry registry = LocateRegistry.getRegistry(null);
      this.gridService = (GridService) registry.lookup("grid");
      this.brushService = (BrushService) registry.lookup("brush");

      //registration of the client
      gridService.register(this);
      brushService.addBrush(this);
    } catch (Exception e) {
      System.err.println("Client exception: " + e);
      e.printStackTrace();
    }
    brushManager.addBrush(localBrush);

    //listener for the movement of the mouse
    view.addMouseMovedListener((x, y) -> {
      localBrush.updatePosition(x, y);
      brushService.receiveMovement(getClientId(), localBrush);
      view.refresh();

    });

    //listener for the addition of the pixel
    view.addPixelGridEventListener((x, y) -> {
      this.gridService.setPixel(x, y, localBrush.getColor());
    });

    view.addColorChangedListener(localBrush::setColor);



    //listener for the closure of the window
    view.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        // Perform any necessary cleanup or actions
        System.out.println("User is leaving...");
        try {
          brushService.removeBrush(getClientId(), getLocalBrush());
          gridService.exit(getClientId());
        } catch (RemoteException ex) {
          throw new RuntimeException(ex);
        }
      }
    });

    view.display();
  }

  //get the client id
  public UUID getClientId() {return serialVersionUID;}

  //get a random color
  public static int randomColor() {
    Random rand = new Random();
    return rand.nextInt(256 * 256 * 256);
  }


  //received the grid from the service and its render
  @Override
  public void receiveGrid(Map<Pair<Integer, Integer>, Integer> map) {
    this.coloredPixels.putAll(map);
    this.coloredPixels.forEach((p, c) -> this.grid.set(p.getX(), p.getY(), c));
    SwingUtilities.invokeLater(view::refresh);
  }

  //get the local brush
  @Override
  public BrushManager.Brush getLocalBrush() {
    return localBrush;
  }

  //receive the brushes from the service and their render
  @Override
  public void receiveBrushes(Set<BrushManager.Brush> brushes) throws RemoteException {
    this.brushManager.addAllBrushes(brushes);
    //draw brushes
    SwingUtilities.invokeLater(view::refresh);
  }
}
