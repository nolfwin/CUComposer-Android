package com.example.user.cucomposer_android;

import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import be.tarsos.dsp.io.android.AndroidAudioInputStream;

/**
 * Created by Wongse on 22/1/2558.
 */
public class MatlabMethod {
    public static float[] wavRead(String directory) {
        float[] audioFloats = new float[1];
        try {
            File file = new File(Environment.getExternalStorageDirectory(), directory);
            FileInputStream fileInputStream = new FileInputStream(file);

            int read;
            byte[] buff = new byte[1024];

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            try {
                // the line below is for creating a wav file from recording
                while ((read = fileInputStream.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            byte[] audioBytes = out.toByteArray();

            ShortBuffer sbuf = ByteBuffer.wrap(audioBytes)
                    .order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
            short[] audioShorts = new short[sbuf.capacity()];
            sbuf.get(audioShorts);
            audioFloats = new float[audioShorts.length];
            for (int i = 0; i < audioShorts.length; i++) {
                audioFloats[i] = ((float) audioShorts[i]) / 0x8000;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return audioFloats;
    }
    public static float[] convertToFloats(byte[] audioBytes){
        ShortBuffer sbuf =
                ByteBuffer.wrap(audioBytes).order(ByteOrder.BIG_ENDIAN).asShortBuffer();
        short[] audioShorts = new short[sbuf.capacity()];
        sbuf.get(audioShorts);
        float[] audioFloats = new float[audioShorts.length];
        for (int i = 0; i < audioShorts.length; i++) {
            audioFloats[i] = ((float)audioShorts[i])/0x8000;
        }
        return audioFloats;
    }
    public static byte[] convertToBytes(float[] audioFloats){

        short[] shortsArray = new short[audioFloats.length];
        for(int i = 0 ; i < shortsArray.length;i++){
            shortsArray[i] = (short)((audioFloats[i])*0x8000);
        }

        byte[] bytesArray = new byte[shortsArray.length * 2];
        ByteBuffer.wrap(bytesArray).order(ByteOrder.BIG_ENDIAN).asShortBuffer().put(shortsArray);
        return bytesArray;
    }

    public static List<Integer> segment(float[] amp){
        List<Integer> start = new ArrayList<Integer>();
        int k = 0;
        int size = 330; int inc = 110;
        boolean voiced = false;
        double max1=0, max2=0, max3=0, max4=0;
        int startIndex = 0;
        while(startIndex<amp.length){
            float e = 0;
            float zcr = 0;
            int i;
            for(i=startIndex;i<startIndex+size && i<amp.length;i++){
                //e
                e += amp[i]*amp[i];
                //zcr
                if(i>0 && ((amp[i]<0 && amp[i-1]>0) || (amp[i]>0 && amp[i-1]<0))){
                    zcr++;
                }
            }
            if(i==amp.length){
                e /= 220 + amp.length % inc;
                zcr /= 220 + amp.length % inc;
            }
            else{
                e /= size;
                zcr /= size;
            }
            if(e > max1)
                max1 = e;
            if(zcr > max2)
                max2 = zcr;
            if(e*zcr > max3)
                max3 = e*zcr;
            if(e/zcr > max4)
                max4 = e/zcr;
            startIndex += inc;
        }
        startIndex = 0;
        while(startIndex<amp.length){
            float e = 0;
            float zcr = 0;
            int i;
            for(i=startIndex;i<startIndex+size && i<amp.length;i++){
                //e
                e += amp[i]*amp[i];
                //zcr
                if(i>0 && ((amp[i]<0 && amp[i-1]>0) || (amp[i]>0 && amp[i-1]<0))){
                    zcr++;
                }
            }
            if(i==amp.length){
                e /= 220 + amp.length % inc;
                zcr /= 220 + amp.length % inc;
            }
            else{
                e /= size;
                zcr /= size;
            }

            System.out.println("start: " + startIndex
                    + "e: " + e
                    + " zcr: " + zcr
                    + " e*zcr: " + e*zcr
                    + " e/zcr: " + e/zcr);
            System.out.println("start: " + startIndex
                    + "e%: " + e/max1*100
                    + " zcr%: " + zcr/max2*100
                    + " e*zcr%: " + e*zcr/max3*100
                    + " e/zcr%: " + e/zcr/max4*100);
            if(voiced && (e*zcr < 0.0015 || (e*zcr < 0.01 && e/zcr < 1))){
                start.add(startIndex);
                voiced = false;
            }
            else if(!voiced && !(e*zcr < 0.0015 || (e*zcr < 0.01 && e/zcr < 1))){
                start.add(startIndex);
                voiced = true;
            }
            startIndex += inc;
        }
        return start;
    }
}
