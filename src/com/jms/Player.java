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

    public enum PlayerState
    {
        STATE_STOPPED,
        STATE_PAUSED,
        STATE_PLAYING,
        STATE_NO_FILE
    }
    PlayerState state;

    public Player()
    {
        state = PlayerState.STATE_NO_FILE;
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
            state = PlayerState.STATE_STOPPED;
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
        state = PlayerState.STATE_PAUSED;
    }

    /**
     * Stops playback.
     */
    public void stop()
    {
        player.stop();
        state = PlayerState.STATE_STOPPED;
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
    public void play(File file)
    {
        if(state == PlayerState.STATE_PLAYING)
            return;
//        Thread worker = new Thread(() ->
//        {
            // Load file
            try
            {
                // Close if any file opened
                if(currently_played_file != null)
                    currently_played_file.close();

                currently_played_file = new FileInputStream(file);
            } catch (IOException e)
            {
                System.out.println(e.getMessage());
            }

            // Prepare player
            try
            {
                // Create player instance
                player = new AdvancedPlayer(currently_played_file);
                // Assign playback listener
                player.setPlayBackListener(getPlaybackListener());

                // Start playing depending on state
                if(state == PlayerState.STATE_PAUSED)
                {
                    state = PlayerState.STATE_PLAYING;
                    player.play(paused_on_frame);
                }
                else
                {
                    state = PlayerState.STATE_PLAYING;
                    player.play();
                }
            }catch(JavaLayerException e)
            {
                System.out.println(e.getMessage());
            }
//        });
//        worker.start();
    }

    public PlayerState getState() { return state; }

    /**
     * This function sets the currently_played_song to the next song on the playlist. It does playlist wrapping
     */
    private File getNextSong()
    {
        ///   If we are have not played recently the last song, then increment the current song index
        if(this.getPlaylist().getCurrentElementIndex() < this.getPlaylist().getSize() - 1)
            this.getPlaylist().incCurrentElementIndex();
        else    /**< Else, wrap the index and set it to the playlist start */
            this.getPlaylist().setCurrentElementIndex(0);

        /// Open the next file
        return playlist.getCurrentElement().getFile();
    }

    /**
     * This function randomizes the next song which is to be played
     */
    private File randomizeNextSong()
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

        /// Return chosen file
        return playlist.getCurrentElement().getFile();
    }

    /**
     * This function continuously reads songs from the playlist orderly and plays it
     */
    public void continuousOrderPlay()
    {
        /// Set initially the index to the playlist end, because the getNextSong method will wrap it to the start
        playlist.setCurrentElementIndex(this.getPlaylist().getSize() - 1);

        Thread t = new Thread( () ->
        {
            do
            {
                play(getNextSong());
            }while(state != PlayerState.STATE_STOPPED && state != PlayerState.STATE_PAUSED);
        });
        t.start();

    }

    /**
     * This function continuously randomizes songs from playlist and plays them
     */
    public void continuousRandomPlay()
    {
        Thread t = new Thread( () ->
        {
            do
            {
                play(randomizeNextSong());
            }while(state != PlayerState.STATE_STOPPED && state != PlayerState.STATE_PAUSED);
        });
        t.start();
    }

    private PlaybackListener getPlaybackListener(){
        return new PlaybackListener() {
            @Override
            public void playbackStarted(PlaybackEvent evt) {

            }

            @Override
            public void playbackFinished(PlaybackEvent evt) {
                paused_on_frame = evt.getFrame();
                /// Close file
                try {
                    currently_played_file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

}
