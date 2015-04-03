package com.example.user.cucomposer_android;

import com.example.user.cucomposer_android.entity.Note;
import com.example.user.cucomposer_android.entity.Part;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Nuttapong on 4/3/2015.
 */
public class AccompanimentGenerator {
    
    private int[] majorScale;
    private int[] minorScale;
    private int key;
    private boolean isMajor;
    
    private Part part;
    private List<Integer> chords = new ArrayList<Integer>();
    
    private int combination;


    public AccompanimentGenerator(Part part,int[] chordSequence){
        this.part = part;
        for (int index = 0; index < chordSequence.length; index++)
        {
            chords.add(chordSequence[index]);
        }
        this.key=part.getKeyPitch();
        this.isMajor=part.getKeyMode();
        majorScale = new int[]{key, 2 + key, 4 + key, 5 + key, 7 + key, 9 + key, 11 + key};
        minorScale = new int[]{key, 2 + key, 3 + key, 5 + key, 7 + key, 8 + key, 10 + key};
    }

    public void generateNotesForBass(int accomStyle){
        ArrayList<Note> bassNoteList = new ArrayList<Note>();
        int[] scale = isMajor ? majorScale : minorScale;
        float offset = 0;
        int octave = 3;
        for(int i = 0 ; i < chords.size();i++) {
            Random ran = new Random();
            accomStyle = ran.nextInt(8) + 0;

            octave = 2;
            int velocity = 100;
            int k = chords.get(i);
            int chordType = k/7;
            int sevenOffset=0;
            if(chordType==3) sevenOffset--;
            k%=7;

            if (accomStyle == 0) {
                int[] step = {0, 0,0,0};
                runStep(scale, step, offset, k, octave, sevenOffset, bassNoteList);
                offset+=4.0f;
            } else if (accomStyle == 1) {
                int[] step = {0, 2, 4, 2};
                if(chordType==2){
                    step[1]=4;
                    step[2]=6;
                    step[3]=4;
                }
                runStep(scale, step,offset, k,octave,sevenOffset, bassNoteList);
                offset+=4.0f;
            } else if (accomStyle == 2) {
                int[] step = {0, 2, 4, 2, 7, 4, 2, 4};
                if(chordType>1){
                    step[4]=6;
                }
                runStep(scale, step,offset, k,octave,sevenOffset, bassNoteList);
                offset+=4.0f;
            }
            else if (accomStyle==3){
                int[] step = {0, 4,0,4};
                runStep(scale, step,offset, k,octave,sevenOffset, bassNoteList);
                offset+=4.0f;
            }
            else if (accomStyle==4){
                int[] step = {0, 4,4,0};
                runStep(scale, step,offset, k,octave,sevenOffset, bassNoteList);
                offset+=4.0f;
            }
            else if (accomStyle==4){
                int[] step = {0, 4,7,0};
                runStep(scale, step,offset, k,octave,sevenOffset, bassNoteList);
                offset+=4.0f;
            }
            else if (accomStyle==5){
                int[] step = {0, 4,7,0};
                runStep(scale, step,offset, k,octave,sevenOffset,bassNoteList);
                offset+=4.0f;
            }
            else if (accomStyle==6){
                int[] step = {0, 0,0,0,0,0,0,0};
                runStep(scale, step,offset, k,octave,sevenOffset, bassNoteList);
                offset+=4.0f;
            }
            else if (accomStyle==7){
                int[] step = {0, 7,7,0};
                runStep(scale, step,offset, k,octave,sevenOffset, bassNoteList);
                offset+=4.0f;
            }
            else if (accomStyle==8){
                int[] step = {0, 0,1,1,2,2,1,1};
                runStep(scale, step,offset, k,octave,sevenOffset, bassNoteList);
                offset+=4.0f;
            }
        }
        part.setBassNoteList(bassNoteList);
    }


