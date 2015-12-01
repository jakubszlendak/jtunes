package com.jms.tests;

import com.jms.PlaylistItem;
import junit.framework.TestCase;

import java.io.File;

/**
 * Created by jakub on 28.11.15.
 */
public class PlaylistItemTest extends TestCase {

    private PlaylistItem instance;
    private final static String TEST_FILE_PATH = "01_The_Trail.mp3";

    public void setUp() throws Exception {
        super.setUp();
        instance = new PlaylistItem(new File(TEST_FILE_PATH));

    }

    public void testGetTitle() throws Exception {
        assertEquals("The Trail", instance.getTitle());
    }

    public void testGetArtist() throws Exception {

    }

    public void testGetAlbum() throws Exception {

    }

    public void testGetGenre() throws Exception {

    }

    public void testGetYear() throws Exception {

    }

    public void testGetDuration() throws Exception {

    }

    public void testGetAlbumArt() throws Exception {

    }

    public void testGetFile() throws Exception {

    }

    public void testGetFilename() throws Exception {

    }
}