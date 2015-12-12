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
            byte editedSong[] = editor.cutSong(5, 8);
            //editor.changeVolume(0.7);
            /// Zapisz wyciety fragment
            editor.saveSong("C:/Users/Konrad/Desktop/cutSong.wav", editedSong);



            MP3Player mp3Player = new MP3Player();
            Runnable task = () -> MainWindow.makeGUI(mp3Player, editor);
            javax.swing.SwingUtilities.invokeLater(task);

        } catch (JavaLayerException e)
        {
            e.printStackTrace();
        }


    }
}
