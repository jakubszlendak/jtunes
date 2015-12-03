package com.jms.tests;

import com.jms.PlaylistItem;
import junit.framework.TestCase;

import java.io.File;

/**
 * Created by jakub on 28.11.15.
 */
public class PlaylistItemTest extends TestCase {

    private PlaylistItem instance1;
    private PlaylistItem instance2;
    final static String TEST_FILE_PATH_1 = "01_The_Trail.mp3";
    final static String TEST_FILE_PATH_2 = "01 - Hard Row to Hoe.mp3";
    public void setUp() throws Exception {
        super.setUp();
        instance1 = new PlaylistItem(new File(TEST_FILE_PATH_1));
        instance2 = new PlaylistItem(new File(TEST_FILE_PATH_2));

    }

    public void testGetTitleOnFileWithoutTags() throws Exception {
        assertNull(instance1.getTitle());
    }

    public void testGetTitleOnFileWithTags() throws Exception {
        assertEquals("Hard Row to Hoe", instance2.getTitle());
    }

    public void testGetArtistOnFileWithoutTags() throws Exception {
        assertNull(instance1.getArtist());
    }

    public void testGetArtistOnFileWithTags() throws Exception {
        assertEquals("Brother Dege", instance2.getArtist());
    }

    public void testGetAlbumOnFileWithoutTags() throws Exception {
        assertNull(instance1.getAlbum());
    }

    public void testGetAlbumOnFileWithTags() throws Exception {
        assertEquals("Folk Songs of the American Longhair", instance2.getAlbum());
    }

    public void testGetDuration() throws Exception {
        assertEquals(3*60+33, instance2.getDuration());
    }

    public void testGetAlbumArtWhenNoAlbumArtGiven() throws Exception {
        assertNull(instance1.getAlbumArt());
    }

    public void testGetFile() throws Exception {
        assertNotNull(instance1.getFile());
        assertTrue(instance1.getFile().canRead());
    }

    public void testGetFilename() throws Exception {
        assertEquals(TEST_FILE_PATH_1, instance1.getFilename());
        assertEquals(TEST_FILE_PATH_2, instance2.getFilename());
    }
}