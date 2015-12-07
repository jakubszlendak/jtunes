package com.jms;

import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;
import org.farng.mp3.TagException;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {


        try
        {
            Editor editor = new Editor();
            MP3Player mp3Player = new MP3Player();
            Runnable task = () -> MainWindow.makeGUI(mp3Player, editor);
            javax.swing.SwingUtilities.invokeLater(task);

        } catch (JavaLayerException e)
        {
            e.printStackTrace();
        }


    }
}
