package com.jms;

import javazoom.jl.decoder.JavaLayerException;
import org.farng.mp3.TagException;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Player player = new Player();

        Runnable task = () -> MainWindow.makeGUI(player);
        javax.swing.SwingUtilities.invokeLater(task);

        try
        {
            player.getPlaylist().addPlaylistItem(new PlaylistItem(new File("01_The_Trail.mp3")));
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (TagException e)
        {
            e.printStackTrace();
        }
        try
        {
            player.getPlaylist().addPlaylistItem(new PlaylistItem(new File("35 Hunt Or Be Hunted.mp3")));
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (TagException e)
        {
            e.printStackTrace();
        }
       // player.continuousOrderPlay();

        player.openFile(new File("01_The_Trail.mp3"));
        try
        {
            player.getPlayer().play(0, Integer.MAX_VALUE);
        } catch (JavaLayerException e)
        {
            e.printStackTrace();
        }
        //  player.play(new File("01_The_Trail.mp3"));


    }
}
