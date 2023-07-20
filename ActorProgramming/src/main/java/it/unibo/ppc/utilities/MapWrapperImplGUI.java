package it.unibo.ppc.utilities;

import it.unibo.ppc.gui.GUIResponsive;
import it.unibo.ppc.gui.GUIResponsive;
import it.unibo.ppc.interfaces.MapWrapper;
import it.unibo.ppc.utils.RangeClass;
import it.unibo.ppc.utils.Settings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapWrapperImplGUI<T extends Number> implements MapWrapper {
    private Settings settings;
    private Map<String, Integer> listLongerFiles;
    private Map<RangeClass, T> map;

    private GUIResponsive guiResponsive;
    public MapWrapperImplGUI(Settings settings, GUIResponsive guiResponsive) {
        this.settings = settings;
        this.guiResponsive = guiResponsive;
        buildMap(settings.getIntervals(), settings.getMaxLines());
    }

    private void buildMap(int nInterval, int maxLines) {
        listLongerFiles = new HashMap<>();
        map = new HashMap<>();
        int mod = Math.abs(maxLines / (nInterval - 1));
        int m = 0;
        for (int i = 0; i < nInterval - 1; i++, m += mod) {
            map.put(new RangeClass((mod + m - 1), m), (T) Integer.valueOf(0));
        }
        map.put(new RangeClass(-1, maxLines), (T) Integer.valueOf(0));
        System.out.println(map);
    }


    @Override
    public void updateMap(Number data) {
        RangeClass k = map.keySet().stream().filter(x -> x.inRange((Integer) data)).collect(Collectors.toList()).get(0);//.map(p -> p.setValue(p.getValue() + 1));
        map.replace(k, this.increment(map.get(k)));
        guiResponsive.updateCountValue(this.fancyMap());
    }

    @Override
    public void updateList(String path, Number data) {
        listLongerFiles.putIfAbsent(path, (int)data);
        guiResponsive.updateList(this.getLongerFiles());
    }

    @Override
    public Map<RangeClass, T> getResultAsAMap() {
        return map;
    }

    @Override
    public List<String> getLongerFiles() {
        return listLongerFiles.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(this.settings.getnFiles())
                .map(Map.Entry::getKey)
                .map(path -> path.split("nested")[1])
                .collect(Collectors.toList());
    }

    @Override
    public String fancyMap() {
        String fancy = "<html>";
        for (RangeClass rc:
                this.map.keySet()) {
            fancy += "range " + rc.toString() + " has " + this.map.get(rc) + " files <br /><br />";
        }
        fancy += "<html>";
        return fancy;
    }

    @Override
    public T increment(Number value) {
        if (value instanceof Integer) {
            return (T) Integer.valueOf(value.intValue() + 1);
        } else if (value instanceof Long) {
            return (T) Long.valueOf(value.longValue() + 1L);
        } else if (value instanceof Float) {
            return  (T) Float.valueOf(value.floatValue() + 1.0f);
        } else if (value instanceof Double) {
            return  (T) Double.valueOf(value.doubleValue() + 1.0);
        } else {
            throw new UnsupportedOperationException("Type not supported");
        }
    }


}
