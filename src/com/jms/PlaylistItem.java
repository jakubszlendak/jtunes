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

    /**
     * Test constructor. Used to create dummy PlaylistItems. Just fills paramteres "by hand"
     * @param title
     * @param artist
     * @param album
     * @param genre
     * @param year
     * @param duration
     */
    public PlaylistItem(String title, String artist, String album, String genre, int year, int duration) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.year = year;
        this.duration = duration;
    }

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
//        return albumArt;
        return null;
    }

    public String getFilename() {
        return filename;
    }
}
