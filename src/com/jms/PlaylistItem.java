package com.jms;

import javax.swing.*;

/**
 * Created by jakub on 16.11.15.
 */
public class PlaylistItem {
    private String title;
    private String artist;
    private String album;
    private String genre;
    private int year;
    private int duration;
    private String filename;

    public PlaylistItem(String title, String artist, String album, String genre, int year, int duration) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.year = year;
        this.duration = duration;
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
