package it.unibo.ppc.akka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import it.unibo.ppc.akka.Pm.Ordered;
import akka.actor.typed.javadsl.Behaviors;

public class Employee extends AbstractBehavior<Pm.Ordered>{

    private String name;

    public static class Report {
        public Map<String, Integer> response; 
        public String from;

        public Report(String filename, int numberOfLines, String from){
            this.response = Map.of(filename, numberOfLines);
            this.from = from;
        }

    }

    private Employee(ActorContext<Ordered> context) {
        super(context);
        this.name = context.getSelf().toString();
    }

    public static Behavior<Pm.Ordered> create(){
        return Behaviors.setup(context -> new Employee(context));
    }


    @Override
    public Receive<Pm.Ordered> createReceive() {
        return newReceiveBuilder().onMessage(Pm.Ordered.class, this::onMsgReceived).build();
    }
    

    private Behavior<Pm.Ordered> onMsgReceived(Pm.Ordered message){
        // getContext().getLog().info("Gonna mess around wtih: " + message.task);
        // message.from.tell(new Ordered(name, getContext(), new ArrayList<String>()));
        // message.from.tell(null);
        message.parent.tell(new Report(this.name, 0, this.name));
        // getContext().getLog().info("Got it Pm -{}", this.name, getContext(), getClass());
        return this;
    }
}
