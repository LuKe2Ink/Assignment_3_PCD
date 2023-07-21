/*
 * Created by JFormDesigner on Sun May 07 15:56:16 CEST 2023
 */

package it.unibo.ppc.gui;

import it.unibo.ppc.interfaces.SourceAnalyser;
import it.unibo.ppc.utilities.Continue;
import it.unibo.ppc.utilities.Flag;
import it.unibo.ppc.utilities.MapWrapperImplGUI;
import it.unibo.ppc.utils.Settings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * @author luke3
 */
public class GUIResponsive extends JFrame implements ActionListener {

    private Continue c = new Continue();
    public GUIResponsive() {
        initComponents();
    }

    private void initComponents() {
        listModel = new DefaultListModel();
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Luis
        start = new JButton();
        resume = new JButton();
        pause = new JButton();
        maxLine = new JTextField();
        interval = new JTextField();
        nfile = new JTextField();
        label1 = new JLabel();
        map = new JLabel();
        scrollPane1 = new JScrollPane();
        maxFiles = new JList(listModel);
        label3 = new JLabel();
        label4 = new JLabel();

        //======== this ========
        var contentPane = getContentPane();

        //---- start ----
        start.setText("Start");

        //---- resume ----
        resume.setText("Resume");

        //---- pause ----
        pause.setText("Pause");

        //---- maxLine ----
        maxLine.setText("7450");

        //---- interval ----
        interval.setText("6");

        //---- nfile ----
        nfile.setText("3");

        //---- label1 ----
        label1.setText("Max Lines");

        //---- map ----
        map.setText("Mappa");

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(maxFiles);
        }

        //---- label3 ----
        label3.setText("Intervals");

        //---- label4 ----
        label4.setText("N\u00b0 Files");

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(19, 19, 19)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(label1)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                .addComponent(maxLine, GroupLayout.Alignment.LEADING)
                                .addComponent(label3, GroupLayout.Alignment.LEADING)
                                .addComponent(label4, GroupLayout.Alignment.LEADING)
                                .addComponent(nfile)
                                .addComponent(interval)
                                .addComponent(pause, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(resume, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(start, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGap(27, 27, 27)
                            .addComponent(map, GroupLayout.PREFERRED_SIZE, 316, GroupLayout.PREFERRED_SIZE)
                            .addGap(42, 42, 42)
                            .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 177, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(39, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(20, 20, 20)
                    .addComponent(label1)
                    .addGap(4, 4, 4)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(maxLine, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addComponent(label3)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(interval, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(label4)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(nfile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(start)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(resume)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(pause))
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addGap(1, 1, 1)
                                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 175, GroupLayout.PREFERRED_SIZE))))
                        .addComponent(map, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap(33, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        start.addActionListener(this);
        resume.addActionListener(this);
        pause.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src==start){
            System.out.println("cliccato");
            int maxLines = Integer.parseInt(maxLine.getText());
            int intervals = Integer.parseInt(interval.getText());
            int nFiles = Integer.parseInt(nfile.getText());
            SourceAnalyser analyser = (SourceAnalyser) new SourceAnalyserImpl(new Settings(maxLines, intervals, nFiles));
            MapWrapperImplGUI map = new MapWrapperImplGUI(new Settings(maxLines, intervals, nFiles), this);
            analyser.setMapWrapper(map);
            analyser.setContinue(this.c);
            AnalyzeFromSource analizza = new AnalyzeFromSource(analyser);
            analizza.execute();
        } else if (src == pause){
            if(!this.c.isPaused())
                this.c.pause();
        } else if (src == resume) {
            this.c.resume();
        }
    }

    public void updateCountValue(String value) {
        map.setText(value);
    }

    public void updateList(List<String> paths){
        this.listModel.clear();
        this.listModel.addAll(paths);
    }

    public class AnalyzeFromSource extends SwingWorker<Void, Float> {

        private SourceAnalyser analyser;

        public AnalyzeFromSource(SourceAnalyser analyser){
            this.analyser = analyser;
        }
        @Override
        protected Void doInBackground() throws Exception {
            this.analyser.analyzeSources("javaContainer");
            return null;
        }
    }
    DefaultListModel listModel;
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - Luis
    private JButton start;
    private JButton resume;
    private JButton pause;
    private JTextField maxLine;
    private JTextField interval;
    private JTextField nfile;
    private JLabel label1;
    private JLabel map;
    private JScrollPane scrollPane1;
    private JList maxFiles;
    private JLabel label3;
    private JLabel label4;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
