package it.unibo.ppc.akka;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import it.unibo.ppc.utilities.Utils;
import akka.actor.typed.javadsl.Behaviors;


public class Boss extends AbstractBehavior<Boss.FileReadingTask> {

    private final static int NUMBER_OF_WORKERS = 15;
    public Boss(ActorContext<FileReadingTask> context) {
        super(context);
        worker = context.spawn(Pm.create(), "Pm");
    }

    public static class SayMyName {
        public final String name;

        public SayMyName(String name) {
            this.name = name;
        }
    }

    public static class FileReadingTask {
        public final String directoryPath;
        private List<String> pathList = new ArrayList<>();
        public final List<List<String>> tasks = new ArrayList<>(NUMBER_OF_WORKERS);

        public FileReadingTask(String directoryPath) {
            this.directoryPath = directoryPath;
            try {
                Utils.populateListOfPaths(pathList, directoryPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(pathList.toString());
        }
        
    }


    private final ActorRef<Pm.Order> worker;

    public static Behavior<FileReadingTask> create() {
        return Behaviors.setup(Boss::new);
    }

    @Override
    public Receive<FileReadingTask> createReceive() {
        // throw new UnsupportedOperationException("Unimplemented method
        // 'createReceive'");
        return newReceiveBuilder().onMessage(FileReadingTask.class, this::onStart).build();

    }

    private Behavior<FileReadingTask> onStart(FileReadingTask command) {
        // TODO qui manca qualcosa che nel format original e cera
        List<ActorRef<Pm.Ordered>> replyTo = IntStream.range(0, NUMBER_OF_WORKERS - 1).boxed().map(numberId -> 
            getContext().spawn(Employee.create(),Employee.class.getName() + String.valueOf(numberId))
        ).collect(Collectors.toList());
        worker.tell(new Pm.Order(command.directoryPath, replyTo));
        return this;
    }
}
