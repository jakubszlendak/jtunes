package com.jms;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Konrad on 2015-11-20.
 */
public class Player
{
    private FileInputStream  currently_played_file;
    private AdvancedPlayer   player;

    private Playlist playlist;

    private int paused_on_frame = 0;

    private enum State
    {
        STATE_STOPPED,
        STATE_PAUSED,
        STATE_PLAYING,
        STATE_NO_FILE
    }
    State state;

    public Player()
    {
        state = State.STATE_NO_FILE;
        playlist = new Playlist();
    }

    /**
     * Sets the .mp3 file which is about to be played by the player and creates FileInputStream for it
     * @param file - the file which is to be played
     */
    public void openFile(File file)
    {
        try
        {
            if(currently_played_file != null)
                currently_played_file.close();

            currently_played_file = new FileInputStream(file);
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
        }

        try
        {
            player = new AdvancedPlayer(currently_played_file);
            player.setPlayBackListener(new PlaybackListener()
            {
                @Override
                public void playbackFinished(PlaybackEvent evt)
                {
                    paused_on_frame = evt.getFrame();
                }
            });
            state = State.STATE_STOPPED;
        }catch(JavaLayerException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public void openPlaylistItem(int index)
    {
        try
        {
            openFile(playlist.getElementAt(index).getFile());
        } catch (Exception e)
        {
            //pass
        }
    }

    /**
     * This function pauses the song playing and stores information on which frame pause happened. It will be used to
     * start playing the song from the frame it was paused
     */
    public void pause()
    {
        player.stop();
        state = State.STATE_PAUSED;
    }

    /**
     * This is the getter for player's playlist
     * @return playlist
     */
    public Playlist getPlaylist()
    {
        return playlist;
    }

    /**
     * This function does automatically play the song from the beginning or from the frame it was paused on.
     */
    public void play()
    {
        if(state == State.STATE_PLAYING)
            return;
        Thread worker = new Thread(() ->
        {
            try
            {
                if(state == State.STATE_PAUSED)
                {
                    state = State.STATE_PLAYING;
                    player.play(paused_on_frame);
                }
                else
                {
                    state = State.STATE_PLAYING;
                    player.play();
                }

            } catch (JavaLayerException e)
            {
                e.printStackTrace();
            }
        });
        worker.start();
    }

    /**
     * This function sets the currently_played_song to the next song on the playlist. It does playlist wrapping
     */
    private void getNextSong()
    {
        ///   If we are have not played recently the last song, then increment the current song index
        if(this.getPlaylist().getCurrentElementIndex() < this.getPlaylist().getSize() - 1)
            this.getPlaylist().incCurrentElementIndex();
        else    /**< Else, wrap the index and set it to the playlist start**/
            this.getPlaylist().setCurrentElementIndex(0);

        /// Open the next file
        this.openFile(this.getPlaylist().getElementAt(this.getPlaylist().getCurrentElementIndex()).getFile());
    }

    /**
     * This function randomizes the next song which is to be played
     */
    private void randomizeNextSong()
    {
        /// Randomize the index of the next song, accordingly to the list's size and set that index as the current
        // element index
        int random =0;
        try
        {
            random = (int) (Math.random() * Integer.MAX_VALUE) % this.getPlaylist().getSize();
        } catch (ArithmeticException e)
        {

        }
        this.getPlaylist().setCurrentElementIndex(random);

        /// Open the chosen fike
        this.openFile(this.getPlaylist().getElementAt(this.getPlaylist().getCurrentElementIndex()).getFile());
    }

    /**
     * This function continuously reads songs from the playlist orderly and plays it
     */
    public void continuousOrderlyPlay()
    {
        /// Set initially the index to the playlist end, because the getNextSong method will wrap it to the start
        this.getPlaylist().setCurrentElementIndex(this.getPlaylist().getSize()-1);

        do
        {
            /// Get the next song from the playlist
            this.getNextSong();
            /// Play the song
            this.play();
        }while(true);
    }

    /**
     * This function continuously randomizes songs from playlist and plays them
     */
    public void continuousRandomPlay()
    {
        do
        {
            /// Randomize the next song from the playlist
            this.randomizeNextSong();
            /// Play the song
            this.play();
        }while(true);
    }

}
