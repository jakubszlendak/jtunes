package com.jms;

import javazoom.jl.decoder.JavaLayerException;
import org.farng.mp3.TagException;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        try
        {
            MP3Player mp3Player = new MP3Player(new File("01_The_Trail.mp3"));
            Runnable task = () -> MainWindow.makeGUI(mp3Player);
            javax.swing.SwingUtilities.invokeLater(task);
            mp3Player.playSong(0, Integer.MAX_VALUE, new File("01_The_Trail.mp3"));
            try
            {
                mp3Player.getPlaylist().addPlaylistItem(new PlaylistItem(new File("01_The_Trail.mp3")));
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (TagException e)
            {
                e.printStackTrace();
            }
            try
            {
                mp3Player.getPlaylist().addPlaylistItem(new PlaylistItem(new File("35 Hunt Or Be Hunted.mp3")));
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (TagException e)
            {
                e.printStackTrace();
            }
        } catch (JavaLayerException e)
        {
            e.printStackTrace();
        }

        /*player.openFile(new File("01_The_Trail.mp3"));
        try
        {
            player.getPlayer().play(0, Integer.MAX_VALUE);
        } catch (JavaLayerException e)
        {
            e.printStackTrace();
        }*/
        //  player.play(new File("01_The_Trail.mp3"));


    }
}
