package it.unibo.ppc;

import java.io.IOException;

import akka.actor.typed.ActorSystem;
import it.unibo.ppc.akka.Boss;
import it.unibo.ppc.utils.Settings;

public class StartCMD {
    public static void main(String[] args) {
        System.out.println("Hello world! from commandLIne");
        final ActorSystem<Boss.FileReadingTask> greeterMain = ActorSystem.create(Boss.create(), "helloakka");

        greeterMain.tell(new Boss.FileReadingTask("javaContainer", new Settings(Integer.parseInt(args[1]),Integer.parseInt(args[1]),Integer.parseInt(args[1]))));
        try {
            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException ignored) {
        } finally {
            greeterMain.terminate();
        }
    }
}