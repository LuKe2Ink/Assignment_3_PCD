package it.unibo.ppc.akka;

import java.util.ArrayList;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import it.unibo.ppc.akka.Pm.Ordered;
import akka.actor.typed.javadsl.Behaviors;

public class Employee extends AbstractBehavior<Pm.Ordered>{

    public static class Report {} 
    private Employee(ActorContext<Ordered> context) {
        super(context);
    }

    public static Behavior<Pm.Ordered> create(){
        return Behaviors.setup(context -> new Employee(context));
    }

    private String name;

    @Override
    public Receive<Pm.Ordered> createReceive() {
        return newReceiveBuilder().onMessage(Pm.Ordered.class, this::onMsgReceived).build();
    }
    
    private Behavior<Pm.Ordered> onMsgReceived(Pm.Ordered message){
        getContext().getLog().info("Gonna mess around wtih: " + message.task);
        // message.from.tell(new Ordered(name, getContext(), new ArrayList<String>()));
        // message.from.tell(null);
        // getContext().getLog().info("Got it Pm -{}", this.name, getContext(), getClass());
        return this;
    }
}
