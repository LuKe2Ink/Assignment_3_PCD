package it.unibo.ppc;

import java.io.IOException;
import java.util.Map;

import akka.actor.ActorSystem;
import it.unibo.ppc.akka.Boss;
import it.unibo.ppc.akka.Employee;
import it.unibo.ppc.interfaces.MapWrapper;
import it.unibo.ppc.utils.MapWrapperImpl;
import it.unibo.ppc.utils.Settings;

public class StartCMD {
    public static void main(String[] args) {
        System.out.println("Hello world! from commandLIne");
        final ActorSystem greeterMain = ActorSystem.create("Sys");
        Settings settings = new Settings(7450, 6, 3);
        MapWrapper map = new MapWrapperImpl(settings);
        greeterMain.actorOf(Boss.props("javaContainer", map ),"Boss");
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