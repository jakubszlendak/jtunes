package com.jms.tests;

import com.jms.Editor;
import junit.framework.TestCase;

import java.io.File;

/**
 * Created by jakub on 14.12.15.
 */
public class EditorTest extends TestCase {

    private Editor instance;

    public void setUp() throws Exception {
        super.setUp();
        instance = new Editor();


    }

    public void tearDown() throws Exception {
        File file = new File("test.wav");
        if(file.exists())
            file.delete();
        file = new File("test.mp3");
        if(file.exists())
            file.delete();
    }

    public void testConvertMP3ToWav() throws Exception {
        instance.convertMP3ToWav("01_The_Trail.mp3", "test.wav");
        File test = new File("test.wav");
        assertTrue(test.exists());
        assertTrue(test.isFile());
        assertTrue(test.getName().endsWith(".wav"));
    }

    public void testLoadSong() throws Exception {
        instance.loadSong(new File("01_The_Trail.mp3"));
    }

    public void testSaveSong() throws Exception {
        instance.loadSong(new File("01_The_Trail.mp3"));
        instance.saveSong("test.wav");
    }

    public void testMuteSong() throws Exception {
        instance.loadSong(new File("01_The_Trail.mp3"));
        instance.muteSong(1, 2);
//        instance.muteSong(2, 1);
    }

    public void testCutSong() throws Exception {
        instance.loadSong(new File("01_The_Trail.mp3"));
//        instance.cutSong(10, 20);
//        instance.cutSong(2, 1);

    }

    public void testChangeVolume() throws Exception {
        instance.loadSong(new File("01_The_Trail.mp3"));
        instance.changeVolume(0.5);
        // If something will go wrong, exception will be thrown

    }


    public void testConvertWavToMP3() throws Exception {
        instance.loadSong(new File("01_The_Trail.mp3"));
        instance.convertWavToMP3("temp.wav", "test.mp3");
        File test = new File("test.mp3");
        assertTrue(test.exists());
        assertTrue(test.isFile());
        assertTrue(test.getName().endsWith(".mp3"));
    }
}