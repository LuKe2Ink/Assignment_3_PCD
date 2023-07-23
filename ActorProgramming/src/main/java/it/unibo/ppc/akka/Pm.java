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
        public final String whom;
        public final List<ActorRef<Ordered>> replyTo;
        public Collection<List<String>> tasks;

        public Order(String whom, List<ActorRef<Ordered>> replyTo, Collection<List<String>> tasks) {
            this.whom = whom;
            this.replyTo = replyTo;
            this.tasks = tasks;
        }
    }

    public static final class Ordered {
        public final String whom;
        public final ActorRef<Order> from;
        public List<String> task;

        public Ordered(String whom, ActorRef<Order> from, List<String> task) {
            this.whom = whom;
            this.from = from;
            this.task = task;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((whom == null) ? 0 : whom.hashCode());
            result = prime * result + ((from == null) ? 0 : from.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Ordered other = (Ordered) obj;
            if (whom == null) {
                if (other.whom != null)
                    return false;
            } else if (!whom.equals(other.whom))
                return false;
            if (from == null) {
                if (other.from != null)
                    return false;
            } else if (!from.equals(other.from))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "Ordered [whom=" + whom + ", from=" + from + "]";
        }

    }

    public static Behavior<Order> create() {
        return Behaviors.setup(Pm::new);
    }

    @Override
    public Receive<Order> createReceive() {
        return newReceiveBuilder().onMessage(Order.class, this::onOrder).build();
    }

    private Behavior<Order> onOrder(Order command) {
        getContext().getLog().info("Received an order from {}!", command.whom);
        // #greeter-send-message
        // command.replyTo.tell(new Ordered(command.whom, getContext().getSelf()));
        command.replyTo.forEach(replier -> replier.tell(new Ordered(command.whom, getContext().getSelf(),
                command.tasks.stream().toList().get(command.replyTo.indexOf(replier)))));
        // #greeter-send-message
        return this;
    }
}
