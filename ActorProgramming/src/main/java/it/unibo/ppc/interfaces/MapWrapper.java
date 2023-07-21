package it.unibo.ppc.interfaces;


import it.unibo.ppc.utils.RangeClass;

import java.util.List;
import java.util.Map;

public interface MapWrapper<T extends Number> {
    void updateMap(T data);

    void updateList(String path, T data);

    Map<RangeClass, T> getResultAsAMap();

    List<String> getLongerFiles();

    String fancyMap();

    T increment(T value);

}
