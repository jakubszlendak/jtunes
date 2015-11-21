package com.jms;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        Playlist playlist = new Playlist();
        //TODO: test code
        playlist.addPlaylistItem(new PlaylistItem("Nothing else matters", "Metallica", "Black album", "Metal", 1000, 1000));
        playlist.addPlaylistItem(new PlaylistItem("aaaa", "bdagds", "ccvx", "d", 1000, 1000));
        playlist.addPlaylistItem(new PlaylistItem("abbbb", "bsad", "cxcvxv", "d", 1000, 1000));

        Runnable task = () -> MainWindow.makeGUI(playlist);
        javax.swing.SwingUtilities.invokeLater(task);

        Player player = new Player();
        player.getPlaylist().addPlaylistItem(new PlaylistItem(new File("01_The_Trail.mp3")));
        player.getPlaylist().addPlaylistItem(new PlaylistItem(new File("35 Hunt Or Be Hunted.mp3")));
       /* player.openFile(player.getPlaylist().getElementAt(0).getFile());
        player.play();*/
        player.continuousPlay();

        player.
    }
}