    public void generateNotesForPiano(int accomStyle){
        
        ArrayList<Note> pianoNoteList = new ArrayList<Note>();
        
        int[] scale = isMajor ? majorScale : minorScale;
        float offset = 0;

        for(int i = 0 ; i < chords.size();i++){
            int octave = 5;
            int velocity = 100;
            Random ran = new Random();
            accomStyle = ran.nextInt(8) + 0;

            int k = chords.get(i);
            int chordType = k/7;
            int sevenOffset=0;
            if(chordType==3) sevenOffset--;
            k%=7;
            if(accomStyle == 0 ) {
                int[] step = {0,2,4,2};
                if(chordType>1){
                    step[1]=4;
                    step[2]=6;
                    step[3]=4;
                }
                runStep(scale, step, offset, k, octave, sevenOffset, pianoNoteList);
                offset+=4.0f;
            }
            else if (accomStyle==1) {
                for (int j = 0; j < 4; j++) {
                    
                    float duration = 1.0f;
                    
                    pianoNoteList.add(new Note(scale[k]+12*octave,duration,offset));
                    pianoNoteList.add(new Note(scale[(k+2)%7]+12*octave,duration,offset));
                    pianoNoteList.add(new Note(scale[(k+4)%7]+12*octave,duration,offset));
                    if(chordType>1)
                        pianoNoteList.add(new Note(scale[(k+6)%7]+12*octave,duration,offset));
                    
                    offset += 1.0f;
                }
            }
            else if (accomStyle==2) {
                int[] step = {0,2,4,2,7,4,2,4};
//            scale[(k+step[j])%7]+12*octave
                if(chordType>1){
                    step[4]=6;
                }
                runStep(scale, step,offset, k,octave,sevenOffset, pianoNoteList);
                offset+=4.0f;
            }
            else if (accomStyle ==3){
                int[] step = {0, 2,4,2,0,2,4,2};
                runStep(scale, step,offset, k,octave,sevenOffset, pianoNoteList);
                offset+=4.0f;
            }
            else if (accomStyle ==4){
                int[] step = {0, 4,7,8,9,8,7,4};
                runStep(scale, step,offset, k,octave,sevenOffset,pianoNoteList);
                offset+=4.0f;
            }
            else if (accomStyle ==5){
                int[] step = {0, 4,7,4,0,4,7,4};
                runStep(scale, step,offset, k,octave,sevenOffset, pianoNoteList);
                offset+=4.0f;
            }
            else if (accomStyle==6) {
                for (int j = 0; j < 4; j++) {
                    if(j==1){
                        offset+=1.0f;
                        continue;
                    }
                    
                    float duration = 1.0f;

                    pianoNoteList.add(new Note(scale[k]+12*octave,duration,offset));
                    pianoNoteList.add(new Note(scale[(k+2)%7]+12*octave,duration,offset));
                    pianoNoteList.add(new Note(scale[(k+4)%7]+12*octave,duration,offset));
                    if(chordType>1)
                        pianoNoteList.add(new Note(scale[(k+6)%7]+12*octave+sevenOffset,duration,offset));

                    offset += 1.0f;
                }
            }
            else if (accomStyle == 7){
                int[] step = {0, 4,7,4,0,4,7,4};
                runStep(scale, step,offset, k,octave,sevenOffset, pianoNoteList);

                for(int z = 0 ; z < 2 ; z++) {
                    
                    float duration = 2.0f;

                    pianoNoteList.add(new Note(scale[k]+12*octave,duration,offset));
                    pianoNoteList.add(new Note(scale[(k+2)%7]+12*octave,duration,offset));
                    pianoNoteList.add(new Note(scale[(k+4)%7]+12*octave,duration,offset));
                    if(chordType>1)
                        pianoNoteList.add(new Note(scale[(k+6)%7]+12*octave+sevenOffset,duration,offset));

                    offset += 2.0f;
                }
            }
            else if(accomStyle==8){
                for (int j = 0; j < 8; j++) {

                    float duration = 0.5f;

                    pianoNoteList.add(new Note(scale[k]+12*octave,duration,offset));
                    pianoNoteList.add(new Note(scale[(k+2)%7]+12*octave,duration,offset));
                    pianoNoteList.add(new Note(scale[(k+4)%7]+12*octave,duration,offset));
                    if(chordType>1)
                        pianoNoteList.add(new Note(scale[(k+6)%7]+12*octave+sevenOffset,duration,offset));

                    offset += 0.5f;
                }
            }
        }

        part.setPianoNoteList(pianoNoteList);
    }

