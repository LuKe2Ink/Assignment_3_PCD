/*
 * Created by JFormDesigner on Sun May 07 15:56:16 CEST 2023
 */

package it.unibo.ppc.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

import akka.actor.ActorSystem;
import akka.actor.ActorRef;
import it.unibo.ppc.akka.Boss;
import it.unibo.ppc.interfaces.MapWrapper;
import it.unibo.ppc.utilities.Continue;
import it.unibo.ppc.utilities.MapWrapperImplGUI;
import it.unibo.ppc.utils.Settings;

/**
 * @author luke3
 */
public class GUIResponsive extends JFrame implements ActionListener {

    private final Continue c = new Continue();
    private final ActorSystem main;
    private ActorRef boss;
    public GUIResponsive(final ActorSystem main) {
        this.main = main;
        this.initComponents();
    }

    private void initComponents() {
        this.listModel = new DefaultListModel();
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Luis
        this.start = new JButton();
        this.resume = new JButton();
        this.pause = new JButton();
        this.maxLine = new JTextField();
        this.interval = new JTextField();
        this.nfile = new JTextField();
        this.label1 = new JLabel();
        this.map = new JLabel();
        this.scrollPane1 = new JScrollPane();
        this.maxFiles = new JList(this.listModel);
        this.label3 = new JLabel();
        this.label4 = new JLabel();

        //======== this ========
        final var contentPane = this.getContentPane();

        //---- start ----
        this.start.setText("Start");

        //---- resume ----
        this.resume.setText("Resume");

        //---- pause ----
        this.pause.setText("Pause");

        //---- maxLine ----
        this.maxLine.setText("7450");

        //---- interval ----
        this.interval.setText("6");

        //---- nfile ----
        this.nfile.setText("3");

        //---- label1 ----
        this.label1.setText("Max Lines");

        //---- map ----
        this.map.setText("Mappa");

        //======== scrollPane1 ========
        {
            this.scrollPane1.setViewportView(this.maxFiles);
        }

        //---- label3 ----
        this.label3.setText("Intervals");

        //---- label4 ----
        this.label4.setText("N\u00b0 Files");

        final GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(19, 19, 19)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(this.label1)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                .addComponent(this.maxLine, GroupLayout.Alignment.LEADING)
                                .addComponent(this.label3, GroupLayout.Alignment.LEADING)
                                .addComponent(this.label4, GroupLayout.Alignment.LEADING)
                                .addComponent(this.nfile)
                                .addComponent(this.interval)
                                .addComponent(this.pause, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(this.resume, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(this.start, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGap(27, 27, 27)
                            .addComponent(this.map, GroupLayout.PREFERRED_SIZE, 316, GroupLayout.PREFERRED_SIZE)
                            .addGap(42, 42, 42)
                            .addComponent(this.scrollPane1, GroupLayout.PREFERRED_SIZE, 177, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(39, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(20, 20, 20)
                    .addComponent(this.label1)
                    .addGap(4, 4, 4)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(this.maxLine, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addComponent(this.label3)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(this.interval, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(this.label4)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(this.nfile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(this.start)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(this.resume)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(this.pause))
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addGap(1, 1, 1)
                                    .addComponent(this.scrollPane1, GroupLayout.PREFERRED_SIZE, 175, GroupLayout.PREFERRED_SIZE))))
                        .addComponent(this.map, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap(33, Short.MAX_VALUE))
        );
        this.pack();
        this.setLocationRelativeTo(this.getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        this.start.addActionListener(this);
        this.resume.addActionListener(this);
        this.pause.addActionListener(this);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final Object src = e.getSource();
        if (src==this.start){
            System.out.println("cliccato");
            final int maxLines = Integer.parseInt(this.maxLine.getText());
            final int intervals = Integer.parseInt(this.interval.getText());
            final int nFiles = Integer.parseInt(this.nfile.getText());
                // MapWrapper map = new MapWrapperImpl(settings);
            final MapWrapper map = new MapWrapperImplGUI(new Settings(maxLines, intervals, nFiles), this);
            System.out.println(this.main.toString());
            this.boss = this.main.actorOf(Boss.props("javaContainer", map ),"Boss");
            // SourceAnalyser analyser = (SourceAnalyser) new SourceAnalyserImpl(new Settings(maxLines, intervals, nFiles));
            // analyser.setMapWrapper(map);
            // analyser.setContinue(this.c);
            // AnalyzeFromSource analizza = new AnalyzeFromSource(analyser);
            // analizza.execute();
        } else if (src == this.pause){
            if(!this.c.isPaused()){
                this.c.pause();
                this.boss.tell(new Boss.StopMsg(), ActorRef.noSender());
            }
        } else if (src == this.resume) {
            this.c.resume();
        }
    }

    public void updateCountValue(final String value) {
        this.map.setText(value);
    }

    public void updateList(final List<String> paths){
        this.listModel.clear();
        this.listModel.addAll(paths);
    }

    // public class AnalyzeFromSource extends SwingWorker<Void, Float> {

    //     private SourceAnalyser analyser;

    //     public AnalyzeFromSource(SourceAnalyser analyser){
    //         this.analyser = analyser;
    //     }
    //     @Override
    //     protected Void doInBackground() throws Exception {
    //         this.analyser.analyzeSources("javaContainer");
    //         return null;
    //     }
    // }
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
