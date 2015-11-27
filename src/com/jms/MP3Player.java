package com.jms;

import javazoom.jl.decoder.*;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.AdvancedPlayer;

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
    public enum RandomOrContinuous
    {
        PLAY_IN_ORDER,
        PLAY_RANDOM
    }
    private Playlist                playlist;               /**< Playlist from which songs are played **/

    private Equalizer               equalizer;              /**< Equalizer which is used to modify audio settings **/
    private File                    currentlyOpenedFile;    /**< Currently processed file **/
    private FileInputStream         stream;                 /**< Buffer from which audio frames are taken **/
    private int                     currentFrameNumber;     /**< Number of the currently decoded frame **/
    private int                     pausedOnFrame;          /**< Number of the frame which was decoded last when pause event
 came**/

    private PlayerState             state;
    private RandomOrContinuous      randomOrInOrder = RandomOrContinuous.PLAY_IN_ORDER;


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

    /**
     * This function prepares the file connected resources - it creates buffers, audiodevice and decoder
     * @param file - file which is about to be opened(played)
     */
    private void openFile(File file)
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
            decoder = new Decoder();
            audio.open(decoder);

        } catch (JavaLayerException e)
        {
            e.printStackTrace();
        }


    }

    public float getCurrentSongSizeMs()
    {
        return this.decoder.getL3decoder().getHeader().total_ms((int)this.currentlyOpenedFile.length());
    }

    /**
     * It is main function which plays song. It blocks execution, so it is started in its own Thread. It decodes
     * frames and sends them to the AudioDevice buffer
     * @param startFrameNumber - the frame number from which the play should start.
     * @param endFrameNumber - the last frame number which should be played
     * @param songToPlay - File containing the song which is to be played
     * @return true - if song has ended
     */
    private boolean playSong(int startFrameNumber, int endFrameNumber, File songToPlay)
    {
        if(state == PlayerState.STATE_PLAYING)
            return true;


        currentFrameNumber = 0;

        openFile(songToPlay);
        System.out.println(((getCurrentSongSizeMs()/1000)));
        state = PlayerState.STATE_PLAYING;

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

        return false;
    }

    /**
     *  Pauses the song on the currently played frame. If the state != STATE_PLAYING, it does nothing
     */
    public void pauseSong()
    {
        if(state != PlayerState.STATE_PLAYING)
            return;
        pausedOnFrame = currentFrameNumber - 3; /// Minus 3 frames for better impression after resume - the sound is
                                                /// more consistent
        if(pausedOnFrame < 0)
            pausedOnFrame = 0;
        state = PlayerState.STATE_PAUSED;

        this.stop();
    }

    /**
     * Resumes the paused song from the moment it was paused on. If the state != STATE_PAUSED, it does nothing
     */
    public void resumeSong()
    {
        if(state != PlayerState.STATE_PAUSED)
            return;
        playSong(pausedOnFrame, Integer.MAX_VALUE, currentlyOpenedFile);
    }

    /**
     * Stops the currently played song and frees resources connected with it
     */
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
     * This function sets the currently_played_song to the previous song on the playlist. It does playlist wrapping
     */
    private File getPrevSong()
    {
        ///   If we are have not played recently the last song, then increment the current song index
        if(this.getPlaylist().getCurrentElementIndex() > 0)
            this.getPlaylist().decCurrentElementIndex();
        else    /**< Else, wrap the index and set it to the playlist start */
            this.getPlaylist().setCurrentElementIndex(this.getPlaylist().getSize()-1);

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
                    if(randomOrInOrder == RandomOrContinuous.PLAY_IN_ORDER)
                    {
                        /// If the next song was requested
                        if(state != PlayerState.STATE_PREV_SONG_REQUESTED)
                            playSong(0, Integer.MAX_VALUE, getNextSong());
                        else
                            playSong(0, Integer.MAX_VALUE, getPrevSong());      /// If the previous song was requested
                    }
                    else
                        playSong(0, Integer.MAX_VALUE, randomizeNextSong());
                }
                else
                   resumeSong();
            }while(state != PlayerState.STATE_STOPPED && state != PlayerState.STATE_PAUSED);
        });
        t.start();
    }

    /**
     * Stops the currently played song, and starts the next one on the playlist. If the last played song was the last
     * on the list, than it plays the first one
     */
    public void playNextSong()
    {
        /// Stop the currently playing song
        this.stopSong();
        try
        {
            /// Give some time for the thread responsible for song playing to shutdown
            Thread.sleep(100);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        /// Request the next song on the list
        state = PlayerState.STATE_NEXT_SONG_REQUESTED;
        /// Play the song
        this.continuousPlay();
    }
    /**
     * Stops the currently played song, and starts the previous one on the playlist. If the last played song was the
     * first one on the list, than it plays the last one
     */
    public void playPrevSong()
    {
        /// Stop the currently playing song
        this.stopSong();

        try
        {
            /// Give some time for the thread responsible for song playing to shutdown
            Thread.sleep(100);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        /// Request the next song on the list
        state = PlayerState.STATE_PREV_SONG_REQUESTED;
        /// Play the song
        this.continuousPlay();
    }

    /**
     * Toggles the player setting wheter to play songs randomly or in order
     */
    public void toggleRandomOrInOrder()
    {
        if(this.getRandomOrInOrder() == MP3Player.RandomOrContinuous.PLAY_IN_ORDER)
        {
            this.setRandomOrInOrder(MP3Player.RandomOrContinuous.PLAY_RANDOM);
        }
        else
        {
            this.setRandomOrInOrder(MP3Player.RandomOrContinuous.PLAY_IN_ORDER);
        }
    }

    /**
     * This is the setter, to set the playlist
     * @param playlist - playlist ot set
     */
    public void setPlaylist(Playlist playlist)
    {
        this.playlist = playlist;
    }

    /**
     * This is the setter for equalizer of the player
     * @param equalizer - equalizer to be set
     */
    public void setEqualizer(Equalizer equalizer)
    {
        this.equalizer = equalizer;
    }

    /**
     * Returns the currently opened(most currently played) file
     * @return File
     */
    public File getCurrentlyOpenedFile()
    {
        return currentlyOpenedFile;
    }

    /**
     * Returns the playlist which is set in the MP3Player
     * @return
     */
    public Playlist getPlaylist()
    {
        return playlist;
    }

    /**
     * Returns the equalizer which is set in the MP3Player
     * @return
     */
    public Equalizer getEqualizer()
    {
        return equalizer;
    }

    /**
     * Gets the flag @ref randomOrInOrder value.
     * @return       PLAY_IN_ORDER - if the playlist is played in order
     *               PLAY_RANDOM - if the songs are chosen from the playlist randomly
     */
    public RandomOrContinuous getRandomOrInOrder()
    {
        return randomOrInOrder;
    }

    /**
     * This is setter for the flag @ref randomOrInOrder value.
     * @param randomOrInOrder -   PLAY_IN_ORDER - if the playlist is to be played in order
     *                            PLAY_RANDOM - if the songs are to be chosen from the playlist randomly
     */
    public void setRandomOrInOrder(RandomOrContinuous randomOrInOrder)
    {
        this.randomOrInOrder = randomOrInOrder;
    }
}
