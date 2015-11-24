package com.jms;

import javazoom.jl.decoder.*;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;

import java.io.*;

/**
 * Created by Konrad on 2015-11-22.
 */
public class MP3Player extends AdvancedPlayer
{
    public enum PlayerState
    {
        STATE_STOPPED,
        STATE_PAUSED,
        STATE_PLAYING,
        STATE_NO_FILE,
        STATE_NEXT_SONG_REQUESTED,
        STATE_PREV_SONG_REQUESTED
    }
    private enum PlaybackOrder
    {
        PLAY_IN_ORDER,
        PLAY_RANDOM
    }
    private Playlist                playlist;               /**< Playlist from which songs are played **/

    private Equalizer               equalizer;              /**< Equalizer which is used to modify audio settings **/
    private File                    currentlyOpenedFile;    /**< Currently processed file **/
    private FileInputStream         stream;                 /**< Buffer from which audio frames are taken **/
    private int                     currentFrameNumber;     /**< Number of the currently decoded frame **/
    private int                     pausedOnFrame;          /**< Number of the frame which was decoded last when pause event came**/

    private PlayerState             state;
    private PlaybackOrder      randomOrInOrder = PlaybackOrder.PLAY_IN_ORDER;


    public MP3Player(File file) throws JavaLayerException
    {
        this.currentlyOpenedFile = file;
        try
        {
            this.stream = new FileInputStream(file);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        this.bitstream = new Bitstream(stream);
        try
        {
            audio = FactoryRegistry.systemRegistry().createAudioDevice();
        } catch (JavaLayerException e)
        {
            e.printStackTrace();
        }
        try
        {
            audio.open(decoder = new Decoder());
        } catch (JavaLayerException e)
        {
            e.printStackTrace();
        }

        this.playlist = new Playlist();
    }

    public void openFile(File file)
    {
        this.currentlyOpenedFile = file;
        try
        {
            this.stream = new FileInputStream(file);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        this.bitstream = new Bitstream(stream);
        try
        {
            audio = FactoryRegistry.systemRegistry().createAudioDevice();
        } catch (JavaLayerException e)
        {
            e.printStackTrace();
        }
        try
        {
            audio.open(decoder = new Decoder());

        } catch (JavaLayerException e)
        {
            e.printStackTrace();
        }

    }



    public boolean playSong(int startFrameNumber, int endFrameNumber, File songToPlay)
    {
        if(state == PlayerState.STATE_PLAYING)
            return true;


        currentFrameNumber = 0;

        openFile(songToPlay);

        state = PlayerState.STATE_PLAYING;
       // Thread t = new Thread( ()->
       // {
            boolean frameNotAchieved = true;
            boolean songPlayed = true;
            /// Rewind to the start frame
            while ((currentFrameNumber < startFrameNumber) && frameNotAchieved)
            {
                try
                {
                    frameNotAchieved = skipFrame();
                } catch (JavaLayerException e)
                {
                    e.printStackTrace();
                }
                currentFrameNumber++;
            }

            while ((currentFrameNumber < endFrameNumber) && songPlayed && state != PlayerState.STATE_PAUSED && state !=
                    PlayerState.STATE_STOPPED)
            {
                try
                {
                    songPlayed = decodeFrame();
                    currentFrameNumber++;
                } catch (JavaLayerException e)
                {
                    System.out.println("Blad dekodowania ramki nr: " + currentFrameNumber);

                }
            }

            AudioDevice out = audio;
            if (out != null)
            {
                out.flush();
                synchronized (this)
                {
                    close();
                }
            }
        /// If there was no request to pause or stop the song - request next song
        if(state == PlayerState.STATE_PLAYING)
            state = PlayerState.STATE_NEXT_SONG_REQUESTED;
        //}
      //  );
       // t.start();
        return false;
    }

    public void pauseSong()
    {
        pausedOnFrame = currentFrameNumber - 4; /// Minus 4 frames for better impression after resume - the sound is
                                                /// more consistent
        if(pausedOnFrame < 0)
            pausedOnFrame = 0;
        state = PlayerState.STATE_PAUSED;

        this.stop();
    }

    public void resumeSong()
    {
        playSong(pausedOnFrame, Integer.MAX_VALUE, currentlyOpenedFile);
    }

    public void stopSong()
    {
        state = PlayerState.STATE_STOPPED;
        this.stop();
        currentFrameNumber = 0;
        pausedOnFrame = 0;

    }

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
    public void continuousPlay()
    {
        Thread t = new Thread( () ->
        {
            do
            {
                if(state != PlayerState.STATE_PAUSED && state != PlayerState.STATE_STOPPED)
                {
                    if(randomOrInOrder == PlaybackOrder.PLAY_IN_ORDER)
                        playSong(0, Integer.MAX_VALUE, getNextSong());
                    else
                        playSong(0, Integer.MAX_VALUE, randomizeNextSong());
                }
                else
                   resumeSong();
            }while(state != PlayerState.STATE_STOPPED && state != PlayerState.STATE_PAUSED);
        });
        t.start();
    }

    public void setPlaylist(Playlist playlist)
    {
        this.playlist = playlist;
    }

    public void setEqualizer(Equalizer equalizer)
    {
        this.equalizer = equalizer;
    }

    public File getCurrentlyOpenedFile()
    {
        return currentlyOpenedFile;
    }

    public Playlist getPlaylist()
    {
        return playlist;
    }

    public Equalizer getEqualizer()
    {
        return equalizer;
    }

    public PlaybackOrder getRandomOrInOrder()
    {
        return randomOrInOrder;
    }

    public void setPlaybackOrder(PlaybackOrder randomOrInOrder)
    {
        this.randomOrInOrder = randomOrInOrder;
    }
}
