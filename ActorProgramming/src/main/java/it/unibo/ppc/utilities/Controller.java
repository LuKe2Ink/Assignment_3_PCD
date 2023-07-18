package it.unibo.ppc.utilities;

import it.unibo.pc.gui.GUIResponsive;
import it.unibo.ppc.utils.RangeClass;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Controller {
    private Map<RangeClass, Integer> map;
    int nInterval;
    int maxLines;

    GUIResponsive gui;

    public Controller(GUIResponsive gui, int nInterval, int maxLines) {
        this.nInterval = nInterval;
        this.maxLines = maxLines;
        this.gui = gui;
        makeResult();
    }

    public void makeResult(){
        map = new HashMap<>();
        int mod = Math.abs(maxLines / (nInterval - 1));
        int m = 0;
        for (int i = 0; i < nInterval - 1; i++, m += mod) {
            map.put(new RangeClass((mod + m - 1), m), 0);
        }
        map.put(new RangeClass(-1, maxLines), 0);
        System.out.println(map);
    }

    public void updateMap(Integer lines){
        RangeClass k = map.keySet().stream().filter(x -> x.inRange(lines)).collect(Collectors.toList()).get(0);//.map(p -> p.setValue(p.getValue() + 1));
        map.replace(k, map.get(k) + 1);
        gui.updateCountValue(getLuis());
    }

    public String getLuis() {
        return map.toString();
    }

}
