package com.jms.tests;

import com.jms.Playlist;
import com.jms.PlaylistItem;
import junit.framework.TestCase;

/**
 * Created by jakub on 28.11.15.
 */
public class PlaylistTest extends TestCase {

    Playlist instance;
    PlaylistItem item1;
    PlaylistItem item2;
    PlaylistItem item3;

    public void setUp() throws Exception {
        super.setUp();
        instance = new Playlist();
        item1 = new PlaylistItem("title", "artist", "album", "genre", 1993, 1000);
        item2 = new PlaylistItem("title1", "artist1", "album1", "genre1", 19931, 10001);
        item3 = new PlaylistItem("title2", "artist2", "album2", "genre2", 19932, 10002);
        instance.addPlaylistItem(item1);
        instance.addPlaylistItem(item2);
        instance.addPlaylistItem(item3);
    }

    public void testAddPlaylistItemAtEnd() throws Exception {
        instance.addPlaylistItem(item1);
        assertEquals(item1, instance.getElementAt(instance.getSize() - 1));
    }

    public void testAddPlaylistItemAtIndex() throws Exception {
        instance.addPlaylistItem(item1, 0);
        PlaylistItem temp = instance.getElementAt(0);
        assertEquals(item1, instance.getElementAt(0));
        assertEquals(temp, instance.getElementAt(1));
    }

    public void testRemovePlaylistItem() throws Exception {
        instance.addPlaylistItem(item1);
        instance.addPlaylistItem(item2);
        assertEquals(item1, instance.removePlaylistItem(0));
        assertEquals(item2, instance.getElementAt(0));

    }

    public void testGetCurrentElementIndex() throws Exception {
        Playlist testInstance = new Playlist();
        assertEquals(-1, testInstance.getCurrentElementIndex());

        instance.setCurrentElementIndex(1);
        assertEquals(1, instance.getCurrentElementIndex());
        instance.setCurrentElementIndex(1000);
        assertEquals(1, instance.getCurrentElementIndex());

    }

    public void testReplaceItem() throws Exception {
        PlaylistItem a = instance.getElementAt(0);
        PlaylistItem b = instance.getElementAt(1);
        instance.replaceItem(0, 1);
        assertEquals(a, instance.getElementAt(1));
        assertEquals(b, instance.getElementAt(0));

    }

    public void testEmptyPlaylistShouldReturnZeroSize() throws Exception {
        Playlist testInstance = new Playlist();
        assertEquals(0, testInstance.getSize());
    }


    public void testGetSize() throws Exception {
        assertEquals(3, instance.getSize());
    }

    public void testGetElementAt() throws Exception {
        assertEquals(item1, instance.getElementAt(0));
    }

    public void testIncCurrentElementIndex() throws Exception {
        instance.setCurrentElementIndex(1);
        instance.incCurrentElementIndex();
        assertEquals(2, instance.getCurrentElementIndex());
        instance.incCurrentElementIndex();
        assertEquals(2, instance.getCurrentElementIndex());
    }

    public void testDecCurrentElementIndex() throws Exception {
        instance.setCurrentElementIndex(1);
        instance.decCurrentElementIndex();
        assertEquals(0, instance.getCurrentElementIndex());
        instance.decCurrentElementIndex();
        assertEquals(0, instance.getCurrentElementIndex());
    }

    public void testSetCurrentElementIndex() throws Exception {
        instance.setCurrentElementIndex(0);
        assertEquals(0, instance.getCurrentElementIndex());
        instance.setCurrentElementIndex(1000);
        assertEquals(0, instance.getCurrentElementIndex());
        Playlist test = new Playlist();
        assertEquals(-1, test.getCurrentElementIndex());
    }

}