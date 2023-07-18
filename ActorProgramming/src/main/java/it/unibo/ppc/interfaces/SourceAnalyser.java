package it.unibo.ppc.interfaces;


import it.unibo.ppc.utilities.Continue;
import it.unibo.ppc.utilities.Flag;
import it.unibo.ppc.utilities.MapWrapperImplGUI;

public interface SourceAnalyser {

    void setFlag(Flag flag);

    void setMapWrapper(MapWrapperImplGUI map);

    void setContinue(Continue c);

    void getReport(String directoryPath);

    void analyzeSources(String directoryPath);
}
