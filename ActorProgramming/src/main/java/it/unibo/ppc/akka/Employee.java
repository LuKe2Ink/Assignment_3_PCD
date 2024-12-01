package it.unibo.ppc.akka;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
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

    private List<String> currentTasks = new ArrayList<>();
    private Iterator<String> taskIterator = null;


    public static class ProcessTask {
        public final String task;
        public final ActorRef sender;

        public ProcessTask(String task, ActorRef sender) {
            this.task = task;
            this.sender = sender;
        }
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
    

//    private void  onOrderedReceive(Pm.Ordered message){
//        System.out.println(this.name + " received task");
//        message.task.forEach(singleTask -> {
//            try {
//                // Controllo continuo dello stato di pausa
//                while (stopFlag) {
//                    System.out.println(this.name + " paused, waiting to resume...");
//                    Thread.sleep(100); // Attesa attiva per la pausa
//                }
//                // Esegue il lavoro
//                Pair<String, Integer> result = Utils.linesWithBufferInputStream(singleTask);
//                message.from.tell(new Report(result, this.name), getSelf());
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//        // message.from.tell(new Report("bho", 0, "me"), getSelf());
//        // message.parent.tell(new Report(this.name, 0, this.name));
//        // getContext().getLog().info("Got it Pm -{}", this.name, getContext(), getClass());
//    }

    private void onProcessTask(ProcessTask msg) {
        if (!stopFlag) {
            try {
                Pair<String, Integer> result = Utils.linesWithBufferInputStream(msg.task);
                msg.sender.tell(new Report(result, this.name), getSelf());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            processNextTask(msg.sender);
        } else {
            System.out.println(this.name + " paused, will not process further tasks.");
        }
    }

    private void onOrderedReceive(Pm.Ordered message) {
    System.out.println(this.name + " received tasks.");
    this.currentTasks = message.task;
    this.taskIterator = currentTasks.iterator();
    processNextTask(message.from);
}

    private void processNextTask(ActorRef sender) {
        if (taskIterator != null && taskIterator.hasNext()) {
            if (!stopFlag) {
                String task = taskIterator.next();
                getContext().getSystem().scheduler().scheduleOnce(
                        scala.concurrent.duration.Duration.create(0, java.util.concurrent.TimeUnit.MILLISECONDS),
                        getSelf(),
                        new ProcessTask(task, sender),
                        getContext().getSystem().dispatcher(),
                        getSelf()
                );
            } else {
                System.out.println(this.name + " paused, waiting to resume...");
            }
        } else if (taskIterator != null && !taskIterator.hasNext()) {
            System.out.println(this.name + " completed all tasks.");
        }
    }



    @Override
    public Receive createReceive() {
        // return Behaviors.receive(Message.class).onMessage(Ordered.class, Employee::onOrderedReceive)
        return receiveBuilder()
                .match(Ordered.class, this::onOrderedReceive)
                .match(StopMsg.class, this::onStopReceive)
                .match(ProcessTask.class, this::onProcessTask)
                .match(Employee.ResumeMsg.class, this::onResumeReceive)
                .match(Ordered.class, this::onOrderedReceive)
                .build();
    }

    private void onResumeReceive(Employee.ResumeMsg msg) {
        System.out.println(this.name + " received resume");
        this.stopFlag = false;
        if (taskIterator != null && taskIterator.hasNext()) {
            System.out.println(this.name + " resuming task processing.");
            processNextTask(getSender());
        }
    }

    private  void onStopReceive(Employee.StopMsg msg) {
        System.out.println(this.name + " received stop");
        this.stopFlag = true;
    }
}
