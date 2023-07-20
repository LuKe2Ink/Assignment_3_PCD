package it.unibo.ppc;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.Behaviors;

public class Boss extends AbstractBehavior<Boss.SayMyName> {
    public Boss(ActorContext<SayMyName> context) {
        super(context);
        worker = context.spawn(Pm.create(), "tuSorella");
    }

    public static class SayMyName {
        public final String name;

        public SayMyName(String name) {
            this.name = name;
        }
    }

    private final ActorRef<Pm.Order> worker;

    public static Behavior<SayMyName> create() {
        return Behaviors.setup(Boss::new);
    }

    @Override
    public Receive<SayMyName> createReceive() {
        // throw new UnsupportedOperationException("Unimplemented method
        // 'createReceive'");
        return newReceiveBuilder().onMessage(SayMyName.class, this::onStart).build();

    }

    private Behavior<SayMyName> onStart(SayMyName command) {
        ActorRef<Pm.Ordered> replyTo = getContext().spawn(Employee.create(1), command.name);
        worker.tell(new Pm.Order(command.name, replyTo));
        return this;
    }
}
