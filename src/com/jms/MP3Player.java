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
        STATE_NO_FILE
    }

    private Playlist        playlist;               /**< Playlist from which songs are played **/

    private Equalizer       equalizer;              /**< Equalizer which is used to modify audio settings **/
    private File            currentlyOpenedFile;    /**< Currently processed file **/
    private FileInputStream stream;                 /**< Buffer from which audio frames are taken **/
    private int             currentFrameNumber;     /**< Number of the currently decoded frame **/
    private int             pausedOnFrame;          /**< Number of the frame which was decoded last when pause event
 came**/

    private PlayerState     state;

    public MP3Player(File file) throws JavaLayerException
    {
        super(null, null);
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
            audio.open(decoder);
        } catch (JavaLayerException e)
        {
            e.printStackTrace();
        }

    }

    public boolean playSong(int startFrameNumber, int endFrameNumber, File songToPlay)
    {
        if(state == PlayerState.STATE_PLAYING)
            return true;

        boolean frameNotAchieved = true;
        boolean songPlayed = true;
        currentFrameNumber = 0;

        openFile(songToPlay);

        state = PlayerState.STATE_PLAYING;
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

        while((currentFrameNumber < endFrameNumber) && songPlayed && state != PlayerState.STATE_PAUSED && state !=
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

        return false;
    }

    public void pauseSong()
    {
        pausedOnFrame = currentFrameNumber;
        this.stop();

        state = PlayerState.STATE_PAUSED;
    }

    public void resumeSong()
    {
        this.playSong(pausedOnFrame, Integer.MAX_VALUE, currentlyOpenedFile);
    }

    public void stopSong()
    {
        this.stop();
        currentFrameNumber = 0;
        pausedOnFrame = 0;
        state = PlayerState.STATE_STOPPED;
    }


}
