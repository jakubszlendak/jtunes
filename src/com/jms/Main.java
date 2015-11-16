package com.jms;

public class Main {

    public static void main(String[] args) {
	// write your code here

        Playlist playlist = new Playlist();

        Runnable task = () -> MainWindow.makeGUI();
        javax.swing.SwingUtilities.invokeLater(task);
    }
}
