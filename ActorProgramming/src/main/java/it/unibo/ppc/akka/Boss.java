package it.unibo.ppc.akka;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.typed.Behavior;
import it.unibo.ppc.akka.Employee.Report;
import it.unibo.ppc.interfaces.MapWrapper;
import it.unibo.ppc.utilities.Utils;
import it.unibo.ppc.utils.MapWrapperImpl;
import it.unibo.ppc.utils.Settings;


public class Boss extends AbstractActor {
    private String directoryPath;
    private final List<String> pathList = new ArrayList<>();
    public Collection<List<String>> tasks;

    private MapWrapper map ;
    private final static int NUMBER_OF_WORKERS = 7;


    public static Props props(final String directoryPath, final MapWrapper map) {
        return Props.create(Boss.class, directoryPath, map);
    }

    private ActorRef worker;

    public Boss() {}

    public interface Message {}

    public static class StopMsg implements Message {}

    public static class ResumeMsg implements Message {}


    public Boss(final String directoryPath, final MapWrapper map) {
        // super(context);
        // worker = context.spawn(Pm.create(), "Pm");
        System.out.println("Boss created");
        this.worker = this.getContext().actorOf(Pm.props(getSelf()), "PM");
        // worker = getContext().getSystem().actorOf(Pm.props(), "PM");
        this.map =  map;
        this.directoryPath = directoryPath;
        try {
            Utils.populateListOfPaths(this.pathList, directoryPath);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("pathListL: " + this.pathList.size());

        final int partitionSize = (int) this.pathList.size() / NUMBER_OF_WORKERS;
        this.tasks =  IntStream.range(0, this.pathList.size())
                .boxed()
                .collect(Collectors.groupingBy(partition -> (partition % partitionSize),
                        Collectors.mapping(elementIndex -> this.pathList.get(elementIndex), Collectors.toList())))
                .values();

//        this.replyTo = IntStream.range(0, NUMBER_OF_WORKERS - 1).boxed().map(
//                numberId -> this.getContext().getSystem().actorOf(Employee.props(), String.valueOf(numberId)))
//                .collect(Collectors.toList());
//

        // List<ActorRef> replyTo = IntStream.range(0, NUMBER_OF_WORKERS - 1).boxed().map(
        //         numberId -> getContext().spawn(Employee.create(), Employee.class.getName() + String.valueOf(numberId)))
        //         .collect(Collectors.toList());
//        this.worker.tell(new Pm.Order(this.directoryPath, replyTo, this.tasks), this.getSelf());
        this.worker.tell(new Pm.Order(this.directoryPath, null, this.tasks), this.getSelf());

    }

    public static class SayMyName {
        public final String name;

        public SayMyName(final String name) {
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


    // public static Behavior<Report> create(String directoryPath, Settings settings) {
    //     return Behaviors.setup(context -> new Boss(context, directoryPath, settings));
    // }

    // @Override
    // public Receive<Report> createReceive() {
    //     // throw new UnsupportedOperationException("Unimplemented method
    //     // 'createReceive'");
    //     return newReceiveBuilder().onMessage(Report.class, this::onReport).build();

    // }

    private void onReport(final Report command) {
        // TODO qui manca qualcosa che nel format original e cera
        // List<ActorRef> replyTo = IntStream.range(0, NUMBER_OF_WORKERS - 1).boxed().map(
        //         numberId -> getContext().spawn(Employee.create(), Employee.class.getName() + String.valueOf(numberId)))
        //         .collect(Collectors.toList());
        // worker.tell(new Pm.Order(this.directoryPath, replyTo, this.tasks));
        // getContext().getLog().debug("recived report from {}", command.from);
        this.map.updateMap(command.response.getValue());
        this.map.updateList(command.response.getKey(), command.response.getValue());
        System.out.println(this.map.fancyMap());
        // return null;
        // System.out.println("Boss.onReport()");
    }

    private Behavior<Boss.StopMsg> onStopReceive(Boss.StopMsg msg) {
        System.out.println("Boss received stop");
        this.worker.tell(new Pm.StopMsg(), this.getSelf());
//        this.replyTo.forEach(employee -> employee.tell(new Employee.StopMsg(), this.getSelf()));

//        this.replyTo.forEach(replier -> replier.tell(new Employee.StopMsg(), getSelf()) );
        return null;
    }

    private Behavior<Boss.ResumeMsg> onResumeReceive(Boss.ResumeMsg msg) {
        System.out.println("Boss received resume");
        this.worker.tell(new Pm.ResumeMsg(), this.getSelf());
        return null;
    }

    @Override
    public Receive createReceive() {
        return this.receiveBuilder()
                .match(Boss.StopMsg.class, this::onStopReceive)
                .match(Boss.ResumeMsg.class, this::onResumeReceive)
                .match(Employee.Report.class, this::onReport)
        .build();
    }
}
