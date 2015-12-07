package com.jms;

import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * Created by Konrad on 2015-12-01.
 */
class WavTagReader
{
    public int chunkID;
    public int chunkSize;
    public int wavFormat;
    public int subchunk1ID;
    public int subchunk1Size;
    public short audioFormat;
    public short numOfChannels;
    public int sampleRate;
    public int byteRate;
    public short blockAlign;
    public short bitsPerSample;
    public int subchunk2ID;
    public int subchunk2Size;
    private int dataChunkStartIndex;
    private int firstSampleIndex;


    byte rawData[];



    public WavTagReader(byte rawDataArray[])
    {
        this.rawData = rawDataArray;
        dataChunkStartIndex = 0;
        firstSampleIndex = 0;

    }
    public int getFirstSampleIndex()
    {
        return this.firstSampleIndex;
    }

    private void readChunkID(int startIndex)
    {
        int lastByte = (rawData[startIndex + 3] << 24 & 0xFF000000);
        int thirdByte = ((rawData[startIndex + 2]) << 16) & 0x00FF0000;
        int secondByte = (rawData[startIndex + 1] << 8) & 0x0000FF00;
        int firstByte = rawData[startIndex];
        this.chunkID = firstByte | secondByte | thirdByte | lastByte;
    }

    private void readChunkSize(int startIndex)
    {
        int lastByte = (rawData[startIndex + 3] << 24 & 0xFF000000);
        int thirdByte = ((rawData[startIndex + 2]) << 16) & 0x00FF0000;
        int secondByte = (rawData[startIndex + 1] << 8) & 0x0000FF00;
        int firstByte = rawData[startIndex];
        this.chunkSize = firstByte | secondByte | thirdByte | lastByte;
    }
    private void readWavFormat(int startIndex)
    {
        int lastByte = (rawData[startIndex + 3] << 24 & 0xFF000000);
        int thirdByte = ((rawData[startIndex + 2]) << 16) & 0x00FF0000;
        int secondByte = (rawData[startIndex + 1] << 8) & 0x0000FF00;
        int firstByte = rawData[startIndex];
        this.wavFormat = firstByte | secondByte | thirdByte | lastByte;
    }
    private void readSubchunk1ID(int startIndex)
    {
        int lastByte = (rawData[startIndex + 3] << 24 & 0xFF000000);
        int thirdByte = ((rawData[startIndex + 2]) << 16) & 0x00FF0000;
        int secondByte = (rawData[startIndex + 1] << 8) & 0x0000FF00;
        int firstByte = rawData[startIndex];
        this.subchunk1ID = firstByte | secondByte | thirdByte | lastByte;
    }
    private void readSubchunk1Size(int startIndex)
    {
        int lastByte = (rawData[startIndex + 3] << 24 & 0xFF000000);
        int thirdByte = ((rawData[startIndex + 2]) << 16) & 0x00FF0000;
        int secondByte = (rawData[startIndex + 1] << 8) & 0x0000FF00;
        int firstByte = rawData[startIndex];
        this.subchunk1Size = firstByte | secondByte | thirdByte | lastByte;
    }
    private void readAudioFormat(int startIndex)
    {
        this.audioFormat = (short)(rawData[startIndex] | ((rawData[startIndex + 1] << 8) & 0xFF00));
    }
    private void readNumOfChannels(int startIndex)
    {
        this.numOfChannels = (short)(rawData[startIndex] | ((rawData[startIndex + 1] << 8) & 0xFF00));
    }
    private void readSampleRate(int startIndex)
    {
        int lastByte = (rawData[startIndex + 3] << 24 & 0xFF000000);
        int thirdByte = ((rawData[startIndex + 2]) << 16) & 0x00FF0000;
        int secondByte = (rawData[startIndex + 1] << 8) & 0x0000FF00;
        int firstByte = rawData[startIndex];
        this.sampleRate = firstByte | secondByte | thirdByte | lastByte;
    }
    private void readByteRate(int startIndex)
    {
        int lastByte = (rawData[startIndex + 3] << 24 & 0xFF000000);
        int thirdByte = ((rawData[startIndex + 2]) << 16) & 0x00FF0000;
        int secondByte = (rawData[startIndex + 1] << 8) & 0x0000FF00;
        int firstByte = rawData[startIndex];
        this.byteRate = firstByte | secondByte | thirdByte | lastByte;
    }
    private void readBlockAlign(int startIndex)
    {
        this.blockAlign = (short)(rawData[startIndex] | ((rawData[startIndex + 1] << 8) & 0xFF00));
    }
    private void readBitsPerSample(int startIndex)
    {
        this.bitsPerSample= (short)(rawData[startIndex] | ((rawData[startIndex + 1] << 8) & 0xFF00));
    }
    private void readSubchunk2ID(int startIndex)
    {
        int lastByte = (rawData[startIndex + 3] << 24 & 0xFF000000);
        int thirdByte = ((rawData[startIndex + 2]) << 16) & 0x00FF0000;
        int secondByte = (rawData[startIndex + 1] << 8) & 0x0000FF00;
        int firstByte = rawData[startIndex];
        this.subchunk2ID = firstByte | secondByte | thirdByte | lastByte;
    }
    private void readSubchunk2Size(int startIndex)
    {
        int lastByte = (rawData[startIndex + 3] << 24 & 0xFF000000);
        int thirdByte = ((rawData[startIndex + 2]) << 16) & 0x00FF0000;
        int secondByte = (rawData[startIndex + 1] << 8) & 0x0000FF00;
        int firstByte = rawData[startIndex];
        this.subchunk2Size = firstByte | secondByte | thirdByte | lastByte;
    }

