package com.example.user.cucomposer_android.utility;

import com.example.user.cucomposer_android.entity.Note;

import java.util.List;


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

    public static int[] ProjectNotes(List<Note> notes,int keyPitch,boolean keyMode){
        Note lastNote = notes.get(notes.size()-1);
        //System.out.println("last note: "+lastNote.getOffset()+" "+lastNote.getDuration());
        int[] projectedNotes = new int[((int)(Math.ceil((lastNote.getOffset()+lastNote.getDuration())/4)))*16];
        for(int i=0;i<notes.size();i++){
            Note aNote = notes.get(i);
            if(aNote.getPitch()<0){
                continue;
            }
            for(int j=(int)(aNote.getOffset()*4);j<(int)((aNote.getOffset()+aNote.getDuration())*4);j++){
                projectedNotes[j] = Key.mapToKey(aNote.getPitch(),keyPitch,keyMode)+1;
            }
        }
        return projectedNotes;
    }
}
