package com.jms;

import javax.swing.*;
import java.awt.*;

/**
 * Created by jakub on 17.11.15.
 * Renderer class of playlist item. Responsible for rendering content of playlist item in GUI
 */
public class PlaylistItemRenderer extends JPanel implements ListCellRenderer<PlaylistItem> {

    private JLabel artist;
    private JLabel album;
    private JLabel title;
    private JLabel genre;
    private JLabel albumArt;
    private JLabel trackLength;

    private static ImageIcon noAlbumArtIcon = null;
    private static final String DEFAULT_ALBUM_ART_PATH = "/";

    public PlaylistItemRenderer() {
        setOpaque(true);

        // Load default album art if not already loaded
        if(noAlbumArtIcon == null)
            noAlbumArtIcon = new ImageIcon(DEFAULT_ALBUM_ART_PATH);

        // Create render elements with default values
        artist = new JLabel("Unknown artist");
        album = new JLabel("Unknown album");
        title = new JLabel("Unknown title");
        trackLength = new JLabel("0:00");
        genre = new JLabel("Not a music!");
        albumArt = new JLabel(noAlbumArtIcon);

        this.setLayout(new BorderLayout());
        add(artist, BorderLayout.WEST);
        add(albumArt, BorderLayout.LINE_START);
        add(title, BorderLayout.EAST);
        add(album, BorderLayout.CENTER);
        add(trackLength, BorderLayout.LINE_END);
        add(genre, BorderLayout.PAGE_END);

    }

    /**
     * Renders list item
     * @param list Instance of list on which item will be rendered
     * @param value List item
     * @param index Item index
     * @param isSelected is item selected
     * @param cellHasFocus is item in focus
     * @return Rendered component.
     */
    @Override
    public Component getListCellRendererComponent(JList<? extends PlaylistItem> list, PlaylistItem value, int index, boolean isSelected, boolean cellHasFocus) {
        // Set values or leave defaults
        if(value.getArtist() != null)
            artist.setText(value.getArtist());
        if(value.getAlbum() != null)
            album.setText(value.getAlbum());
        if(value.getTitle() != null)
            title.setText(value.getTitle());
        if(value.getGenre() != null)
            genre.setText(value.getGenre());
        if(value.getArtist() != null)
            trackLength.setText(String.format("%02d:%02d", value.getDuration() / 60, value.getDuration() % 60));
        if(value.getAlbumArt() != null)
            albumArt.setIcon(value.getAlbumArt());

        Color background;
        Color foreground;

        // check if this cell represents the current DnD drop location
        JList.DropLocation dropLocation = list.getDropLocation();
        if (dropLocation != null
                && !dropLocation.isInsert()
                && dropLocation.getIndex() == index) {

            background = Color.BLUE;
            foreground = Color.WHITE;

            // check if this cell is selected
        } else if (isSelected) {
            background = Color.RED;
            foreground = Color.WHITE;

            // unselected, and not the DnD drop location
        } else {
            background = Color.WHITE;
            foreground = Color.BLACK;
        }

        setBackground(background);
        setForeground(foreground);

        return this;
    }
}
