package it.unibo.ppc;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Pm extends AbstractBehavior<Pm.Order>{
    public Pm(ActorContext<Order> context) {
        super(context);
    }

    public static final class Order {
        public final String whom;
        public final ActorRef<Ordered> replyTo;

        public Order(String whom, ActorRef<Ordered> replyTo) {
        this.whom = whom;
        this.replyTo = replyTo;
        }
    }

    public static final class Ordered {
        public final String whom;
        public final ActorRef<Order> from;

        public Ordered(String whom, ActorRef<Order> from) {
        this.whom = whom;
        this.from = from;
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
        getContext().getLog().info("Hello {}!", command.whom);
        //#greeter-send-message
        command.replyTo.tell(new Ordered(command.whom, getContext().getSelf()));
        //#greeter-send-message
        return this;
  }
}
