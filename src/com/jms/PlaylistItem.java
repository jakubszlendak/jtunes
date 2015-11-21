package com.jms;

import javax.swing.*;
import java.io.File;

/**
 * Created by jakub on 16.11.15.
 */
public class PlaylistItem {
    File file;

    private String title;
    private String artist;
    private String album;
    private String genre;
    private int year;
    private int duration;
    private String filename;
    private ImageIcon albumArt;

    /**
     * Test constructor. Used to create dummy PlaylistItems. Just fills paramteres "by hand"
     * @param title title
     * @param artist artist
     * @param album album
     * @param genre genre
     * @param year year
     * @param duration duration
     */
    public PlaylistItem(String title, String artist, String album, String genre, int year, int duration) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.year = year;
        this.duration = duration;
    }

    /**
     * Creates a playlist item from given file
     * @param file music file
     */
    public PlaylistItem(File file)
    {
        //TODO: Open file, extract metadata, fill properties
        //TODO: Try to find album art
    }

    public String getTitle() { return title; }

    public String getArtist() { return artist; }

    public String getAlbum() {  return album;  }

    public String getGenre() {  return genre;  }

    public int getYear() { return year; }

    public int getDuration() { return duration; }

    public ImageIcon getAlbumArt() {
        return albumArt;
    }

    public String getFilename() {
        return filename;
    }
}
