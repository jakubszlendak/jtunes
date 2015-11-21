package com.jms;
import org.farng.mp3.TagException;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.IOException;

/**
 * Created by jakub on 15.11.15.
 */
public class MainWindow extends JPanel
{
    private final static String ICON_PATH = "img/";
    private final static String PLAY_ICON_PATH  = ICON_PATH +"play.jpg";
    private final static String PAUSE_ICON_PATH = ICON_PATH +"pause.jpg";
    private final static String STOP_ICON_PATH  = ICON_PATH +"stop.jpg";
    private final static String NEXT_ICON_PATH  = ICON_PATH +"next.jpg";
    private final static String PREV_ICON_PATH  = ICON_PATH +"prev.jpg";
    private final static String LOAD_ICON_PATH  = ICON_PATH +"load.jpg";
    private final static String SHUFFLE_ICON_PATH = ICON_PATH +"shuffle.jpg";

    private final static String ITEM_UP_ICON_PATH = ICON_PATH +"up.png";
    private final static String ITEM_DOWN_ICON_PATH = ICON_PATH +"down.png";
    private final static String ITEM_DEL_ICON_PATH = ICON_PATH +"del.png";

    private final static int BUTTON_ICON_SIZE = 30;

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


    private boolean editModeEnabled = false;

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
                playlistDisplay.setSelectedIndex(e.getIndex0());
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                playlistDisplay.repaint();
                playlistDisplay.clearSelection();

            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                playlistDisplay.repaint();
//                if(e.getIndex0()>e.getIndex1())
//                    playlistDisplay.setSelectedIndex(e.getIndex0());
//                else
                    playlistDisplay.setSelectedIndex(e.getIndex1());
            }
        });
        playlistDisplay.setModel(playlist);

    }

    private void setupPlaylistToolbar() {
        itemUpButton = new JButton(new ImageIcon(PlaylistItemRenderer.getScaledImage(new ImageIcon(ITEM_UP_ICON_PATH).getImage(), BUTTON_ICON_SIZE, BUTTON_ICON_SIZE)));
        itemUpButton.setToolTipText("Move one position up");
        itemDownButton = new JButton(new ImageIcon(PlaylistItemRenderer.getScaledImage(new ImageIcon(ITEM_DOWN_ICON_PATH).getImage(), BUTTON_ICON_SIZE, BUTTON_ICON_SIZE)));
        itemDownButton.setToolTipText("Move one position down");
        removeItemButton = new JButton(new ImageIcon(PlaylistItemRenderer.getScaledImage(new ImageIcon(ITEM_DEL_ICON_PATH).getImage(), BUTTON_ICON_SIZE, BUTTON_ICON_SIZE)));
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

        removeItemButton.addActionListener(e -> {
            int index = playlistDisplay.getSelectedIndex();
            if(index<0) return;
            playlist.removePlaylistItem(index);
        });

        playlistToolbar = new JToolBar("Playlsit toolbar", JToolBar.VERTICAL);
        playlistToolbar.add(itemUpButton);
        playlistToolbar.add(itemDownButton);
        playlistToolbar.add(removeItemButton);
        playlistToolbar.setFloatable(false);
    }

    private void setupPlaybackButtons()
    {
        playButton = new JButton(new ImageIcon(PlaylistItemRenderer.getScaledImage(new ImageIcon(PLAY_ICON_PATH).getImage(), BUTTON_ICON_SIZE, BUTTON_ICON_SIZE)));
        playButton.setToolTipText("Play");

        pauseButton = new JButton(new ImageIcon(PlaylistItemRenderer.getScaledImage(new ImageIcon(PAUSE_ICON_PATH).getImage(), BUTTON_ICON_SIZE, BUTTON_ICON_SIZE)));
        pauseButton.setToolTipText("Pause");

        stopButton = new JButton(new ImageIcon(PlaylistItemRenderer.getScaledImage(new ImageIcon(STOP_ICON_PATH).getImage(), BUTTON_ICON_SIZE, BUTTON_ICON_SIZE)));
        stopButton.setToolTipText("Stop");

        nextButton = new JButton(new ImageIcon(PlaylistItemRenderer.getScaledImage(new ImageIcon(NEXT_ICON_PATH).getImage(), BUTTON_ICON_SIZE, BUTTON_ICON_SIZE)));
        nextButton.setToolTipText("Next track");;

        prevButton = new JButton(new ImageIcon(PlaylistItemRenderer.getScaledImage(new ImageIcon(PREV_ICON_PATH).getImage(), BUTTON_ICON_SIZE, BUTTON_ICON_SIZE)));
        prevButton.setToolTipText("Previous track");

        randomButton = new JButton(new ImageIcon(PlaylistItemRenderer.getScaledImage(new ImageIcon(SHUFFLE_ICON_PATH).getImage(), BUTTON_ICON_SIZE, BUTTON_ICON_SIZE)));
        randomButton.setToolTipText("Play random track");

        loadButton = new JButton(new ImageIcon(PlaylistItemRenderer.getScaledImage(new ImageIcon(LOAD_ICON_PATH).getImage(), BUTTON_ICON_SIZE, BUTTON_ICON_SIZE)));
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
            if(editModeEnabled){
                cl.show(mainPanel, PLAYLIST_PANEL);
                editModeEnabled = false;
                editButton.setText("Playlist");
            } else {
                cl.show(mainPanel, EDITOR_PANEL);
                editModeEnabled = true;
                editButton.setText("Editor");
            }

        });

        loadButton.addActionListener(e ->
        {
            final JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("MP3 and WAVE files.", "mp3", "wav", "wave"));
            int retval = fc.showOpenDialog(this);
            if(retval == JFileChooser.APPROVE_OPTION)
            {
                try {
                    playlist.addPlaylistItem(new PlaylistItem(fc.getSelectedFile()));
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(this, "Error when opening file: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (TagException e1) {
                    JOptionPane.showMessageDialog(this, "Error when reading tags: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
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
