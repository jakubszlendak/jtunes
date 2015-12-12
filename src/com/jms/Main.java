package com.jms;

import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;
import org.farng.mp3.TagException;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        Editor editor = new Editor();
        editor.convertMP3ToWav("35 Hunt Or Be Hunted.mp3", "35 Hunt Or Be Hunted.wav");
        editor.loadSong(new File("35 Hunt Or Be Hunted.wav"));
        byte editedSong[] = editor.cutSong(5, 8);
        //editor.changeVolume(0.7);
        editor.saveSong("C:/Users/Konrad/Desktop/cutSong.wav", editedSong);
        try
        {
            MP3Player mp3Player = new MP3Player(/*new File("01_The_Trail.mp3")*/);
          //  mp3Player.getPlaylist().addPlaylistItem(new PlaylistItem(new File("01_The_Trail.mp3")));
            //mp3Player.getPlaylist().addPlaylistItem(new PlaylistItem(new File("35 Hunt Or Be Hunted.mp3")));
            //mp3Player.continuousOrderPlay();
            Runnable task = () -> MainWindow.makeGUI(mp3Player);
            javax.swing.SwingUtilities.invokeLater(task);

        } catch (JavaLayerException e)
        {
            e.printStackTrace();
        }


    }
}
