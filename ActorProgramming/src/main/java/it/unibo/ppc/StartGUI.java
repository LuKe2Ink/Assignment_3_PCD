package it.unibo.ppc;

import java.io.IOException;

import javax.swing.JFrame;

import akka.actor.ActorSystem;
import it.unibo.ppc.akka.Boss;
import it.unibo.ppc.gui.GUIResponsive;
import it.unibo.ppc.interfaces.MapWrapper;
import it.unibo.ppc.utilities.MapWrapperImplGUI;
import it.unibo.ppc.utils.Settings;

public class StartGUI {
    public static void main(String[] args) {
        System.out.println("Hello world! from GUI");
        final ActorSystem greeterMain = ActorSystem.create("Sys");
        // Settings settings = new Settings(7450, 6, 3);
        JFrame frame = new GUIResponsive(greeterMain);
            frame.pack();
            frame.setVisible(true);
        // MapWrapper map = new MapWrapperImplGUI<>(settings,  frame);
        // greeterMain.actorOf(Boss.props("javaContainer", map ),"Boss");
        // greeterMain.tell(new Boss.FileReadingTask());
        try {
            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException ignored) {
        } finally {
            greeterMain.terminate();
        }
    }
}