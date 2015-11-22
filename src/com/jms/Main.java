package com.jms;

import org.farng.mp3.TagException;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Player player = new Player();

        Runnable task = () -> MainWindow.makeGUI(player);
        javax.swing.SwingUtilities.invokeLater(task);
//        player.continuousRandomPlay();

    }
}
