package it.unibo.ppc.akka;

import java.util.Collection;
import java.util.List;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.ActorRef;

public class Pm extends AbstractActor{

    public static Props props() {
        return Props.create(Pm.class);
    }


    public Pm() {}

    public static final class Order {
        public final String directoryPath;
        public final List<ActorRef> replyTo;
        public Collection<List<String>> tasks;

        public Order(String directoryPath, List<ActorRef> replyTo, Collection<List<String>> tasks) {
            this.directoryPath = directoryPath;
            this.replyTo = replyTo;
            this.tasks = tasks;
        }

        
    }

    public static final class Ordered {
        // public final String whom;
        public final ActorRef from;
        public List<String> task;

        public Ordered(ActorRef from, List<String> task) {
            // this.whom = whom;
            this.from = from;
            this.task = task;
        }
        //TODO equals hashcode etc....
    }

    // public static Behavior<Order> create() {
    //     return Behaviors.setup(Pm::new);
    // }

    // @Override
    // public Receive<Order> createReceive() {
    //     return newReceiveBuilder().onMessage(Order.class, this::onOrder).build();
    // }

    private void onOrder(Order command) {
        System.out.println("Pm.onOrder()");
        // #greeter-send-message
        // command.replyTo.tell(new Ordered(command.whom, getContext().getSelf()));
        command.replyTo.forEach(replier -> replier.tell(new Ordered(getContext().getParent(),
                command.tasks.stream().toList().get(command.replyTo.indexOf(replier))), getSelf()));
        // #greeter-send-message
        // return this;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Pm.Order.class, this::onOrder).build();
    }
}
