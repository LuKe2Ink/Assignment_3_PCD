package it.unibo.ppc;

import java.io.IOException;

import akka.actor.ActorSystem;
import it.unibo.ppc.akka.Boss;
import it.unibo.ppc.akka.Employee;
import it.unibo.ppc.utils.Settings;

public class StartCMD {
    public static void main(String[] args) {
        System.out.println("Hello world! from commandLIne");
        // final ActorSystem<Employee.Report> greeterMain = ActorSystem.create(Boss.create("javaContainer", new Settings(Integer.parseInt(args[1]),Integer.parseInt(args[1]),Integer.parseInt(args[1]))), "helloakka");
        final ActorSystem greeterMain = ActorSystem.create("Sys");
        // greeterMain.actorOf(Boss.create("javaContainer", new Settings(123, 4, 12)), "helloakka");
        greeterMain.actorOf(Boss.props("javaContainer", new Settings(7450, 6, 3)),"Boss");
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