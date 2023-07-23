package it.unibo.ppc.akka;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import it.unibo.ppc.utilities.Utils;

public class Boss extends AbstractBehavior<Boss.FileReadingTask> {

    private final static int NUMBER_OF_WORKERS = 7;

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
        public final Collection<List<String>> tasks;

        public FileReadingTask(String directoryPath) {
            this.directoryPath = directoryPath;
            try {
                Utils.populateListOfPaths(pathList, directoryPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Collections.shuffle(pathList);

            int partitionSize = (int) pathList.size() / NUMBER_OF_WORKERS;
            this.tasks =  IntStream.range(0, pathList.size())
                    .boxed()
                    .collect(Collectors.groupingBy(partition -> (partition % partitionSize),
                            Collectors.mapping(elementIndex -> pathList.get(elementIndex), Collectors.toList())))
                    .values();
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
        List<ActorRef<Pm.Ordered>> replyTo = IntStream.range(0, NUMBER_OF_WORKERS - 1).boxed().map(
                numberId -> getContext().spawn(Employee.create(), Employee.class.getName() + String.valueOf(numberId)))
                .collect(Collectors.toList());
        worker.tell(new Pm.Order(command.directoryPath, replyTo, command.tasks));
        return this;
    }
}
