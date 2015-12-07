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
//    private JTextField

    private JSlider sliderVolume;
    private JButton buttonCut;
    private JButton buttonVolume;
    private JButton buttonOpen;
    private JButton buttonSave;



    private Editor editor;
    public EditPanel(Editor editor)
    {
        this.editor = editor;
        editCutStartTime = new JTextField();
        editCutEndTime = new JTextField();
        buttonCut = new JButton("Cut song");
        buttonVolume = new JButton("Change volume");
        buttonSave = new JButton("Save file");
        buttonOpen = new JButton("Open file");
        sliderVolume = new JSlider(JSlider.HORIZONTAL);
        sliderVolume.setMaximum(100);
        sliderVolume.setMinimum(0);

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

        this.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 50));
        this.add(filePanel);
        this.add(cutEditPanel);
        this.add(volumePanel);

        buttonOpen.addActionListener(e1 -> {
            JFileChooser fc = new JFileChooser();
            int retval = fc.showOpenDialog(this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                editor.loadSong(file);
            }
        });

        buttonSave.addActionListener(e1 -> {
            JFileChooser fc = new JFileChooser();
            int retval = fc.showOpenDialog(this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                editor.saveSong(file.getAbsolutePath());
            }
        });

        buttonCut.addActionListener(e -> {
            int start, end;
            try {
                start = Integer.parseInt(editCutStartTime.getText());
                end = Integer.parseInt(editCutEndTime.getText());
                editor.cutSong(start, end);
            } catch (NumberFormatException ex) {
                editCutEndTime.setText("Please enter valid number");
                editCutStartTime.setText("Please enter valid number");
            }
        });

        buttonVolume.addActionListener(e -> {
            double factor = sliderVolume.getValue()/sliderVolume.getMaximum();
            editor.changeVolume(factor);
        });


    }
}
