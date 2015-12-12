package com.jms;

/**
 * Listener of PlayerEvents interface
 */
public interface PlayerEventListener
{
    /**
     * Song changed event
     */
    void songChanged();

    /**
     * Playback time update
     */
    void updateSongTime();
}
