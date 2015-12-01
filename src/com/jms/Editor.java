package com.jms;

import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;

import java.io.*;

/**
 * Created by Konrad on 2015-12-01.
 */
public class Editor
{
    private Converter converter;
    private File file;
    private FileOutputStream fileOutputStream;
    private BufferedOutputStream dataOut;

    public void convertMP3ToWav(String filepathToConvert, String convertedFilepath)
    {
        try
        {
            converter.convert(filepathToConvert,convertedFilepath);
        } catch (JavaLayerException e)
        {
            e.printStackTrace();
        }
    }
    public void loadSong(File fileToOpen)
    {
        file = fileToOpen;

        try
        {
            fileOutputStream = new FileOutputStream(file);
            dataOut = new BufferedOutputStream(fileOutputStream);

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

}
