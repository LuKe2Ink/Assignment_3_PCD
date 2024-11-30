package _dpamp;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.swing.SwingUtilities;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;


public class PixelArt {
  private static final String EXCHANGE_NAME = "name exchange";
  public static void main(final String[] argv) throws Exception {
    try {
      // Generate a random UUID
      final UUID uuid = UUID.randomUUID();
      final String identifier = uuid.toString();
      //Map with the pixels colored
      final Map<Pair<Integer, Integer>, Integer> coloredPixels = new HashMap<>();
      final ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("localhost");
      final Connection connection = factory.newConnection();
      final Channel channel = connection.createChannel();
      channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

      //declaration of the queues
      channel.queueDeclare(identifier + "mouse", false, false, false, null);
      channel.queueDeclare(identifier + "color", false, false, false, null);
      channel.queueDeclare(identifier + "history", false, false, false, null);
      channel.queueDeclare(identifier + "exit", false, false, false, null);
      // Bind the queue to the exchange
      channel.queueBind(identifier + "mouse", EXCHANGE_NAME, "topic.mouse");
      channel.queueBind(identifier + "color", EXCHANGE_NAME, "topic.color");
      channel.queueBind(identifier + "history", EXCHANGE_NAME, "topic.history");
      channel.queueBind(identifier + "exit", EXCHANGE_NAME, "topic.exit");

      //publish a message to request the history
      final String requestMessage = "NEED_HISTORY";
      channel.basicPublish(EXCHANGE_NAME, "topic.history", null, requestMessage.getBytes(StandardCharsets.UTF_8));
      System.out.println(" [x] Sent '" + requestMessage + "'");

      //setting of the Brush
      final var brushManager = new BrushManager();
      final var localBrush = new BrushManager.Brush(0, 0, randomColor(), identifier);
      brushManager.addBrush(localBrush);
      final PixelGrid grid = new PixelGrid(40, 40);
      final PixelGridView view = new PixelGridView(grid, brushManager, 800, 600);

      //listener for the movement of the mouse
      view.addMouseMovedListener((x, y) -> {
        localBrush.updatePosition(x, y);
        view.refresh();
        //the message contains the x and y of the mouse and the id and color of the brush
        final String message = x + "_" + y + "_" + localBrush.getIdBrush() + "_" +  localBrush.getColor();
        //publish a message to notify that the local brush changed the position
        try {
			channel.basicPublish(EXCHANGE_NAME, "topic.mouse", null, message.getBytes(StandardCharsets.UTF_8));
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println(" [x] Sent '" + message + "'");
      });

      //listener for the addition of the pixel
      view.addPixelGridEventListener((x, y) -> {
        grid.set(x, y, localBrush.getColor());
        //update of the coloredPixels map
        coloredPixels.put(new Pair<>(x, y), localBrush.getColor());
        view.refresh();
        //the message contains x, y, color of the brush
        final String message = x + "_" + y + "_" + localBrush.getColor();
        //publish a message to notify that a new pixel was colored
        try {
			channel.basicPublish(EXCHANGE_NAME, "topic.color", null, message.getBytes(StandardCharsets.UTF_8));
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println(" [x] Sent '" + message + "'");
      });


      //callbacks to manage the messages
      final DeliverCallback deliverCallbackMouse = (consumerTag, delivery) -> {
        final String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        System.out.println(" [x] Received A '" + message + "' by thread "+Thread.currentThread().getName());
        updateMouse(message, view, brushManager);
      };

      final DeliverCallback deliverCallbackExit = (consumerTag, delivery) -> {
        final String idBrush = new String(delivery.getBody(), StandardCharsets.UTF_8);
        System.out.println(" [x] Received A '" + idBrush + "' by thread "+Thread.currentThread().getName());
        //the brush is removed when the user exits
        brushManager.removeBrush(brushManager.getBrushFromId(idBrush));
      };

      final DeliverCallback deliverCallbackHistory = (consumerTag, delivery) -> {
        final String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        System.out.println(" [x] Received A '" + message + "' by thread "+Thread.currentThread().getName());
        if ("NEED_HISTORY".equals(message)){
          if ( !coloredPixels.isEmpty()){
            final String stringMessage = coloredPixels.entrySet().stream().map(e -> e.getKey().getX()+"_"+e.getKey().getY()+"_"+e.getValue()).reduce((e1, e2) -> e1.concat("@".concat(e2))).orElse("");
            //publish the history
            channel.basicPublish(EXCHANGE_NAME, "topic.history", null, stringMessage.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + stringMessage + "'");
          }
        } else{
          final String[] cellAndColor = message.split("@");
          //update the coloredPixels map with the history and render of the pixels in it
          coloredPixels.putAll(Arrays.stream(cellAndColor).collect(Collectors.toMap(e -> new Pair<>(Integer.parseInt(e.split("_")[0]), Integer.parseInt(e.split("_")[1])), e -> Integer.parseInt(e.split("_")[2]))));
          coloredPixels.forEach((p, c) -> grid.set(p.getX(), p.getY(), c));
        }

        try {
          Thread.sleep(10);
        } catch (final Exception ex) {
          ex.printStackTrace();
        }
      };

      // Consume messages from the queues
      consumeMessages(channel, identifier + "color", coloredPixels, view, grid);
      channel.basicConsume(identifier + "mouse", true, deliverCallbackMouse, consumerTag -> {});
      channel.basicConsume(identifier + "history", true, deliverCallbackHistory, consumerTag -> {});
      channel.basicConsume(identifier + "exit", true, deliverCallbackExit, consumerTag -> {});
      view.addColorChangedListener(localBrush::setColor);

      view.display();

      //listener for the closure of the window
      view.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(final WindowEvent e) {
          // Perform any necessary cleanup or actions
          System.out.println("User is leaving...");
          final String brushId = localBrush.getIdBrush();
          try {
            //publish a message to notify the exit
            channel.basicPublish(EXCHANGE_NAME, "topic.exit", null, brushId.getBytes(StandardCharsets.UTF_8));
          } catch (final IOException ex) {
            throw new RuntimeException(ex);
          }
          System.out.println(" [x] Sent '" + brushId + "'");
          try {
            channel.close();
            connection.close();
          } catch (IOException | TimeoutException ex) {
            throw new RuntimeException(ex);
          }

        }
      });


    } catch (IOException | TimeoutException e) {
      e.printStackTrace();
    }
  }

