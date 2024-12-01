package it.unibo.ppc.akka;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.ActorRef;
import akka.actor.typed.Behavior;


public class Pm extends AbstractActor{
    private final static int NUMBER_OF_WORKERS = 7;

    public static Props props(akka.actor.ActorRef Boss) {
        return Props.create(Pm.class, () -> new Pm(Boss));
    }

    private akka.actor.ActorRef boss;
    private final List<ActorRef> employees = new ArrayList<>();


    public Pm(akka.actor.ActorRef boss) {
        this.boss = boss;
        IntStream.range(0, NUMBER_OF_WORKERS).forEach(i -> {
            ActorRef employee = getContext().actorOf(Employee.props(), "employee-" + i);
            employees.add(employee);
        });
    }
    public interface Message {}

    public static class StopMsg implements Message {}

    public static class ResumeMsg  implements Message {}
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
//        command.replyTo.forEach(replier -> replier.tell(new Ordered(getContext().getParent(),
//                command.tasks.stream().toList().get(command.replyTo.indexOf(replier))), getSelf()));

        System.out.println("Pm.onOrder()");
        for (int i = 0; i < employees.size(); i++) {
            employees.get(i).tell(new Ordered(getSelf(), command.tasks.stream().toList().get(i)), getSelf());
        }
        // #greeter-send-message
        // return this;
    }

    private void onReport(Employee.Report report) {
        System.out.println("Pm received report from Employee: " + report.from);
        boss.tell(report, getSelf());
    }

    private Behavior<Pm.StopMsg> onStopReceive(Pm.StopMsg msg) {
        System.out.println("PM  received stop");
//        getContext().getChildren().forEach(child -> System.out.println("Sending stop to: " + child.path()));
//        getContext().getChildren().forEach(child -> child.tell(new Employee.StopMsg(), getSelf()));
//        employees.forEach(child -> child.tell(new Employee.StopMsg(), getSelf()));
        employees.forEach(employee -> employee.tell(new Employee.StopMsg(), getSelf()));

//        this.getContext().getChildren().forEach(child -> child.tell(new Employee.StopMsg(), getSelf()));
        return null;
    }

    private void onResumeReceive(Pm.ResumeMsg msg) {
        System.out.println("PM received resume");
        employees.forEach(employee -> employee.tell(new Employee.ResumeMsg(), getSelf()));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Pm.Order.class, this::onOrder)
                .match(Pm.StopMsg.class, this::onStopReceive)
                .match(Pm.ResumeMsg.class, this::onResumeReceive)
                .match(Employee.Report.class, this::onReport)
                .build();
    }
}