    private int findDataSectionByteIndex()
    {
        int i = 0;
        while(!(rawData[i] == 'd' && rawData[i+1] == 'a' && rawData[i+2] == 't' && rawData[i+3] == 'a'))
            ++i;
        this.dataChunkStartIndex = i;
        this.firstSampleIndex = i + 8;
        return i;
    }


    public void readHeader()
    {
        readChunkID(0);
        readChunkSize(4);
        readWavFormat(8);
        readSubchunk1ID(12);
        readSubchunk1Size(16);
        readAudioFormat(20);
        readNumOfChannels(22);
        readSampleRate(24);
        readByteRate(28);
        readBlockAlign(32);
        readBitsPerSample(34);
        int subchunk2Index = findDataSectionByteIndex();
        readSubchunk2ID(subchunk2Index);
        readSubchunk2Size(subchunk2Index + 4);
    }

}

public class Editor
{
    private Converter converter;
    private File file;
    private FileInputStream fileInputStream;
    private BufferedInputStream dataIn;
    byte rawData[];
    private WavTagReader wavTagReader;

    public Editor()
    {

    }
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
        int fileLength = (int)file.length();
        rawData = new byte[fileLength];
        try
        {
            fileInputStream = new FileInputStream(file);
            dataIn = new BufferedInputStream(fileInputStream);
            for(int i=0; i<fileLength; ++i)
                rawData[i] = (byte)dataIn.read();
            wavTagReader = new WavTagReader(this.rawData);
            wavTagReader.readHeader();

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void saveSong(String filePath)
    {
        FileOutputStream outputStream = null;
        try
        {
            outputStream = new FileOutputStream(filePath);
            outputStream.write(this.rawData);
            outputStream.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public void cutSong(int startSecond, int endSecond)
    {
        int startIndex = wavTagReader.getFirstSampleIndex() + startSecond*wavTagReader.sampleRate*wavTagReader.numOfChannels*wavTagReader
            .bitsPerSample/8;
        int endIndex = wavTagReader.getFirstSampleIndex() + endSecond*wavTagReader.sampleRate*wavTagReader.numOfChannels*wavTagReader.bitsPerSample/8;

        for(int i=startIndex; i<endIndex; i++)
            rawData[i] = 0;
    }

    public void changeVolume(double gainFactor)
    {
        int sample = 0;
        for(int i=wavTagReader.getFirstSampleIndex(); i<rawData.length; i=i+2)
        {
            sample = (short)rawData[i] +  (short)((rawData[i+1] << 8) & 0xFF00);
            sample *= gainFactor;
            rawData[i] = (byte)(sample & 0xFF);
            rawData[i+1] = (byte)((sample >>> 8) & 0xFF);
        }
    }
}
