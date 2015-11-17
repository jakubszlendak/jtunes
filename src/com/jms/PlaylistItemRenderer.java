package com.jms;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by jakub on 17.11.15.
 * Renderer class of playlist item. Responsible for rendering content of playlist item in GUI
 */
public class PlaylistItemRenderer extends JPanel implements ListCellRenderer<PlaylistItem> {

    private JLabel description;
    private JLabel genre;
    private JLabel albumArt;
    private JLabel trackLength;

    private static ImageIcon noAlbumArtIcon = null;
    private static final String DEFAULT_ALBUM_ART_PATH = "noalbum.jpg";

    public PlaylistItemRenderer() {

        setOpaque(true);

        // Load default album art if not already loaded
        if(noAlbumArtIcon == null)
            noAlbumArtIcon = new ImageIcon(getScaledImage(new ImageIcon(DEFAULT_ALBUM_ART_PATH).getImage(), 40, 40));

        // Create render elements with default values
        description = new JLabel("Track");
        trackLength = new JLabel("0:00");
        genre = new JLabel("Not a music!");
        albumArt = new JLabel(noAlbumArtIcon);

        this.setLayout(new BorderLayout());
        add(description, BorderLayout.CENTER);
        add(albumArt, BorderLayout.LINE_START);
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
        String descriptionStr = "";
        if(value.getArtist() != null)
            descriptionStr = value.getArtist();
        if(value.getAlbum() != null)
            descriptionStr += " - " + value.getAlbum();
        if(value.getTitle() != null)
            descriptionStr += " - " + value.getTitle();

        // If no tags was given fill description with filename
        if(descriptionStr.equals(""))
            descriptionStr = value.getFilename();

        description.setText(descriptionStr);

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
                && dropLocation.getIndex() == index)
        {

            background = Color.BLUE;
            foreground = Color.WHITE;

            // check if this cell is selected
        } else if (isSelected)
        {
            background = Color.RED;
            foreground = Color.WHITE;

            // unselected, and not the DnD drop location
        } else
        {
            background = Color.WHITE;
            foreground = Color.BLACK;
        }

        setBackground(background);
        setForeground(foreground);

        return this;
    }

    /**
     * Resizes an image using a Graphics2D object backed by a BufferedImage.
     * @param srcImg - source image to scale
     * @param w - desired width
     * @param h - desired height
     * @return - the new resized image
     */
    public static Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }
}
