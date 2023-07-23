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
import it.unibo.ppc.akka.Employee.Report;
import it.unibo.ppc.interfaces.MapWrapper;
import it.unibo.ppc.utilities.Utils;
import it.unibo.ppc.utils.MapWrapperImpl;
import it.unibo.ppc.utils.Settings;

public class Boss extends AbstractBehavior<Employee.Report> {
    public final String directoryPath;
    private List<String> pathList = new ArrayList<>();
    public final Collection<List<String>> tasks;
    private Settings settings;
    private MapWrapper map; 

    private final static int NUMBER_OF_WORKERS = 7;

    public Boss(ActorContext<Employee.Report> context, String directoryPath, Settings settings) {
        super(context);
        worker = context.spawn(Pm.create(), "Pm");
        this.settings = settings;
        this.map =  new MapWrapperImpl(settings);
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

        List<ActorRef<Pm.Ordered>> replyTo = IntStream.range(0, NUMBER_OF_WORKERS - 1).boxed().map(
                numberId -> getContext().spawn(Employee.create(), Employee.class.getName() + String.valueOf(numberId)))
                .collect(Collectors.toList());
        worker.tell(new Pm.Order(this.directoryPath, replyTo, this.tasks));
    }

    public static class SayMyName {
        public final String name;

        public SayMyName(String name) {
            this.name = name;
        }
    }

    // public static class FileReadingTask {
    //     public final String directoryPath;
    //     private List<String> pathList = new ArrayList<>();
    //     public final Collection<List<String>> tasks;
    //     private Settings settings;
    //     private MapWrapper map; 

    //     public FileReadingTask(String directoryPath, Settings settings) {
    //         this.settings = settings;
    //         this.map =  new MapWrapperImpl(settings);
    //         this.directoryPath = directoryPath;
    //         try {
    //             Utils.populateListOfPaths(pathList, directoryPath);
    //         } catch (IOException e) {
    //             e.printStackTrace();
    //         }

    //         Collections.shuffle(pathList);

    //         int partitionSize = (int) pathList.size() / NUMBER_OF_WORKERS;
    //         this.tasks =  IntStream.range(0, pathList.size())
    //                 .boxed()
    //                 .collect(Collectors.groupingBy(partition -> (partition % partitionSize),
    //                         Collectors.mapping(elementIndex -> pathList.get(elementIndex), Collectors.toList())))
    //                 .values();
    //     }
    // }

    private final ActorRef<Pm.Order> worker;

    public static Behavior<Report> create(String directoryPath, Settings settings) {
        return Behaviors.setup(context -> new Boss(context, directoryPath, settings));
    }

    @Override
    public Receive<Report> createReceive() {
        // throw new UnsupportedOperationException("Unimplemented method
        // 'createReceive'");
        return newReceiveBuilder().onMessage(Report.class, this::onReport).build();

    }

    private Behavior<Report> onReport(Report command) {
        // TODO qui manca qualcosa che nel format original e cera
        // List<ActorRef<Pm.Ordered>> replyTo = IntStream.range(0, NUMBER_OF_WORKERS - 1).boxed().map(
        //         numberId -> getContext().spawn(Employee.create(), Employee.class.getName() + String.valueOf(numberId)))
        //         .collect(Collectors.toList());
        // worker.tell(new Pm.Order(this.directoryPath, replyTo, this.tasks));
        return this;
    }
}
