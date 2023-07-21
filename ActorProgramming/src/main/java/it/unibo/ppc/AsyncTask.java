package it.unibo.ppc;

import it.unibo.ppc.gui.GUIResponsive;
import it.unibo.ppc.gui.SourceAnalyserImpl;
import it.unibo.ppc.interfaces.SourceAnalyser;
import it.unibo.ppc.utils.Settings;

import javax.swing.*;

public class AsyncTask {
    public static void main(String[] args) {

//        final int maxLines = Integer.parseInt(args[0]);//7450;//Integer.parseInt(this.maxLinesField.getText());
//        final int intervals = Integer.parseInt(args[1]); //6;//Integer.parseInt(this.numberIntervalField.getText());
//        final int nFiles = Integer.parseInt(args[2]);//3;

        if(Boolean.valueOf(args[0])){
             JFrame frame = new GUIResponsive();
             frame.pack();
             frame.setVisible(true);
        }else {
            SourceAnalyser analyser = new SourceAnalyserImpl(new Settings(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3])));
            analyser.getReport("javaContainer");
            System.exit(0);
        }
//        analyser.analyzeSources("javaContainer");
        // System.out.println("hello from async task");
    }
}
