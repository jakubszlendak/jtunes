package com.jms;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 *  Panel with editing controls
 */
public class EditPanel extends JPanel {
    private JTextField editCutStartTime;
    private JTextField editCutEndTime;
    private JTextField editMuteStartTime;
    private JTextField editMuteEndTime;

    private JTextField editFilename;
    private JTextField editFileLength;
    private JTextArea console;
//    private JTextField

    private JSlider sliderVolume;
    private JButton buttonMute;
    private JButton buttonVolume;
    private JButton buttonCut;
    private JButton buttonOpen;
    private JButton buttonSave;

    private StringBuffer log;


    private Editor editor;

    /**
     * Creates Edit panel
     * @param editor Instance of Editor
     */
    public EditPanel(Editor editor)
    {
        this.editor = editor;
        log = new StringBuffer();
        editCutStartTime = new JTextField();
        editCutEndTime = new JTextField();
        editMuteStartTime = new JTextField();
        editMuteEndTime = new JTextField();
        buttonMute = new JButton("Mute song");
        buttonMute.setEnabled(false);
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

        JPanel muteEditPanel = new JPanel(new GridLayout(4,2, 10, 20));
        muteEditPanel.add(new JLabel("Mute song fragment:"));
        muteEditPanel.add(new JLabel());
        muteEditPanel.add(new JLabel("Begin time [secs]: "));
        muteEditPanel.add(editMuteStartTime);
        muteEditPanel.add(new JLabel("End time [secs]: "));
        muteEditPanel.add(editMuteEndTime);
        muteEditPanel.add(new JLabel());
        muteEditPanel.add(buttonMute);

        JPanel cutEditPanel = new JPanel(new GridLayout(4,2, 10, 20));
        cutEditPanel.add(new JLabel("Cut song:"));
        cutEditPanel.add(new JLabel());
        cutEditPanel.add(new JLabel("Begin time [secs]: "));
        cutEditPanel.add(editCutStartTime);
        cutEditPanel.add(new JLabel("End time [secs]: "));
        cutEditPanel.add(editCutEndTime);
        cutEditPanel.add(new JLabel());
        cutEditPanel.add(buttonCut);

        JPanel volumePanel = new JPanel(new GridLayout(4,1, 10, 20));
        volumePanel.add(new JLabel("Change song volume:"));
        volumePanel.add(new JLabel("Set new volume in % of current volume:"));
        volumePanel.add(sliderVolume);
        volumePanel.add(buttonVolume);

        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        Dimension size = new Dimension(100, 100);
        sep.setMinimumSize(size);

        JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 50));
        mainPanel.add(filePanel);
        mainPanel.add(cutEditPanel);
        mainPanel.add(muteEditPanel);
        mainPanel.add(volumePanel);

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(2, 1));
        p.add(mainPanel);
        p.add(console);
        this.setLayout(new FlowLayout());
        this.add(p);

        //Setup action listeners

        buttonOpen.addActionListener(e1 -> {
            JFileChooser fc = new JFileChooser();
            int retval = fc.showOpenDialog(this);
            fc.setFileFilter(new FileNameExtensionFilter("MP3 and WAVE files.", "mp3", "wav", "wave"));
            if (retval == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();

                if (file.getName().endsWith("mp3")) {
                    editor.convertMP3ToWav(file.getAbsolutePath(), "temp.wav");
                    editor.loadSong(new File("temp.wav"));
                    log.append("File converted from MP3 to WAVE format.");
                    console.setText(log.toString());
                }
                else
                    editor.loadSong(file);
                buttonMute.setEnabled(true);
                buttonVolume.setEnabled(true);
                buttonCut.setEnabled(true);
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
                File temp = new File("temp.wav");
                if(temp.exists())
                    temp.delete();
                buttonMute.setEnabled(false);
                buttonVolume.setEnabled(false);
                buttonCut.setEnabled(false);
                log.append("Saved file: " + file.getAbsolutePath() + "\n");
                console.setText(log.toString());
            }
        });

        buttonMute.addActionListener(e -> {
            int start, end;
            try {
                start = Integer.parseInt(editMuteStartTime.getText());
                end = Integer.parseInt(editMuteEndTime.getText());
                editor.muteSong(start, end);
                log.append(String.format("Song muted: Start: %d, end: %d \n", start, end));
                console.setText(log.toString());
            } catch (NumberFormatException ex) {
                editMuteEndTime.setText("Invalid number");
                editMuteStartTime.setText("Invalid number");
                log.append("Please enter valid time\n");
                console.setText(log.toString());
            }
        });

        buttonCut.addActionListener(e1 -> {
            int start, end;
            try {
                start = Integer.parseInt(editCutStartTime.getText());
                end = Integer.parseInt(editCutEndTime.getText());
                byte result[] = editor.cutSong(start, end);
                editor.saveSong("temp.wav", result);
                editor.loadSong(new File("temp.wav"));
                log.append(String.format("Song muted: Start: %d, end: %d \n", start, end));
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
