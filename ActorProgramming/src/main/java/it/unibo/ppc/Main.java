package it.unibo.ppc;

import akka.actor.typed.ActorSystem;
import it.unibo.ppc.Boss.SayMyName;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        final ActorSystem<Boss.SayMyName> greeterMain = 
        ActorSystem.create(Boss.create(), "helloakka");

        greeterMain.tell(new Boss.SayMyName("Cacca"));
    }
}