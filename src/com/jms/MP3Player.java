package com.jms;

import javazoom.jl.decoder.*;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
        STATE_PREV_SONG_REQUESTED,
        STATE_SONG_REQUESTED
    }

    public enum PlaybackOrder
    {
        PLAY_IN_ORDER,
        REPEAT_SINGLE,
        PLAY_SINGLE,
        PLAY_RANDOM
    }

    private Playlist                playlist;               /**< Playlist from which songs are played **/

    private Equalizer               equalizer;              /**< Equalizer which is used to modify audio settings **/
    private File                    currentlyOpenedFile;    /**< Currently processed file **/
    private FileInputStream         stream;                 /**< Buffer from which audio frames are taken **/
    private int                     currentFrameNumber;     /**< Number of the currently decoded frame **/
    private int                     pausedOnFrame;          /**< Number of the frame which was decoded last when pause event came**/
    private int                     requestedItemIndex;     /**< If song requested from playlist, this is the item number */

    private PlayerState             state = PlayerState.STATE_NO_FILE;
    private PlaybackOrder           playbackOrder = PlaybackOrder.PLAY_IN_ORDER;

    private List<PlayerEventListener> listener = new ArrayList<PlayerEventListener>();

    /**
     * @param file
     * @throws JavaLayerException
     */
    public MP3Player(/*File file*/) throws JavaLayerException
    {
//        this.currentlyOpenedFile = file;
//        try
//        {
//            this.stream = new FileInputStream(file);
//        } catch (FileNotFoundException e)
//        {
//            e.printStackTrace();
//        }
//        this.bitstream = new Bitstream(stream);
//        try
//        {
//            audio = FactoryRegistry.systemRegistry().createAudioDevice();
//        } catch (JavaLayerException e)
//        {
//            e.printStackTrace();
//        }
//        try
//        {
//            audio.open(decoder = new Decoder());
//        } catch (JavaLayerException e)
//        {
//            e.printStackTrace();
//        }

        this.playlist = new Playlist();
    }

    /**
     * This function adds an listener of the player events
     * @param listenerToAdd - object which implements an PlayerEventListener interface, which is to react on the event
     */
    public void addListener(PlayerEventListener listenerToAdd)
    {
        listener.add(listenerToAdd);
    }

    /**
     * This function sends an event to the GUI to inform it, which song is currently played in order to select it in
     * playerDisplay
     */
    public void sendSongChangedEvt()
    {
        /// Send event
        for(int i=0; i<listener.size(); i++)
            listener.get(i).songChanged();
    }

    /**
     * This function sends an event to the GUI to update the time of the song
     */
    public void songTimeUpdateEvt()
    {
        /// Send event
        for(int i=0; i<listener.size(); i++)
            listener.get(i).updateSongTime();
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
            decoder.setEqualizer(equalizer);

        } catch (JavaLayerException e)
        {
            e.printStackTrace();
        }


    }

    /**
     * Gets the current time of the song
     * @return the current time in miliseconds
     */
    public float getCurrentSongSizeMs()
    {
        if(h != null)
            return currentFrameNumber*h.ms_per_frame();

        return 0;
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
        /// Check if the playlist isn't empty
        if(songToPlay == null)
        {
            state = PlayerState.STATE_NO_FILE;
            return false;
        }

        if(state == PlayerState.STATE_PLAYING)
            return true;


        currentFrameNumber = 0;

        openFile(songToPlay);
       // System.out.println(((getCurrentSongSizeMs()/1000)));
        state = PlayerState.STATE_PLAYING;

        /// Inform GUI, which song is to be played
        this.sendSongChangedEvt();

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
                songTimeUpdateEvt();    /// Send an event to the GUI about current song time
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
        try
        {
            /// Give some time for the thread responsible for song playing to shutdown
            Thread.sleep(100);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        if(currentlyOpenedFile != null)
            this.stop();

        currentFrameNumber = 0;
        pausedOnFrame = 0;

        if(playlist.getSize() == 0)
            this.currentlyOpenedFile = null;
    }

    void rewindSong(int milis)
    {
        if( h == null || state == PlayerState.STATE_STOPPED)
            return;

        pauseSong();
        pausedOnFrame = (int) ((float) milis/h.ms_per_frame());
        executeTask();
    }

    private File getCurrentSong()
    {
        if(getPlaylist().getCurrentElementIndex() == -1)
            return null;

        return playlist.getCurrentElement().getFile();
    }
    /**
     * This function sets the currently_played_song to the next song on the playlist. It does playlist wrapping
     */
    private File getNextSong()
    {
        if(getPlaylist().getSize() == 0)
            return null;

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
        if(getPlaylist().getSize() == 0)
            return null;

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
        if(getPlaylist().getSize() == 0)
            return null;

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
     * The main function which handles songs playing in different options:
     *                  - in order from playlist
     *                  - randomly from playlist
     *                  - single song repeated
     *                  - single song played only once
     *                  - pause and resume
     *                  - stop and start song from the beginning
     */
    public void executeTask()
    {
        Thread t = new Thread( () ->
        {
            do
            {
                // If not paused
                if(state != PlayerState.STATE_PAUSED)// && state != PlayerState.STATE_STOPPED)
                {
                    if(playbackOrder == PlaybackOrder.REPEAT_SINGLE || playbackOrder == PlaybackOrder.PLAY_SINGLE)
                    {
                        playSong(0, Integer.MAX_VALUE, getCurrentSong());
                        if(playbackOrder == PlaybackOrder.PLAY_SINGLE)
                        {
                            if(state == PlayerState.STATE_NEXT_SONG_REQUESTED)
                                this.stopSong();
                        }
                    }

                    if(playbackOrder == PlaybackOrder.PLAY_IN_ORDER)
                    {
                        /// If user requested particular song from playlist or the current song was stopped
                        if(state == PlayerState.STATE_SONG_REQUESTED || state == PlayerState.STATE_STOPPED)
                            playSong(0, Integer.MAX_VALUE, getCurrentSong());
                        else if(state == PlayerState.STATE_PREV_SONG_REQUESTED) // If previous song requested
                            playSong(0, Integer.MAX_VALUE, getPrevSong());
                        else
                            playSong(0, Integer.MAX_VALUE, getNextSong());      /// If the previous song was requested
                    }
                    if(playbackOrder == PlaybackOrder.PLAY_RANDOM) //if random play enabled
                    {
                        if(state == PlayerState.STATE_SONG_REQUESTED || state == PlayerState.STATE_STOPPED)
                            playSong(0, Integer.MAX_VALUE, getCurrentSong());
                        else
                        if(state == PlayerState.STATE_PREV_SONG_REQUESTED)
                            playSong(0, Integer.MAX_VALUE, randomizeNextSong());
                        else
                            playSong(0, Integer.MAX_VALUE, randomizeNextSong());
                    }
                }
                else //if paused, just resume
                   resumeSong();
            }while(state != PlayerState.STATE_STOPPED && state != PlayerState.STATE_PAUSED && state != PlayerState.STATE_NO_FILE);
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
        this.executeTask();
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
        this.executeTask();
    }

    public void playPlaylistItem(int playlistIndex)
    {
        this.stopSong();
        try
        {
            /// Give some time for the thread responsible for song playing to shutdown
            Thread.sleep(100);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        state = PlayerState.STATE_SONG_REQUESTED;
        playlist.setCurrentElementIndex(playlistIndex);
        this.executeTask();

    }
    /**
     * Toggles the player setting wheter to play songs randomly or in order
     */
    public void setPlaybackOrder()
    {
        if(this.getPlaybackOrder() == MP3Player.PlaybackOrder.PLAY_IN_ORDER)
        {
            this.setPlaybackOrder(MP3Player.PlaybackOrder.PLAY_RANDOM);
        }
        else
        {
            this.setPlaybackOrder(MP3Player.PlaybackOrder.PLAY_IN_ORDER);
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
     * Gets the flag @ref playbackOrder value.
     * @return       PLAY_IN_ORDER - if the playlist is played in order
     *               PLAY_RANDOM - if the songs are chosen from the playlist randomly
     */
    public PlaybackOrder getPlaybackOrder()
    {
        return playbackOrder;
    }

    /**
     * This is setter for the flag @ref playbackOrder value.
     * @param playbackOrder -   PLAY_IN_ORDER - if the playlist is to be played in order
     *                            PLAY_RANDOM - if the songs are to be chosen from the playlist randomly
     */
    public void setPlaybackOrder(PlaybackOrder playbackOrder)
    {
        this.playbackOrder = playbackOrder;
    }

    public PlayerState getState()
    {
        return state;
    }

    public void setState(PlayerState state)
    {
        this.state = state;
    }

    /**
     *
     * @param song
     * @return
     */
    public int getSongTotalTimeMs(File song)
    {
        int frameCount = 0;
        int timeMs = 0;
        openFile(song);
        try
        {
            while(skipFrame())
            {
                timeMs = (int)(frameCount*h.ms_per_frame());
                frameCount++;
            }
        } catch (JavaLayerException e)
        {
            e.printStackTrace();
        }


        return timeMs;
    }

    public int getSongTotalFrames(File song)
    {
        int frameCount = 0;
        openFile(song);
        try
        {
            while(skipFrame())
            {
                frameCount++;
            }
        } catch (JavaLayerException e)
        {
            e.printStackTrace();
        }


        return frameCount;
    }

    public int getCurrentFrameNumber()
    {
        return currentFrameNumber;
    }

    /**
     * Returns list of PlayerEventListeners, mainly for test purposes
     * @return list of PlayerEventListeners registered to this instance
     */
    public List<PlayerEventListener> getPlayerEventListeners() {
        return listener;
    }
}
