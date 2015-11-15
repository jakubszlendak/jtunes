package com.jms;

public class Main {

    public static void main(String[] args) {
	// write your code here


        Runnable task = MainWindow::makeGUI;
        javax.swing.SwingUtilities.invokeLater(task);
    }
}
