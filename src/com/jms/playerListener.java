package com.jms;

/**
 * Created by Konrad on 2015-11-28.
 */
public interface playerListener
{
    /// The event function
    void songChanged();
    /// The event for song current time update
    void updateSongTime();
}
