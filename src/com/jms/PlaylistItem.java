package com.jms;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

import org.farng.mp3.MP3File;
import org.farng.mp3.id3.ID3v1;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;

/**
 * Created by jakub on 16.11.15.
 */
public class PlaylistItem {
    File file;
    MP3File mp3File;
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
    public PlaylistItem(String title, String artist, String album, String genre, int year, int duration)
    {
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
    public PlaylistItem(File file) throws IOException, TagException
    {
        filename = file.getName();
        this.file = file;
        mp3File = new MP3File(file);
        if(mp3File.hasID3v1Tag())
        {
            ID3v1 tag = mp3File.getID3v1Tag();
            title = tag.getTitle();
            artist =tag.getArtist();
            album = tag.getAlbum();
            genre = tag.getSongGenre();
            year = Integer.parseInt(tag.getYear());
        }
        else if(mp3File.hasID3v2Tag())
        {
            AbstractID3v2 tag = mp3File.getID3v2Tag();
            title = tag.getSongTitle();
            artist =tag.getLeadArtist();
            album = tag.getAlbumTitle();
            genre = tag.getSongGenre();
            year = Integer.parseInt(tag.getYearReleased());
        }
        else
        {
            title = null;
            artist = null;
            album = null;
            genre = null;
            year = 0;
        }


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
