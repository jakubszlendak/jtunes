package com.jms.tests;

import com.jms.MP3Player;
import com.jms.Playlist;
import com.jms.PlaylistItem;
import com.jms.PlayerEventListener;
import junit.framework.TestCase;

import java.io.File;

/**
 * Created by jakub on 01.12.15.
 */
public class MP3PlayerTest extends TestCase {


    private MP3Player instance;

    public void setUp() throws Exception {
        super.setUp();
        instance = new MP3Player();
        instance.getPlaylist().addPlaylistItem(new PlaylistItem(new File(PlaylistItemTest.TEST_FILE_PATH_1)));
        instance.getPlaylist().addPlaylistItem(new PlaylistItem(new File(PlaylistItemTest.TEST_FILE_PATH_2)));


    }

    public void testAddListener() throws Exception {
        instance.addListener(new PlayerEventListener() {
            @Override
            public void songChanged() {
            }

            @Override
            public void updateSongTime() {

            }
        });
        assertNotNull(instance.getPlayerEventListeners());
    }

    public void testSendSongChangedEvt() throws Exception {
//        instance.addListener(new PlayerEventListener() {
//            @Override
//            public void songChanged() {
//            }
//
//            @Override
//            public void updateSongTime() {
//
//            }
//        });
//        instance.sendSongChangedEvt();

    }

    public void testSongTimeUpdateEvt() throws Exception {

    }

    public void testGetCurrentSongSizeMs() throws Exception {
        assertNotNull(instance.getSongTotalTimeMs(new File(PlaylistItemTest.TEST_FILE_PATH_1)));
        assertTrue(instance.getSongTotalTimeMs(new File(PlaylistItemTest.TEST_FILE_PATH_1)) > 1000);
    }

    public void testPauseSong() throws Exception {
        instance.setPlaybackOrder(MP3Player.PlaybackOrder.PLAY_IN_ORDER);
        instance.executeTask();
        Thread.sleep(10);
        instance.pauseSong();
        assertEquals(MP3Player.PlayerState.STATE_PAUSED, instance.getState());

    }

    public void testResumeSong() throws Exception {
        instance.setPlaybackOrder(MP3Player.PlaybackOrder.PLAY_IN_ORDER);
        instance.executeTask();
        Thread.sleep(10);
        instance.pauseSong();
        Thread t = new Thread( ()-> instance.resumeSong() );
        t.start();
        Thread.sleep(100);
        assertEquals(MP3Player.PlayerState.STATE_PLAYING, instance.getState());
        instance.stopSong();
    }

    public void testStopSong() throws Exception {
        instance.setPlaybackOrder(MP3Player.PlaybackOrder.PLAY_IN_ORDER);
        instance.executeTask();
        Thread.sleep(100);
        instance.stopSong();
        assertEquals(MP3Player.PlayerState.STATE_STOPPED, instance.getState());
    }

    public void testPlayNextSong() throws Exception {

    }

    public void testPlayPrevSong() throws Exception {

    }

    public void testPlayPlaylistItem() throws Exception {

    }

    public void testSetPlaybackOrder() throws Exception {

    }

    public void testSetPlaylist() throws Exception {
        Playlist testPlaylist = new Playlist();
        testPlaylist.addPlaylistItem(new PlaylistItem(new File(PlaylistItemTest.TEST_FILE_PATH_1)));

        instance.setPlaylist(testPlaylist);
        assertEquals(testPlaylist, instance.getPlaylist());

    }

    public void testSetEqualizer() throws Exception {

    }


    public void testGetPlaylist() throws Exception {

    }

    public void testGetEqualizer() throws Exception {

    }

    public void testGetPlaybackOrder() throws Exception {

    }

    public void testSetPlaybackOrder1() throws Exception {

    }

    public void testGetState() throws Exception {
        assertEquals(MP3Player.PlayerState.STATE_NO_FILE, instance.getState());

    }

    public void testSetState() throws Exception {
        instance.setState(MP3Player.PlayerState.STATE_STOPPED);
        assertEquals(MP3Player.PlayerState.STATE_STOPPED, instance.getState());

    }

    public void testGetSongTotalTimeMs() throws Exception {

    }
}