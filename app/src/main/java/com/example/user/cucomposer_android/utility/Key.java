package com.example.user.cucomposer_android.utility;

/**
 * Created by Nuttapong on 2/15/2015.
 */
public class Key {

    public static final boolean MAJOR = true;
    public static final boolean MINOR = !MAJOR;

    public static final int[] MAJOR_SEQUENCE = {0,2,4,5,7,9,11};
    public static final int[] MINOR_SEQUENCE = {0,2,3,5,7,8,10};

    public static int mapToKey(int notePitch,int keyPitch,boolean mode){
        int[] seq;
        int ref = (notePitch-keyPitch)%12;
        if(mode == MAJOR){
            seq = MAJOR_SEQUENCE;
        }
        else{
            seq = MINOR_SEQUENCE;
        }
        for(int i=0;i<seq.length;i++){
            if(ref<=seq[i]){
                return i;
            }
        }
        return 0;
    }

    public static int mapBackToPitch(int noteKey,int keyPitch,boolean mode){
        int[] seq;
        if(mode == MAJOR){
            seq = MAJOR_SEQUENCE;
        }
        else{
            seq = MINOR_SEQUENCE;
        }
        return (seq[noteKey%7]+keyPitch)%12;
    }
}
