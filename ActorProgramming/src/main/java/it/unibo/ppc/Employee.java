package it.unibo.ppc;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.scaladsl.Behaviors;
import it.unibo.ppc.Boss.SayMyName;
import it.unibo.ppc.Pm.Ordered;

public class Employee extends AbstractBehavior<Pm.Ordered>{

    private Employee(ActorContext<Ordered> context, int number) {
        super(context);
        this.name = "Employee: " + String.valueOf(number);
    }

    public static Behavior<Pm.Ordered> create(int number){
        return Behaviors.setup(context -> new Employee(context, number));
    }

    private String name;

    @Override
    public Receive<Pm.Ordered> createReceive() {
        return newReceiveBuilder().onMessage(Pm.Ordered.class, this::onMsgReceived).build();
    }
    
    private Behavior<Pm.Ordered> onMsgReceived(Pm.Ordered message){
        getContext().getLog().info("Got it Boss -{}", this.name, getContext(), getClass());
        return this;
    }
}
