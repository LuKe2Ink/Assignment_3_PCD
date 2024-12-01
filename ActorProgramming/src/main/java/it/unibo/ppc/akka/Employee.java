package it.unibo.ppc.akka;

import java.util.Map;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import it.unibo.ppc.akka.Pm.Ordered;
import it.unibo.ppc.utilities.Utils;
import it.unibo.ppc.utilities.Utils.Pair;

public class Employee extends AbstractActor{
    boolean stopFlag = false;
    private String name;

    public static Props props() {
        return Props.create(Employee.class);
    }


    public static class Report implements Message {
        public Pair<String, Integer> response; 
        public String from;

        public Report(Pair<String, Integer> response, String from){
            this.response = response;
            this.from = from;
        }

    }
    public interface Message {}

    public static class StopMsg implements Message{}

    public static class ResumeMsg implements Message{}

    // private Employee(context) {
    //     // super(context);
    //     this.name = context.getSelf().toString();
    // }

    // public static Behavior<Message> create(){
    //     return Behaviors.setup(new Employee());
    //     // return Behaviors.receive(Message.class).onMessage(Report.class, Employee::)
    // }


    // @Override
    // public Receive<Pm.Ordered> createReceive() {
    //     return newReceiveBuilder()
    //     .onMessage(Pm.Ordered.class, this::onMsgReceived)
    //     .build();
    // }
    

    private void  onOrderedReceive(Pm.Ordered message){
        System.out.println(".()");
        // getContext().getLog().info("Gonna mess around wtih: " + message.task);
        // message.from.tell(new Ordered(name, getContext(), new ArrayList<String>()));
        // message.from.tell(null);
        message.task.forEach(singleTask -> {
            try {
                while (stopFlag) {
                    System.out.println("Employee paused, waiting to resume...");
                    Thread.sleep(100); // Attesa attiva fino a quando stopFlag non viene resettato
                }
                Pair<String, Integer> result = Utils.linesWithBufferInputStream(singleTask);
                message.from.tell(new Report(result, this.name), getSelf());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        // message.from.tell(new Report("bho", 0, "me"), getSelf());
        // message.parent.tell(new Report(this.name, 0, this.name));
        // getContext().getLog().info("Got it Pm -{}", this.name, getContext(), getClass());
    }

    @Override
    public Receive createReceive() {
        // return Behaviors.receive(Message.class).onMessage(Ordered.class, Employee::onOrderedReceive)
        return receiveBuilder()
                .match(Ordered.class, this::onOrderedReceive)
                .match(StopMsg.class, this::onStopReceive)
                .match(ResumeMsg.class, this::onResumeReceive)
                .match(Ordered.class, this::onOrderedReceive)
                .build();
    }

    private Behavior<Employee.ResumeMsg> onResumeReceive(Employee.ResumeMsg msg) {
        System.out.println(this.name + " received resume");
        this.stopFlag = false;
        return null;
    }

    private Behavior<Employee.StopMsg> onStopReceive(Employee.StopMsg msg) {
        System.out.println(this.name + " received stop");
        this.stopFlag = true;
        return null;
    }
}
