package com.jms;

import it.sauronsoftware.jave.*;
import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

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

    /**
     * This function reads entire .WAV file header to retrieve important information and get the first sample index
     */
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

    public int getChunkID()
    {
        return chunkID;
    }

    public int getChunkSize()
    {
        return chunkSize;
    }

    public int getWavFormat()
    {
        return wavFormat;
    }

    public int getSubchunk1ID()
    {
        return subchunk1ID;
    }

    public int getSubchunk1Size()
    {
        return subchunk1Size;
    }

    public short getAudioFormat()
    {
        return audioFormat;
    }

    public short getNumOfChannels()
    {
        return numOfChannels;
    }

    public int getSampleRate()
    {
        return sampleRate;
    }

    public int getByteRate()
    {
        return byteRate;
    }

    public short getBlockAlign()
    {
        return blockAlign;
    }

    public short getBitsPerSample()
    {
        return bitsPerSample;
    }

    public int getSubchunk2ID()
    {
        return subchunk2ID;
    }

    public int getSubchunk2Size()
    {
        return subchunk2Size;
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
        converter = new Converter();
    }

    /**
     * This function converts .mp3 song to .WAV song
     * @param filepathToConvert - the source file path (mp3)
     * @param convertedFilepath - the destination file path (wave)
     */
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

    public void convertWavToMP3(String filepathToConvert, String convertedFilePath)
    {
        Encoder wavToMp3Converter = new Encoder();
        EncodingAttributes att = new EncodingAttributes();
        AudioAttributes audioAtt = new AudioAttributes();
        String codecs[] = null;
        String formats[] = null;
        try
        {
            formats = wavToMp3Converter.getSupportedDecodingFormats();
        } catch (EncoderException e)
        {
            e.printStackTrace();
        }
        try
        {
            codecs = wavToMp3Converter.getAudioEncoders();
        } catch (EncoderException e)
        {
            e.printStackTrace();
        }
        int mp3CodecIndex = -1;
        /*for(int i=codecs.length; i>0; i--)
        {
            if(codecs[i].compareTo("") == 0)
            {
                mp3CodecIndex = i;
                break;
            }
        }*/
        audioAtt.setVolume(255);
        audioAtt.setSamplingRate(new Integer(this.getWavTagReader().sampleRate));
        audioAtt.setChannels(new Integer(wavTagReader.numOfChannels));
        audioAtt.setBitRate(new Integer(this.wavTagReader.sampleRate*this.wavTagReader.bitsPerSample*this
                .wavTagReader.numOfChannels));
        audioAtt.setCodec("libmp3lame");
        att.setAudioAttributes(audioAtt);
        att.setFormat("mp3");
        att.setOffset(new Float((float)0));
        att.setVideoAttributes(null);
        //att.setDuration(new Float((float)100));
        try
        {
            wavToMp3Converter.encode(new File(filepathToConvert), new File(convertedFilePath), att, new EncoderProgressListener()
            {
                @Override
                public void sourceInfo(MultimediaInfo multimediaInfo)
                {

                }

                @Override
                public void progress(int i)
                {

                }

                @Override
                public void message(String s)
                {

                }
            });
        } catch (EncoderException e)
        {
            e.getMessage();

            e.getCause();
        }
    }

    /**
     * This function loads the .WAV song which is to be edited into the RAM memory
     * @param fileToOpen - the file to be edited.
     */
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

    /**
     * This function saves on the hard disk the edited .WAV
     * @param filePath - the path to the directory, where the song is to be saved and the song name
     */
    public void saveSong(String filePath)
    {
        FileOutputStream outputStream = null;
        try
        {
            outputStream = new FileOutputStream("temp.wav");
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

    /**
     * This function saves given array with WAV song to the file with given filepathh
     * @param filePath - the path to the file where the song is to be saved
     * @param wavSong - the array containing song raw data
     */
    public void saveSong(String filePath, byte wavSong[])
    {
        FileOutputStream outputStream = null;
        try
        {
            outputStream = new FileOutputStream(filePath);
            outputStream.write(wavSong);
            outputStream.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This function is responsible for muting the fragment of the currently edited .WAV song.
     * @param startSecond - the start time from which the song is cutted
     * @param endSecond - the end time to which the song is cutted
     */
    public void muteSong(int startSecond, int endSecond)
    {
        int startIndex = wavTagReader.getFirstSampleIndex() + startSecond*wavTagReader.sampleRate*wavTagReader.numOfChannels*wavTagReader
            .bitsPerSample/8;
        int endIndex = wavTagReader.getFirstSampleIndex() + endSecond*wavTagReader.sampleRate*wavTagReader.numOfChannels*wavTagReader.bitsPerSample/8;

        for(int i=startIndex; i<endIndex; i++)
            rawData[i] = 0;
    }

    /**
     * This function cuts the fragment of song, and puts it in another byte array. It assigns also the header of the
     * edited song to the cut fragment
     * @param startSecond - the start second to cut
     * @param endSecond - the end second to cut
     * @return reference to the array with cut fragment.
     */
    public byte[] cutSong(int startSecond, int endSecond)
    {
        int startIndex = wavTagReader.getFirstSampleIndex() + startSecond*wavTagReader.sampleRate*wavTagReader.numOfChannels*wavTagReader
                .bitsPerSample/8;
        int endIndex = wavTagReader.getFirstSampleIndex() + endSecond*wavTagReader.sampleRate*wavTagReader.numOfChannels*wavTagReader.bitsPerSample/8;

        byte cutSong[] = new byte[wavTagReader.getFirstSampleIndex() + (endIndex - startIndex)];

        /// Copy the song header except for data field size
        for(int i=0; i< wavTagReader.getFirstSampleIndex()-4;i++)
        {
            cutSong[i] = rawData[i];
        }

        /// Set the new data size
        int samplesSize = endIndex - startIndex;
        int firstSampleIndex = wavTagReader.getFirstSampleIndex();
        for(int i=0; i<4; i++)
        {
            cutSong[firstSampleIndex-4+i] = (byte)((samplesSize >>> (i*8)) & 0xFF);
        }

        int byteIndex = startIndex;
        /// Copy the data
        for(int i=wavTagReader.getFirstSampleIndex(); i<cutSong.length;i++)
        {
            cutSong[i] = rawData[byteIndex++];
        }

        return cutSong;
    }

    /**
     * This function goes through all samples of the .WAV song and multiplies the values by the given gainFactor in
     * order to achieve the volume changing
     * @param gainFactor - the value which is multiplied with the sample value to get new sample value
     */
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

    public WavTagReader getWavTagReader()
    {
        return wavTagReader;
    }

    public void convertWavToMP3(String filePath)
    {
        String codecs[];
        String formats[];
        Encoder encoder = new Encoder();
        EncodingAttributes att = new EncodingAttributes();
        try
        {
            codecs = encoder.getAudioEncoders();
            formats = encoder.getSupportedEncodingFormats();
            AudioAttributes audioAtt = new AudioAttributes();
            audioAtt.setBitRate(this.wavTagReader.getByteRate()*this.wavTagReader.getBitsPerSample()*this.wavTagReader
                    .getNumOfChannels());
            audioAtt.setChannels(Integer.valueOf(this.wavTagReader.getNumOfChannels()));
            audioAtt.setSamplingRate(this.wavTagReader.getSampleRate());
            audioAtt.setVolume(255);
            audioAtt.setCodec(AudioAttributes.DIRECT_STREAM_COPY );
            att.setAudioAttributes(audioAtt);
            att.setFormat("mp3");
            encoder.encode(this.file, new File(filePath), att);
        } catch (EncoderException e)
        {
            e.printStackTrace();
        }


    }
}
