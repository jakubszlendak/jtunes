package com.jms;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by jakub on 15.11.15.
 */
public class MainWindow extends JPanel
{
    private final static String PLAYLIST_PANEL = "Playlist";
    private final static String EDITOR_PANEL = "Editor";

    // Playback toolbar
    JToolBar playbackToolbar;
    JButton playButton;
    JButton pauseButton;
    JButton stopButton;
    JButton nextButton;
    JButton prevButton;
    JButton randomButton;
    JButton loadButton;
    // Switch-to-edit button
    JButton editButton;

    // Panels
    JPanel mainPanel;
    JPanel editPanel;
    JPanel playlistPanel;

    // Playlist widget
    JList playList;

    // Program logic controllers
    Playlist playlist;

    public MainWindow(Playlist playlist)
    {
        // Superclass constructor call
        super(new BorderLayout());
        // Setup toolbar
        setupPlaybackButtons();

        // Setup main panel with card layout
        mainPanel = new JPanel(new CardLayout());

        editPanel = new JPanel();
        playlistPanel = new JPanel();

//        this.setLayout(new BorderLayout());
        this.add(playbackToolbar, BorderLayout.NORTH);
        this.add(mainPanel, BorderLayout.CENTER);


        playList = new JList();
        playList.setCellRenderer(new PlaylistItemRenderer());

        mainPanel.add(playList, PLAYLIST_PANEL);
        mainPanel.add(editPanel, EDITOR_PANEL);

        this.playlist = playlist;
        playList.setModel(playlist);

    }

    private void setupPlaybackButtons()
    {
        playButton = new JButton("Play");
        playButton.setToolTipText("Play");

        pauseButton = new JButton("Pause");
        pauseButton.setToolTipText("Pause");

        stopButton = new JButton("Stop");
        stopButton.setToolTipText("Stop");

        nextButton = new JButton("Next");
        nextButton.setToolTipText("Next track");;

        prevButton = new JButton("Prev");
        prevButton.setToolTipText("Previous track");

        randomButton = new JButton("Random");
        randomButton.setToolTipText("Play random track");

        loadButton = new JButton("Load");
        loadButton.setToolTipText("Load new track from filesystem");

        editButton = new JButton("Edit mode");
        editButton.setToolTipText("Switch to edit mode");

        playbackToolbar = new JToolBar("Playback toolbar", JToolBar.HORIZONTAL);

        playbackToolbar.add(playButton);
        playbackToolbar.add(pauseButton);
        playbackToolbar.add(stopButton);
        playbackToolbar.add(nextButton);
        playbackToolbar.add(prevButton);
        playbackToolbar.addSeparator();
        playbackToolbar.add(randomButton);
        playbackToolbar.addSeparator();
        playbackToolbar.add(loadButton);
        playbackToolbar.addSeparator();
        playbackToolbar.add(editButton);

        editButton.addActionListener(e ->
        {
            CardLayout cl = (CardLayout) mainPanel.getLayout();
            cl.show(mainPanel, EDITOR_PANEL);
        });
    }

    public static void makeGUI(Playlist playlist)
    {
        JFrame frame = new JFrame("jTunes");
        JComponent contentPane = new MainWindow(playlist);
        contentPane.setOpaque(true);
        frame.setContentPane(contentPane);
        frame.pack();
        frame.setVisible(true);
    }

}
