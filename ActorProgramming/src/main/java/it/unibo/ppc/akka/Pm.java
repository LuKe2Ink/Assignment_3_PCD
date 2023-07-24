package it.unibo.ppc.akka;

import java.util.Collection;
import java.util.List;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Pm extends AbstractBehavior<Pm.Order> {
    public Pm(ActorContext<Order> context) {
        super(context);
    }

    public static final class Order {
        public final String directoryPath;
        public final ActorRef<Employee.Report> parent;
        public final List<ActorRef<Ordered>> replyTo;
        public Collection<List<String>> tasks;

        public Order(ActorRef<Employee.Report> parent, String directoryPath, List<ActorRef<Ordered>> replyTo, Collection<List<String>> tasks) {
            this.parent = parent;
            this.directoryPath = directoryPath;
            this.replyTo = replyTo;
            this.tasks = tasks;
        }

        
    }

    public static final class Ordered {
        public final ActorRef<Employee.Report> parent;
        // public final String whom;
        public final ActorRef<Order> from;
        public List<String> task;

        public Ordered(ActorRef<Employee.Report> parent,ActorRef<Order> from, List<String> task) {
            // this.whom = whom;
            this.parent = parent;
            this.from = from;
            this.task = task;
        }
        //TODO equals hashcode etc....
    }

    public static Behavior<Order> create() {
        return Behaviors.setup(Pm::new);
    }

    @Override
    public Receive<Order> createReceive() {
        return newReceiveBuilder().onMessage(Order.class, this::onOrder).build();
    }

    private Behavior<Order> onOrder(Order command) {
        // getContext().getLog().info("Received an order from {}!", command.whom);
        // #greeter-send-message
        // command.replyTo.tell(new Ordered(command.whom, getContext().getSelf()));
        command.replyTo.forEach(replier -> replier.tell(new Ordered(command.parent, getContext().getSelf(),
                command.tasks.stream().toList().get(command.replyTo.indexOf(replier)))));
        // #greeter-send-message
        return this;
    }
}