    public void generateNotesForGuitar(int accomStyle){

        ArrayList<Note> guitarNoteList = new ArrayList<Note>();

        int[] scale = isMajor ? majorScale : minorScale;
        float offset = 0;

        for(int i = 0 ; i < chords.size();i++){
            int octave = 5;
            int velocity = 100;
            Random ran = new Random();
            accomStyle = ran.nextInt(8) + 0;

            int k = chords.get(i);
            int chordType = k/7;
            int sevenOffset=0;
            if(chordType==3) sevenOffset--;
            k%=7;
            if(accomStyle == 0 ) {
                int[] step = {0,2,4,2};
                if(chordType>1){
                    step[1]=4;
                    step[2]=6;
                    step[3]=4;
                }
                runStep(scale, step,offset, k,octave,sevenOffset, guitarNoteList);
                offset+=4.0f;
            }
            else if (accomStyle==1) {
                for (int j = 0; j < 4; j++) {

                    float duration = 1.0f;

                    guitarNoteList.add(new Note(scale[k]+12*octave,duration,offset));
                    guitarNoteList.add(new Note(scale[(k+2)%7]+12*octave,duration,offset));
                    guitarNoteList.add(new Note(scale[(k+4)%7]+12*octave,duration,offset));
                    if(chordType>1)
                        guitarNoteList.add(new Note(scale[(k+6)%7]+12*octave+sevenOffset,duration,offset));

                    offset += 1.0f;
                }
            }
            else if (accomStyle==2) {
                int[] step = {0,2,4,2,7,4,2,4};
//            scale[(k+step[j])%7]+12*octave
                if(chordType>1){
                    step[4]=6;
                }
                runStep(scale, step,offset, k,octave,sevenOffset, guitarNoteList);
                offset+=4.0f;
            }
            else if (accomStyle ==3){
                int[] step = {0, 2,4,2,0,2,4,2};
                runStep(scale, step,offset, k,octave,sevenOffset, guitarNoteList);
                offset+=4.0f;
            }
            else if (accomStyle ==4){
                int[] step = {0, 4,7,8,9,8,7,4};
                runStep(scale, step,offset, k,octave,sevenOffset, guitarNoteList);
                offset+=4.0f;
            }
            else if (accomStyle ==5){
                int[] step = {0, 4,7,4,0,4,7,4};
                runStep(scale, step,offset, k,octave,sevenOffset, guitarNoteList);
                offset+=4.0f;
            }
            else if (accomStyle==6) {
                for (int j = 0; j < 4; j++) {
                    if(j==1){
                        offset+=1.0f;
                        continue;
                    }

                    float duration = 1.0f;

                    guitarNoteList.add(new Note(scale[k]+12*octave,duration,offset));
                    guitarNoteList.add(new Note(scale[(k+2)%7]+12*octave,duration,offset));
                    guitarNoteList.add(new Note(scale[(k+4)%7]+12*octave,duration,offset));
                    if(chordType>1)
                        guitarNoteList.add(new Note(scale[(k+6)%7]+12*octave+sevenOffset,duration,offset));

                    offset += 1.0f;
                }
            }
            else if (accomStyle == 7){
                int[] step = {0, 4,7,4,0,4,7,4};
                runStep(scale, step,offset, k,octave,sevenOffset, guitarNoteList);

                for(int z = 0 ; z < 2 ; z++) {
                    float duration = 2.0f;

                    guitarNoteList.add(new Note(scale[k]+12*octave,duration,offset));
                    guitarNoteList.add(new Note(scale[(k+2)%7]+12*octave,duration,offset));
                    guitarNoteList.add(new Note(scale[(k+4)%7]+12*octave,duration,offset));
                    if(chordType>1)
                        guitarNoteList.add(new Note(scale[(k+6)%7]+12*octave+sevenOffset,duration,offset));

                    offset += 2.0f;
                }
            }
            else if(accomStyle==8){
                for (int j = 0; j < 8; j++) {
                    float duration = 0.5f;

                    guitarNoteList.add(new Note(scale[k]+12*octave,duration,offset));
                    guitarNoteList.add(new Note(scale[(k+2)%7]+12*octave,duration,offset));
                    guitarNoteList.add(new Note(scale[(k+4)%7]+12*octave,duration,offset));
                    if(chordType>1)
                        guitarNoteList.add(new Note(scale[(k+6)%7]+12*octave+sevenOffset,duration,offset));

                    offset += 0.5f;
                }
            }
        }
        part.setGuitarNoteList(guitarNoteList);
    }




    public void runStep(int[] scale, int[] step, float offset, int k,int octave, int sevenOffset, List<Note> notes){
        for (int j = 0; j < step.length; j++) {
            float duration = 4.0f/step.length;

            int note = scale[(k + step[j]) % 7] + 12 * (octave + (k + step[j]) / 7) ;
            if(step[j]==6)note+=sevenOffset;

            notes.add(new Note(note, 4.0f / step.length, offset));

            offset += duration;
        }
    }
    
}
