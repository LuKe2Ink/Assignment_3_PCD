package it.unibo.ppc;

import java.io.IOException;

import akka.actor.ActorSystem;
import it.unibo.ppc.akka.Boss;
import it.unibo.ppc.akka.Employee;
import it.unibo.ppc.utils.Settings;

public class StartGUI {
    public static void main(String[] args) {
        System.out.println("Hello world! from GUI");
        final ActorSystem greeterMain = ActorSystem.create("Sys");
        // greeterMain.actorOf(Boss.create("javaContainer", new Settings(123, 4, 12)), "helloakka");
        greeterMain.actorOf(Boss.props("javaContainer", new Settings(123, 4, 12)),"Boss");
        // greeterMain.actorOf(Boss.props(),"Boss");
        try {
            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException ignored) {
        } finally {
            greeterMain.terminate();
        }
    }
}