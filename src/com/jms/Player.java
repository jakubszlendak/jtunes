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

    public void pause()
    {
        player.stop();
        state = State.STATE_PAUSED;
    }

    public Playlist getPlaylist()
    {
        return playlist;
    }

    public void play()
    {
        if(state == State.STATE_PLAYING)
            return;

        if(state == State.STATE_PAUSED)
        {
            try
            {
                player.play(paused_on_frame);
                state = State.STATE_PLAYING;
            } catch (JavaLayerException e)
            {
                System.out.println(e.getMessage());
            }
        }
        else
        {
            try
            {
                player.play();
                state = State.STATE_PLAYING;
            } catch (JavaLayerException e)
            {
                System.out.print(e.getMessage());
            }

        }
    }

    public void getNextSong()
    {
        ///   If we are have not played recently the last song, then increment the current song index
        if(this.getPlaylist().getCurrentElementIndex() < this.getPlaylist().getSize() - 1)
            this.getPlaylist().incCurrentElementIndex();
        else    /**< Else, wrap the index and set it to the playlist start**/
            this.getPlaylist().setCurrentElementIndex(0);

        /// Open the next file
        this.openFile(this.getPlaylist().getElementAt(this.getPlaylist().getCurrentElementIndex()).getFile());
    }

    public void randomizeNextSong()
    {
        /// Randomize the index of the next song, accordingly to the list's size and set that index as the current
        // element index
        this.getPlaylist().setCurrentElementIndex((int)Math.random()%this.getPlaylist().getSize());

        /// Open the chosen fike
        this.openFile(this.getPlaylist().getElementAt(this.getPlaylist().getCurrentElementIndex()).getFile());
    }

    public void continuousPlay()
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

}
