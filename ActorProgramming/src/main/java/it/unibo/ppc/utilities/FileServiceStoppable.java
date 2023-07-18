package it.unibo.ppc.utilities;



import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

import static it.unibo.ppc.utilities.Utils.linesWithBufferInputStream;

public class FileServiceStoppable extends Thread {
    private ExecutorService executor;

    private MapWrapperImplGUI map;

    private Controller controller;
    public FileServiceStoppable(MapWrapperImplGUI map, int pool, Continue c) {
        ThreadFactory tf = Executors.defaultThreadFactory();
        executor = new PausableExecutor(pool, tf, c);
        this.map = map;
    }

    public void compute(List<String> path) throws InterruptedException {

        List<Future<Utils.Pair<String, Integer>>> results = new LinkedList<>();

        results = path.stream().map(pathFile -> executor.submit(() -> linesWithBufferInputStream(pathFile))).collect(Collectors.toList());

        for (Future<Utils.Pair<String, Integer>> res : results) {
            try {
//                Thread.sleep(1000);
                Utils.Pair<String, Integer> response = res.get();
                map.updateMap(response.getValue());
                map.updateList(response.getKey(), response.getValue());
//                map.updateMap(res.get());
//                log(map.fancyMap());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
