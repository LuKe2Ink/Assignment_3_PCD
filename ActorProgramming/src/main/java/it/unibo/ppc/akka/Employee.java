package it.unibo.ppc.akka;

import java.util.Map;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import it.unibo.ppc.akka.Pm.Ordered;
import it.unibo.ppc.utilities.Utils;

public class Employee extends AbstractActor{

    private String name;

    public static Props props() {
        return Props.create(Employee.class);
    }


    public static class Report implements Message {
        public Map<String, Integer> response; 
        public String from;

        public Report(String filename, int numberOfLines, String from){
            this.response = Map.of(filename, numberOfLines);
            this.from = from;
        }

    }
    public interface Message {}

    public static class StopMsg implements Message{}

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
        message.from.tell(new Report("bho", 0, "me"), getSelf());
        // message.parent.tell(new Report(this.name, 0, this.name));
        // getContext().getLog().info("Got it Pm -{}", this.name, getContext(), getClass());
    }

    @Override
    public Receive createReceive() {
        // return Behaviors.receive(Message.class).onMessage(Ordered.class, Employee::onOrderedReceive)
        return receiveBuilder().match(Ordered.class, this::onOrderedReceive)
        .match(StopMsg.class, this::onStopReceive)
        .match(Ordered.class, this::onOrderedReceive)
        .build();
    }

    private Behavior<Employee.StopMsg> onStopReceive(Employee.StopMsg msg) {
        return null;
    }
}
