package it.unibo.ppc.utilities;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import it.unibo.ppc.utilities.Utils.*;
import it.unibo.ppc.interfaces.MapWrapper;

import static it.unibo.ppc.utilities.Utils.linesWithBufferInputStream;

public class FileService extends Thread {
    private ExecutorService executor;

    private MapWrapper map;
    public FileService(MapWrapper map, int pool){
        executor = Executors.newFixedThreadPool(pool);
        this.map = map;
    }

    public void compute(List<String> path) throws InterruptedException {

        List<Future<Pair<String, Integer>>> results = new LinkedList<>();

        results = path.stream().map(pathFile -> executor.submit(() -> linesWithBufferInputStream(pathFile))).collect(Collectors.toList());

        for (Future<Pair<String, Integer>> res: results) {
            try {
                Pair<String, Integer> response = res.get();
                map.updateMap(response.getValue());
                map.updateList(response.getKey(), response.getValue());
//                log(map.fancyMap());
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
