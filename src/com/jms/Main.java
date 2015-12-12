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

            editor.convertMP3ToWav("35 Hunt Or Be Hunted.mp3", "35 Hunt Or Be Hunted.wav");
            editor.loadSong(new File("35 Hunt Or Be Hunted.wav"));
            /// Pobierz referencje do tablicy z wycietym fragmentem piosenki
            //rawData= editor.cutSong(5, 8);
            editor.changeVolume(0.5);
            editor.muteSong(10, 12);
            /// Zapisz wyciety fragment
            editor.saveSong("C:/Users/Konrad/Desktop/cutSong.wav", editor.rawData);
            editor.convertWavToMP3("C:/Users/Konrad/Desktop/cutSong.wav", "C:/Users/Konrad/Desktop/cutSong.mp3");


            MP3Player mp3Player = new MP3Player();
            Runnable task = () -> MainWindow.makeGUI(mp3Player, editor);
            javax.swing.SwingUtilities.invokeLater(task);

        } catch (JavaLayerException e)
        {
            e.printStackTrace();
        }


    }
}
