package it.unibo.ppc.akka;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import it.unibo.ppc.akka.Pm.Ordered;
import akka.actor.typed.javadsl.Behaviors;

public class Employee extends AbstractBehavior<Pm.Ordered>{

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
        getContext().getLog().info("Got it Pm -{}", this.name, getContext(), getClass());
        return this;
    }
}