  //get a random color
  public static int randomColor() {
    final Random rand = new Random();
    return rand.nextInt(256 * 256 * 256);
  }

  //update of the colored pixels
  public static void updateColor(final String message, final PixelGridView view, final PixelGrid grid, final Map<Pair<Integer, Integer>, Integer> coloredPixels){
    SwingUtilities.invokeLater(() -> {
      final String[] messageContent = message.split("_");
      //the message contains x, y, color of the brush
      grid.set(Integer.parseInt(messageContent[0]), Integer.parseInt(messageContent[1]), Integer.parseInt(messageContent[2]));
      view.refresh();
      coloredPixels.put(new Pair<>(Integer.parseInt(messageContent[0]), Integer.parseInt(messageContent[1])), Integer.parseInt(messageContent[2]));
    });
  }

  //update of the movement of the mouse
  private static void updateMouse(final String message, final PixelGridView view, final BrushManager brushManager) {
    SwingUtilities.invokeLater(() -> {
      final String[] messageContent = message.split("_");
      //the message contains the x and y of the mouse and the id and color of the brush
      final BrushManager.Brush currentBrush = brushManager.getBrushFromInfo(messageContent);
      currentBrush.updatePosition(Integer.parseInt(messageContent[0]), Integer.parseInt(messageContent[1]));
      view.refresh();
    });
  }

    //consume of message for update the colored pixels
    private static void consumeMessages(final Channel channel, final String identifier, final Map<Pair<Integer, Integer>, Integer> coloredPixels, final PixelGridView view, final PixelGrid grid) throws IOException {
      // Create a consumer and start consuming messages
      final DeliverCallback deliverCallbackColor = (consumerTag, delivery) -> {
        final String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        System.out.println(" [x] Received A '" + message + "' by thread "+Thread.currentThread().getName());
        updateColor(message, view, grid, coloredPixels);
        try {
          Thread.sleep(10);
        } catch (final Exception ex) {
          ex.printStackTrace();
        }
      };
      channel.basicConsume(identifier, true, deliverCallbackColor, consumerTag -> {});

  }




}