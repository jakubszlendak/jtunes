package com.jms;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Created by jakub on 07.12.15.
 */
public class EditPanel extends JPanel {
    private JTextField editCutStartTime;
    private JTextField editCutEndTime;

    private JTextField editFilename;
    private JTextField editFileLength;
    private JTextArea console;
//    private JTextField

    private JSlider sliderVolume;
    private JButton buttonCut;
    private JButton buttonVolume;
    private JButton buttonOpen;
    private JButton buttonSave;

    private StringBuffer log;


    private Editor editor;
    public EditPanel(Editor editor)
    {
        this.editor = editor;
        log = new StringBuffer();
        editCutStartTime = new JTextField();
        editCutEndTime = new JTextField();
        buttonCut = new JButton("Cut song");
        buttonCut.setEnabled(false);
        buttonVolume = new JButton("Change volume");
        buttonVolume.setEnabled(false);
        buttonSave = new JButton("Save file");
        buttonOpen = new JButton("Open file");
        sliderVolume = new JSlider(JSlider.HORIZONTAL);
        sliderVolume.setMaximum(100);
        sliderVolume.setMinimum(0);

        console = new JTextArea();

        JPanel filePanel = new JPanel(new GridLayout(3,1,20,20));
        filePanel.add(new JLabel("File manipulation"));
        filePanel.add(buttonOpen);
        filePanel.add(buttonSave);

        JPanel cutEditPanel = new JPanel(new GridLayout(3,2, 10, 20));
        cutEditPanel.add(new JLabel("Begin time [secs]: "));
        cutEditPanel.add(editCutStartTime);
        cutEditPanel.add(new JLabel("End time [secs]: "));
        cutEditPanel.add(editCutEndTime);
        cutEditPanel.add(new JLabel());
        cutEditPanel.add(buttonCut);

        JPanel volumePanel = new JPanel(new GridLayout(3,1, 10, 20));
        volumePanel.add(new JLabel("Set new volume in % of current volume:"));
        volumePanel.add(sliderVolume);
        volumePanel.add(buttonVolume);

        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        Dimension size = new Dimension(100, 100);
        sep.setMinimumSize(size);

        JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 50));
        mainPanel.add(filePanel);
        mainPanel.add(cutEditPanel);
        mainPanel.add(volumePanel);

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(2,1));
        p.add(mainPanel);
        p.add(console);
        this.setLayout(new FlowLayout());
        this.add(p);

        //Setup action listeners

        buttonOpen.addActionListener(e1 -> {
            JFileChooser fc = new JFileChooser();
            int retval = fc.showOpenDialog(this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                editor.loadSong(file);
                buttonCut.setEnabled(true);
                buttonVolume.setEnabled(true);
                log.append("Loaded file: " + file.getAbsolutePath() + "\n");
                console.setText(log.toString());
            }
        });

        buttonSave.addActionListener(e1 -> {
            JFileChooser fc = new JFileChooser();
            int retval = fc.showSaveDialog(this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                editor.saveSong(file.getAbsolutePath());
                buttonCut.setEnabled(false);
                buttonVolume.setEnabled(false);
                log.append("Saved file: "+ file.getAbsolutePath() + "\n");
                console.setText(log.toString());
            }
        });

        buttonCut.addActionListener(e -> {
            int start, end;
            try {
                start = Integer.parseInt(editCutStartTime.getText());
                end = Integer.parseInt(editCutEndTime.getText());
                editor.cutSong(start, end);
                log.append(String.format("Song cut. Start: %d, end: %d \n", start, end));
                console.setText(log.toString());
            } catch (NumberFormatException ex) {
                editCutEndTime.setText("Invalid number");
                editCutStartTime.setText("Invalid number");
                log.append("Please enter valid time\n");
                console.setText(log.toString());
            }
        });

        buttonVolume.addActionListener(e -> {
            double factor = sliderVolume.getValue()/sliderVolume.getMaximum();
            editor.changeVolume(factor);
            log.append("Volume changed to ");
            log.append(sliderVolume.getValue());
            log.append("% of input volume.\n");
            console.setText(log.toString());
        });


    }
}
