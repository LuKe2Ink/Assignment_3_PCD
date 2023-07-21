package it.unibo.ppc.gui;

import it.unibo.ppc.interfaces.SourceAnalyser;
import it.unibo.ppc.utilities.Continue;
import it.unibo.ppc.utilities.FileService;
import it.unibo.ppc.utilities.FileServiceStoppable;
import it.unibo.ppc.utilities.Flag;
import it.unibo.ppc.utilities.MapWrapperImplGUI;
import it.unibo.ppc.interfaces.MapWrapper;
import it.unibo.ppc.utils.MapWrapperImpl;
import it.unibo.ppc.utils.Settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static it.unibo.ppc.utilities.Utils.*;

public class SourceAnalyserImpl implements SourceAnalyser {

    private Settings settings;

    public SourceAnalyserImpl(Settings settings){
        this.settings = settings;
    }

    @Override
    public synchronized void getReport(String directoryPath) {

        List<String> pathList = new ArrayList<>();
        try {
            populateListOfPaths(pathList, directoryPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Collections.shuffle(pathList);


        MapWrapper<Integer> mapWrapper = new MapWrapperImpl<>(settings);

        FileService fs = new FileService(mapWrapper, 15);

        try {
            fs.compute(pathList);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        log("Fancy" + mapWrapper.fancyMap());
        log("Fancy List" + mapWrapper.getLongerFiles().toString());
    }

    private Flag flag;

    private MapWrapperImplGUI mapWrapper;

    private Continue c;


    public void setFlag(Flag flag) {
        this.flag = flag;
    }

    public void setMapWrapper(MapWrapperImplGUI map){
        this.mapWrapper = map;
    }

    public void setContinue(Continue c){this.c = c;}

    @Override
    public synchronized void analyzeSources(String directoryPath) {
        List<String> pathList = new ArrayList<>();
        try {
            populateListOfPaths(pathList, directoryPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Collections.shuffle(pathList); //! Fondamentale lo shuffle per testare......
        FileServiceStoppable fs = new FileServiceStoppable(this.mapWrapper, 5, this.c);

        try {
            fs.compute(pathList);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log("Fancy" + mapWrapper.fancyMap());

    }

}
