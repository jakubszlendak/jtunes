package com.jms;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

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

    //Playlist toolbar
    JToolBar playlistToolbar;
    JButton itemUpButton;
    JButton itemDownButton;
    JButton removeItemButton;

    // Track progress slider
    JSlider progressSlider;

    // Panels
    JPanel mainPanel;
    JPanel editPanel;
    JPanel playlistPanel;

    // Playlist widget
    JList playlistDisplay;

    // Program logic controllers
    Playlist playlist;

    public MainWindow(Playlist playlist)
    {
        // Superclass constructor call
        super(new BorderLayout());
        // Setup toolbar
        setupPlaybackButtons();
        setupSlider();
        setupPlaylistToolbar();

        // Setup main panel with card layout
        mainPanel = new JPanel(new CardLayout());

        editPanel = new JPanel();
        playlistPanel = new JPanel();


        JPanel toolbarPanel = new JPanel(new BorderLayout());
        toolbarPanel.add(playbackToolbar, BorderLayout.NORTH);
        toolbarPanel.add(progressSlider, BorderLayout.SOUTH);


        this.add(toolbarPanel, BorderLayout.NORTH);
        this.add(mainPanel, BorderLayout.CENTER);


        playlistDisplay = new JList();
        playlistDisplay.setCellRenderer(new PlaylistItemRenderer());
        playlistPanel.add(playlistDisplay);
        playlistPanel.add(playlistToolbar);

        mainPanel.add(playlistPanel, PLAYLIST_PANEL);
        mainPanel.add(editPanel, EDITOR_PANEL);

        this.playlist = playlist;
        playlist.addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
                playlistDisplay.repaint();
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                playlistDisplay.repaint();
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                playlistDisplay.repaint();
            }
        });
        playlistDisplay.setModel(playlist);

    }

    private void setupPlaylistToolbar() {
        itemUpButton = new JButton("Up");
        itemUpButton.setToolTipText("Move one position up");
        itemDownButton = new JButton("Down");
        itemDownButton.setToolTipText("Move one position down");
        removeItemButton = new JButton("Del");
        removeItemButton.setToolTipText("Delete item");

        itemUpButton.addActionListener(e -> {
            int index = playlistDisplay.getSelectedIndex();
            if (index < 0) return;
            // If selected index is first, do not replace
            if (index != 0)
                playlist.replaceItem(index, index-1);

        });
        itemDownButton.addActionListener(e -> {
            int index = playlistDisplay.getSelectedIndex();
            if(index < 0) return;
            // If selected index is last, do not replace
            if(index != playlist.getSize()-1)
                playlist.replaceItem(index, index+1);

        });

        playlistToolbar = new JToolBar("Playlsit toolbar", JToolBar.VERTICAL);
        playlistToolbar.add(itemUpButton);
        playlistToolbar.add(itemDownButton);
        playlistToolbar.add(removeItemButton);
        playlistToolbar.setFloatable(false);
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

        playbackToolbar.setFloatable(false);

        editButton.addActionListener(e ->
        {
            CardLayout cl = (CardLayout) mainPanel.getLayout();
            cl.show(mainPanel, EDITOR_PANEL);
        });

        loadButton.addActionListener(e ->
        {
            final JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("MP3 and WAVE files.", "mp3", "wav", "wave"));
            int retval = fc.showOpenDialog(this);
            if(retval == JFileChooser.APPROVE_OPTION)
            {
                playlist.addPlaylistItem(new PlaylistItem(fc.getSelectedFile()));
            }

        });
    }

    private void setupSlider(){
        progressSlider = new JSlider();
        progressSlider.addChangeListener(e -> {
            //TODO: Service slider clicking
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
