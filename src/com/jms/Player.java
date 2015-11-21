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
    }

    public void OpenFile(String file_path)
    {
        try
        {
            if(currently_played_file != null)
                currently_played_file.close();

            currently_played_file = new FileInputStream(new File(file_path));
        }catch(FileNotFoundException e)
        {
            System.out.println(e.getMessage());
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

    public void Pause()
    {
        player.stop();
        state = State.STATE_PAUSED;
    }

    public void Play()
    {
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
}
