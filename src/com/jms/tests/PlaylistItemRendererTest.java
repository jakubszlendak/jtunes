package com.jms.tests;

import com.jms.Playlist;
import com.jms.PlaylistItem;
import com.jms.PlaylistItemRenderer;
import junit.framework.TestCase;

import javax.swing.*;
import java.io.File;

/**
 * Created by jakub on 01.12.15.
 */
public class PlaylistItemRendererTest extends TestCase {

    private PlaylistItemRenderer instance;
    private PlaylistItem item1;
    private PlaylistItem item2;

    public void setUp() throws Exception {
        super.setUp();
        instance = new PlaylistItemRenderer();
        item1 = new PlaylistItem(new File(PlaylistItemTest.TEST_FILE_PATH_1));
        item2 = new PlaylistItem(new File(PlaylistItemTest.TEST_FILE_PATH_2));


    }

    public void testRenderingWhenNoTagsGiven() throws Exception {
        JList mockList = new JList(new Playlist());

        instance.getListCellRendererComponent(mockList,item1,0,true,true);

    }

    public void testRenderingWhenTagsGiven() throws Exception {
        JList mockList = new JList(new Playlist());

        instance.getListCellRendererComponent(mockList,item2,1,true,true);

    }

    public void testGetScaledImage() throws Exception {
        assertNotNull(new ImageIcon(PlaylistItemRenderer.getScaledImage(new ImageIcon("img / noalbum.jpg").getImage(), 50, 50)));
    }
}