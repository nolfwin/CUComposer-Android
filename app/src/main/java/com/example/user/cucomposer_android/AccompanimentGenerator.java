package com.example.user.cucomposer_android;

import com.example.user.cucomposer_android.entity.Note;
import com.example.user.cucomposer_android.entity.Part;
import com.example.user.cucomposer_android.utility.NotesUtil;

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
    public void generateNotesForBass(int accomStyle,int partStyle){
        ArrayList<Note> bassNoteList = new ArrayList<Note>();

        int[] scale = isMajor ? majorScale : minorScale;
        float offset = 0;
        int bassPenal = 12;
        if(accomStyle == -1 ) {
            if(partStyle==0){
             // intro

               for(int j = 0 ; j < 4 ; j++){
                   bassNoteList.add(new Note(29-bassPenal,1.0f));
               }
                for(int j = 0 ; j < 4 ; j++){
                    bassNoteList.add(new Note(34-bassPenal,1.0f));
                }
                for(int j = 0 ; j < 4 ; j++){
                    bassNoteList.add(new Note(27-bassPenal,1.0f));
                }
                for(int j = 0 ; j < 4 ; j++){
                    bassNoteList.add(new Note(32-bassPenal,1.0f));
                }

                NotesUtil.calculateOffset(bassNoteList);
                part.setBassNoteList(bassNoteList);
                return;
            }
            else if(partStyle ==1){// verse
                for(int k = 0 ;k < 2 ; k++) {
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(29-bassPenal, 0.5f));
                    }
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(34-bassPenal, 0.5f));
                    }
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(27-bassPenal, 0.5f));
                    }
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(32-bassPenal, 0.5f));
                    }
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(25-bassPenal, 0.5f));
                    }
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(34-bassPenal, 0.5f));
                    }
                    bassNoteList.add(new Note(36-bassPenal, 2.0f));
                    bassNoteList.add(new Note(24-bassPenal, 0.5f));
                    bassNoteList.add(new Note(24-bassPenal, 0.5f));
                    bassNoteList.add(new Note(24-bassPenal, 1.0f));
                }
                NotesUtil.calculateOffset(bassNoteList);
                part.setBassNoteList(bassNoteList);
                return;
            }
            else if(partStyle ==2){// prechorus
                for(int k = 0 ;k < 2 ; k++) {
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(29-bassPenal, 0.5f));
                    }
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(25-bassPenal, 0.5f));
                    }
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(27-bassPenal, 0.5f));
                    }
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(32-bassPenal, 0.5f));
                    }
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(25-bassPenal, 0.5f));
                    }
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(34-bassPenal, 0.5f));
                    }
                    bassNoteList.add(new Note(36-bassPenal, 2.0f));
                    bassNoteList.add(new Note(24-bassPenal, 0.5f));
                    bassNoteList.add(new Note(24-bassPenal, 0.5f));
                    bassNoteList.add(new Note(24-bassPenal, 1.0f));
                }
                NotesUtil.calculateOffset(bassNoteList);
                part.setBassNoteList(bassNoteList);
                return;
            }
            else if (partStyle==3){// chorus
                bassNoteList.add(new Note(32,0.5f));
                bassNoteList.add(new Note(31,0.5f));
                bassNoteList.add(new Note(29,0.5f));
                bassNoteList.add(new Note(27,0.5f));
                bassNoteList.add(new Note(25,0.5f));
                bassNoteList.add(new Note(24,0.5f));
                bassNoteList.add(new Note(22,0.5f));
                bassNoteList.add(new Note(20,0.5f));
                for(int k = 0 ;k < 2 ; k++) {
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(29-bassPenal, 0.5f));
                    }
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(34-bassPenal, 0.5f));
                    }
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(27-bassPenal, 0.5f));
                    }
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(32-bassPenal, 0.5f));
                    }
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(25-bassPenal, 0.5f));
                    }
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(34-bassPenal, 0.5f));
                    }
                    bassNoteList.add(new Note(36-bassPenal, 2.0f));
                    bassNoteList.add(new Note(24-bassPenal, 0.5f));
                    bassNoteList.add(new Note(24-bassPenal, 0.5f));
                    bassNoteList.add(new Note(24-bassPenal, 1.0f));
                }
                NotesUtil.calculateOffset(bassNoteList);
                part.setBassNoteList(bassNoteList);
                return;            }
            else if(partStyle==4){// solo
                for(int k = 0 ;k < 2 ; k++) {
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(29-bassPenal, 0.5f));
                    }
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(34-bassPenal, 0.5f));
                    }
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(27-bassPenal, 0.5f));
                    }
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(32-bassPenal, 0.5f));
                    }
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(25-bassPenal, 0.5f));
                    }
                    for (int j = 0; j < 4; j++) {
                        bassNoteList.add(new Note(34-bassPenal, 0.5f));
                    }
                    bassNoteList.add(new Note(36-bassPenal, 2.0f));
                    bassNoteList.add(new Note(24-bassPenal, 0.5f));
                    bassNoteList.add(new Note(24-bassPenal, 0.5f));
                    bassNoteList.add(new Note(24-bassPenal, 1.0f));
                }
                NotesUtil.calculateOffset(bassNoteList);
                part.setBassNoteList(bassNoteList);
                return;
            }
        }
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

    public void generateNotesForPiano(int accomStyle,int partStyle){

        ArrayList<Note> pianoNoteList = new ArrayList<Note>();

        int[] scale = isMajor ? majorScale : minorScale;
        float offset = 0;
        if(accomStyle == -1 ) {
            if(partStyle==0){  // intro
                pianoNoteList.add(new Note(29,0.5f));
                pianoNoteList.add(new Note(36,0.5f));
                pianoNoteList.add(new Note(41,0.5f));
                pianoNoteList.add(new Note(43,0.5f));
                pianoNoteList.add(new Note(-1,1.0f));
                pianoNoteList.add(new Note(36,0.5f));
                pianoNoteList.add(new Note(41,0.5f));
                pianoNoteList.add(new Note(34,0.5f));
                pianoNoteList.add(new Note(37,0.5f));
                pianoNoteList.add(new Note(46,0.5f));
                pianoNoteList.add(new Note(48,0.5f));
                pianoNoteList.add(new Note(-1,1.0f));
                pianoNoteList.add(new Note(37,0.5f));
                pianoNoteList.add(new Note(46,0.5f));
                pianoNoteList.add(new Note(27,0.5f));
                pianoNoteList.add(new Note(34,0.5f));
                pianoNoteList.add(new Note(39,0.5f));
                pianoNoteList.add(new Note(41,0.5f));
                pianoNoteList.add(new Note(34,1.0f));
                pianoNoteList.add(new Note(39,0.5f));
                pianoNoteList.add(new Note(32,0.5f));
                pianoNoteList.add(new Note(39,0.5f));
                pianoNoteList.add(new Note(44,0.5f));
                pianoNoteList.add(new Note(46,0.5f));
                pianoNoteList.add(new Note(48,0.5f));
                pianoNoteList.add(new Note(48,0.5f));
                pianoNoteList.add(new Note(46,0.5f));
                pianoNoteList.add(new Note(39,0.5f));
                NotesUtil.calculateOffset(pianoNoteList);
                for(int i = 0 ; i < pianoNoteList.size();i++){
                    int pitch = pianoNoteList.get(i).getPitch();
                    if(pianoNoteList.get(i).getPitch()>0)pianoNoteList.get(i).setPitch(pitch+24);
                }
                part.setPianoNoteList(pianoNoteList);
                return;
            }
            else if(partStyle ==1){// verse
                pianoNoteList.add(new Note(29,0.5f));
                pianoNoteList.add(new Note(32,0.5f));
                pianoNoteList.add(new Note(36,0.5f));
                pianoNoteList.add(new Note(32,0.5f));
                pianoNoteList.add(new Note(29,0.5f));
                pianoNoteList.add(new Note(34,0.5f));
                pianoNoteList.add(new Note(37,0.5f));
                pianoNoteList.add(new Note(34,0.5f));
                pianoNoteList.add(new Note(27,0.5f));
                pianoNoteList.add(new Note(31,0.5f));
                pianoNoteList.add(new Note(34,0.5f));
                pianoNoteList.add(new Note(31,0.5f));
                pianoNoteList.add(new Note(32,0.5f));
                pianoNoteList.add(new Note(36,0.5f));
                pianoNoteList.add(new Note(32,0.5f));
                pianoNoteList.add(new Note(36,0.5f));
                pianoNoteList.add(new Note(25,0.5f));
                pianoNoteList.add(new Note(29,0.5f));
                pianoNoteList.add(new Note(32,0.5f));
                pianoNoteList.add(new Note(29,0.5f));
                pianoNoteList.add(new Note(34,0.5f));
                pianoNoteList.add(new Note(37,0.5f));
                pianoNoteList.add(new Note(41,0.5f));
                pianoNoteList.add(new Note(37,0.5f));
                NotesUtil.calculateOffset(pianoNoteList);
                 offset = 12.0f;
                pianoNoteList.add(new Note(24,2.0f,offset));
                pianoNoteList.add(new Note(28,2.0f,offset));
                pianoNoteList.add(new Note(31,2.0f,offset));
                pianoNoteList.add(new Note(24,0.5f,offset+2.0f));
                pianoNoteList.add(new Note(28,0.5f,offset+2.0f));
                pianoNoteList.add(new Note(31,0.5f,offset+2.0f));
                pianoNoteList.add(new Note(24,0.5f,offset+2.5f));
                pianoNoteList.add(new Note(28,0.5f,offset+2.5f));
                pianoNoteList.add(new Note(31,0.5f,offset+2.5f));
                pianoNoteList.add(new Note(24,1.0f,offset+3.0f));
                pianoNoteList.add(new Note(28,1.0f,offset+3.0f));
                pianoNoteList.add(new Note(31,1.0f,offset+3.0f));
                offset+=4.0f;
                ArrayList<Note> addNoteList = new ArrayList<Note>();
                addNoteList.add(new Note(29,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(32,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(36,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(32,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(29,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(34,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(37,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(34,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(27,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(31,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(34,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(31,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(32,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(36,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(32,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(36,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(25,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(29,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(32,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(29,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(34,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(37,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(41,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(37,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(24,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(28,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(31,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(28,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(24,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(28,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(31,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(28,0.5f,offset));
                pianoNoteList.addAll(addNoteList);
                for(int i = 0 ; i < pianoNoteList.size();i++){
                    int pitch = pianoNoteList.get(i).getPitch();
                    if(pianoNoteList.get(i).getPitch()>0)pianoNoteList.get(i).setPitch(pitch+24);
                }
                part.setPianoNoteList(pianoNoteList);
                return;
            }
            else if(partStyle ==2){// prechorus

                    pianoNoteList.add(new Note(29, 0.5f));
                    pianoNoteList.add(new Note(36, 0.5f));
                    pianoNoteList.add(new Note(41, 0.5f));
                    pianoNoteList.add(new Note(44, 0.5f));
                    pianoNoteList.add(new Note(25, 0.5f));
                    pianoNoteList.add(new Note(32, 0.5f));
                    pianoNoteList.add(new Note(37, 0.5f));
                    pianoNoteList.add(new Note(41, 0.5f));
                    pianoNoteList.add(new Note(27, 0.5f));
                    pianoNoteList.add(new Note(34, 0.5f));
                    pianoNoteList.add(new Note(39, 0.5f));
                    pianoNoteList.add(new Note(43, 0.5f));
                    pianoNoteList.add(new Note(32, 0.5f));
                    pianoNoteList.add(new Note(39, 0.5f));
                    pianoNoteList.add(new Note(44, 0.5f));
                    pianoNoteList.add(new Note(48, 0.5f));
                    pianoNoteList.add(new Note(25, 0.5f));
                    pianoNoteList.add(new Note(32, 0.5f));
                    pianoNoteList.add(new Note(37, 0.5f));
                    pianoNoteList.add(new Note(41, 0.5f));
                    pianoNoteList.add(new Note(34, 0.5f));
                    pianoNoteList.add(new Note(37, 0.5f));
                    pianoNoteList.add(new Note(41, 0.5f));
                    pianoNoteList.add(new Note(37, 0.5f));
                    NotesUtil.calculateOffset(pianoNoteList);
                    offset = 12.0f;
                    pianoNoteList.add(new Note(24, 2.0f, offset));
                    pianoNoteList.add(new Note(28, 2.0f, offset));
                    pianoNoteList.add(new Note(31, 2.0f, offset));
                    pianoNoteList.add(new Note(24, 0.5f, offset + 2.0f));
                    pianoNoteList.add(new Note(28, 0.5f, offset + 2.0f));
                    pianoNoteList.add(new Note(31, 0.5f, offset + 2.0f));
                    pianoNoteList.add(new Note(24, 0.5f, offset + 2.5f));
                    pianoNoteList.add(new Note(28, 0.5f, offset + 2.5f));
                    pianoNoteList.add(new Note(31, 0.5f, offset + 2.5f));
                    pianoNoteList.add(new Note(24, 1.0f, offset + 3.0f));
                    pianoNoteList.add(new Note(28, 1.0f, offset + 3.0f));
                    pianoNoteList.add(new Note(31, 1.0f, offset + 3.0f));
                    offset += 4.0f;
                ArrayList<Note> addNoteList = new ArrayList<Note>();
                addNoteList.add(new Note(29,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(36,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(41,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(44,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(25,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(32,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(37,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(41,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(27,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(34,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(39,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(43,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(32,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(39,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(44,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(48,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(25,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(32,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(37,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(41,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(34,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(37,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(41,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(37,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(24, 2.0f, offset));
                addNoteList.add(new Note(28, 2.0f, offset));
                addNoteList.add(new Note(31, 2.0f, offset));
                addNoteList.add(new Note(24, 0.5f, offset + 2.0f));
                addNoteList.add(new Note(28, 0.5f, offset + 2.0f));
                addNoteList.add(new Note(31, 0.5f, offset + 2.0f));
                addNoteList.add(new Note(24, 0.5f, offset + 2.5f));
                addNoteList.add(new Note(28, 0.5f, offset + 2.5f));
                addNoteList.add(new Note(31, 0.5f, offset + 2.5f));
                addNoteList.add(new Note(24, 1.0f, offset + 3.0f));
                addNoteList.add(new Note(28, 1.0f, offset + 3.0f));
                addNoteList.add(new Note(31, 1.0f, offset + 3.0f));
                pianoNoteList.addAll(addNoteList);
                for(int i = 0 ; i < pianoNoteList.size();i++){
                    int pitch = pianoNoteList.get(i).getPitch();
                    if(pianoNoteList.get(i).getPitch()>0)pianoNoteList.get(i).setPitch(pitch+24);
                }
                part.setPianoNoteList(pianoNoteList);
                return;
            }
            else if (partStyle==3){// chorus
                pianoNoteList.add(new Note(44,0.5f));
                pianoNoteList.add(new Note(43,0.5f));
                pianoNoteList.add(new Note(41,0.5f));
                pianoNoteList.add(new Note(39,0.5f));
                pianoNoteList.add(new Note(37,0.5f));
                pianoNoteList.add(new Note(36,0.5f));
                pianoNoteList.add(new Note(34,0.5f));
                pianoNoteList.add(new Note(32,0.5f));
                pianoNoteList.add(new Note(29,0.5f));
                pianoNoteList.add(new Note(36,0.5f));
                pianoNoteList.add(new Note(41,0.5f));
                pianoNoteList.add(new Note(29,0.5f));
                pianoNoteList.add(new Note(34,0.5f));
                pianoNoteList.add(new Note(41,0.5f));
                pianoNoteList.add(new Note(46,0.5f));
                pianoNoteList.add(new Note(41,0.5f));
                pianoNoteList.add(new Note(27,0.5f));
                pianoNoteList.add(new Note(34,0.5f));
                pianoNoteList.add(new Note(39,0.5f));
                pianoNoteList.add(new Note(34,0.5f));
                pianoNoteList.add(new Note(32,0.5f));
                pianoNoteList.add(new Note(39,0.5f));
                pianoNoteList.add(new Note(43,0.5f));
                pianoNoteList.add(new Note(39,0.5f));
                pianoNoteList.add(new Note(25,0.5f));
                pianoNoteList.add(new Note(32,0.5f));
                pianoNoteList.add(new Note(37,0.5f));
                pianoNoteList.add(new Note(32,0.5f));
                pianoNoteList.add(new Note(34,0.5f));
                pianoNoteList.add(new Note(41,0.5f));
                pianoNoteList.add(new Note(46,0.5f));
                pianoNoteList.add(new Note(41,0.5f));
                NotesUtil.calculateOffset(pianoNoteList);
                offset = 16.0f;
                pianoNoteList.add(new Note(24, 2.0f, offset));
                pianoNoteList.add(new Note(28, 2.0f, offset));
                pianoNoteList.add(new Note(31, 2.0f, offset));
                pianoNoteList.add(new Note(24, 0.5f, offset + 2.0f));
                pianoNoteList.add(new Note(28, 0.5f, offset + 2.0f));
                pianoNoteList.add(new Note(31, 0.5f, offset + 2.0f));
                pianoNoteList.add(new Note(24, 0.5f, offset + 2.5f));
                pianoNoteList.add(new Note(28, 0.5f, offset + 2.5f));
                pianoNoteList.add(new Note(31, 0.5f, offset + 2.5f));
                pianoNoteList.add(new Note(24, 1.0f, offset + 3.0f));
                pianoNoteList.add(new Note(28, 1.0f, offset + 3.0f));
                pianoNoteList.add(new Note(31, 1.0f, offset + 3.0f));
                offset += 4.0f;
                ArrayList<Note> addNoteList = new ArrayList<Note>();
                addNoteList.add(new Note(29,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(36,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(41,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(29,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(34,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(41,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(46,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(41,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(27,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(34,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(39,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(34,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(32,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(39,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(43,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(39,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(25,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(32,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(37,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(32,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(34,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(41,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(46,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(41,0.5f,offset));
                offset+=0.5f;
                addNoteList.add(new Note(24, 2.0f, offset));
                addNoteList.add(new Note(28, 2.0f, offset));
                addNoteList.add(new Note(31, 2.0f, offset));
                addNoteList.add(new Note(24, 0.5f, offset + 2.0f));
                addNoteList.add(new Note(28, 0.5f, offset + 2.0f));
                addNoteList.add(new Note(31, 0.5f, offset + 2.0f));
                addNoteList.add(new Note(24, 0.5f, offset + 2.5f));
                addNoteList.add(new Note(28, 0.5f, offset + 2.5f));
                addNoteList.add(new Note(31, 0.5f, offset + 2.5f));
                addNoteList.add(new Note(24, 1.0f, offset + 3.0f));
                addNoteList.add(new Note(28, 1.0f, offset + 3.0f));
                addNoteList.add(new Note(31, 1.0f, offset + 3.0f));
                offset+=4.0f;
                addNoteList.add(new Note(29, 0.5f, offset));
                offset+=0.5f;
                addNoteList.add(new Note(32, 0.5f, offset));
                offset+=0.5f;
                addNoteList.add(new Note(36, 0.5f, offset));
                offset+=0.5f;
                addNoteList.add(new Note(32,0.5f, offset));
                offset+=0.5f;
                addNoteList.add(new Note(29, 0.5f, offset));
                offset+=0.5f;
                addNoteList.add(new Note(32,0.5f, offset));
                offset+=0.5f;
                addNoteList.add(new Note(36,0.5f, offset));
                offset+=0.5f;
                addNoteList.add(new Note(32,0.5f, offset));

                pianoNoteList.addAll(addNoteList);
                for(int i = 0 ; i < pianoNoteList.size();i++){
                    int pitch = pianoNoteList.get(i).getPitch();
                    if(pianoNoteList.get(i).getPitch()>0)pianoNoteList.get(i).setPitch(pitch+24);
                }
                part.setPianoNoteList(pianoNoteList);
                return;
            }
            else if(partStyle==4){// solo
                pianoNoteList.add(new Note(29,0.5f));
                pianoNoteList.add(new Note(36,0.5f));
                pianoNoteList.add(new Note(41,0.5f));
                pianoNoteList.add(new Note(29,0.5f));
                pianoNoteList.add(new Note(34,0.5f));
                pianoNoteList.add(new Note(41,0.5f));
                pianoNoteList.add(new Note(46,0.5f));
                pianoNoteList.add(new Note(41,0.5f));
                pianoNoteList.add(new Note(29,0.5f));
                pianoNoteList.add(new Note(34,0.5f));
                pianoNoteList.add(new Note(39,0.5f));
                pianoNoteList.add(new Note(34,0.5f));
                pianoNoteList.add(new Note(32,0.5f));
                pianoNoteList.add(new Note(39,0.5f));
                pianoNoteList.add(new Note(43,0.5f));
                pianoNoteList.add(new Note(41,0.5f));
                pianoNoteList.add(new Note(25,0.5f));
                pianoNoteList.add(new Note(32,0.5f));
                pianoNoteList.add(new Note(37,0.5f));
                pianoNoteList.add(new Note(32,0.5f));
                pianoNoteList.add(new Note(34,0.5f));
                pianoNoteList.add(new Note(41,0.5f));
                pianoNoteList.add(new Note(46,0.5f));
                pianoNoteList.add(new Note(41,0.5f));
                pianoNoteList.add(new Note(24,0.5f));
                pianoNoteList.add(new Note(31,0.5f));
                pianoNoteList.add(new Note(36,0.5f));
                pianoNoteList.add(new Note(31,0.5f));
                pianoNoteList.add(new Note(24,0.5f));
                pianoNoteList.add(new Note(31,0.5f));
                pianoNoteList.add(new Note(36,0.5f));
                pianoNoteList.add(new Note(31,0.5f));
                pianoNoteList.add(new Note(29,0.5f));
                pianoNoteList.add(new Note(36,0.5f));
                pianoNoteList.add(new Note(41,0.5f));
                pianoNoteList.add(new Note(29,0.5f));
                pianoNoteList.add(new Note(34,0.5f));
                pianoNoteList.add(new Note(41,0.5f));
                pianoNoteList.add(new Note(46,0.5f));
                pianoNoteList.add(new Note(41,0.5f));
                pianoNoteList.add(new Note(29,0.5f));
                pianoNoteList.add(new Note(34,0.5f));
                pianoNoteList.add(new Note(39,0.5f));
                pianoNoteList.add(new Note(34,0.5f));
                pianoNoteList.add(new Note(32,0.5f));
                pianoNoteList.add(new Note(39,0.5f));
                pianoNoteList.add(new Note(43,0.5f));
                pianoNoteList.add(new Note(41,0.5f));
                pianoNoteList.add(new Note(25,0.5f));
                pianoNoteList.add(new Note(32,0.5f));
                pianoNoteList.add(new Note(37,0.5f));
                pianoNoteList.add(new Note(32,0.5f));
                pianoNoteList.add(new Note(34,0.5f));
                pianoNoteList.add(new Note(41,0.5f));
                pianoNoteList.add(new Note(46,0.5f));
                pianoNoteList.add(new Note(41,0.5f));
                pianoNoteList.add(new Note(24,0.5f));
                pianoNoteList.add(new Note(31,0.5f));
                pianoNoteList.add(new Note(36,0.5f));
                pianoNoteList.add(new Note(31,0.5f));
                pianoNoteList.add(new Note(24,0.5f));
                pianoNoteList.add(new Note(31,0.5f));
                pianoNoteList.add(new Note(36,0.5f));
                pianoNoteList.add(new Note(31,0.5f));
                NotesUtil.calculateOffset(pianoNoteList);
                for(int i = 0 ; i < pianoNoteList.size();i++){
                    int pitch = pianoNoteList.get(i).getPitch();
                    if(pianoNoteList.get(i).getPitch()>0)pianoNoteList.get(i).setPitch(pitch+24);
                }
                part.setPianoNoteList(pianoNoteList);
                return;
            }
        }
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
    public void generateNotesForGuitar(int accomStyle,int partStyle){

        ArrayList<Note> guitarNoteList = new ArrayList<Note>();


        int[] scale = isMajor ? majorScale : minorScale;
        float offset = 0;
        int bassPenal = 12;
        if(accomStyle == -1 ) {
            if(partStyle==0){
                // intro
                guitarNoteList.add(new Note(41,1.5f, offset));
                guitarNoteList.add(new Note(44, 1.5f, offset));
                guitarNoteList.add(new Note(48, 1.5f, offset));
                guitarNoteList.add(new Note(41, 2.0f, offset + 2.0f));
                guitarNoteList.add(new Note(44, 2.0f, offset + 2.0f));
                guitarNoteList.add(new Note(48, 2.0f, offset +2.0f));
                offset+=4;
                guitarNoteList.add(new Note(34,1.5f, offset));
                guitarNoteList.add(new Note(37,1.5f, offset));
                guitarNoteList.add(new Note(41,1.5f, offset));
                guitarNoteList.add(new Note(34, 2.0f, offset + 2.0f));
                guitarNoteList.add(new Note(37, 2.0f, offset + 2.0f));
                guitarNoteList.add(new Note(41, 2.0f, offset +2.0f));
                offset+=4;
                guitarNoteList.add(new Note(39,1.5f, offset));
                guitarNoteList.add(new Note(43,1.5f, offset));
                guitarNoteList.add(new Note(46,1.5f, offset));
                guitarNoteList.add(new Note(39, 2.0f, offset + 2.0f));
                guitarNoteList.add(new Note(43, 2.0f, offset + 2.0f));
                guitarNoteList.add(new Note(46, 2.0f, offset +2.0f));
                offset+=4;
                guitarNoteList.add(new Note(32,1.5f, offset));
                guitarNoteList.add(new Note(36,1.5f, offset));
                guitarNoteList.add(new Note(39,1.5f, offset));
                guitarNoteList.add(new Note(32, 2.0f, offset + 2.0f));
                guitarNoteList.add(new Note(36, 2.0f, offset + 2.0f));
                guitarNoteList.add(new Note(39, 2.0f, offset +2.0f));

                part.setGuitarNoteList(guitarNoteList);
                return;
            }
            else if(partStyle ==1){// verse

                for(int k = 0 ; k < 2 ; k++) {
                    guitarNoteList.add(new Note(41, 1.0f, offset));
                    guitarNoteList.add(new Note(44, 1.0f, offset));
                    guitarNoteList.add(new Note(48, 1.0f, offset));
                    guitarNoteList.add(new Note(41, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(44, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(48, 1.0f, offset + 1.0f));
                    offset += 2;
                    guitarNoteList.add(new Note(34, 1.0f, offset));
                    guitarNoteList.add(new Note(37, 1.0f, offset));
                    guitarNoteList.add(new Note(41, 1.0f, offset));
                    guitarNoteList.add(new Note(34, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(37, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(41, 1.0f, offset + 1.0f));
                    offset += 2;

                    guitarNoteList.add(new Note(39, 1.0f, offset));
                    guitarNoteList.add(new Note(43, 1.0f, offset));
                    guitarNoteList.add(new Note(46, 1.0f, offset));
                    guitarNoteList.add(new Note(39, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(43, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(46, 1.0f, offset + 1.0f));
                    offset += 2;
                    guitarNoteList.add(new Note(32, 1.0f, offset));
                    guitarNoteList.add(new Note(36, 1.0f, offset));
                    guitarNoteList.add(new Note(39, 1.0f, offset));
                    guitarNoteList.add(new Note(32, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(36, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(39, 1.0f, offset + 1.0f));
                    offset += 2;

                    guitarNoteList.add(new Note(37, 1.0f, offset));
                    guitarNoteList.add(new Note(41, 1.0f, offset));
                    guitarNoteList.add(new Note(44, 1.0f, offset));
                    guitarNoteList.add(new Note(37, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(41, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(44, 1.0f, offset + 1.0f));
                    offset += 2;
                    guitarNoteList.add(new Note(34, 1.0f, offset));
                    guitarNoteList.add(new Note(37, 1.0f, offset));
                    guitarNoteList.add(new Note(41, 1.0f, offset));
                    guitarNoteList.add(new Note(34, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(37, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(41, 1.0f, offset + 1.0f));
                    offset += 2;

                    guitarNoteList.add(new Note(36, 1.0f, offset));
                    guitarNoteList.add(new Note(40, 1.0f, offset));
                    guitarNoteList.add(new Note(43, 1.0f, offset));
                    guitarNoteList.add(new Note(36, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(40, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(43, 1.0f, offset + 1.0f));
                    offset += 2;

                    guitarNoteList.add(new Note(36, 1.0f, offset));
                    guitarNoteList.add(new Note(40, 1.0f, offset));
                    guitarNoteList.add(new Note(43, 1.0f, offset));
                    guitarNoteList.add(new Note(36, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(40, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(43, 1.0f, offset + 1.0f));
                    offset += 2;
                }

                part.setGuitarNoteList(guitarNoteList);
                return;
            }
            else if(partStyle ==2){// prechorus

                for(int k = 0 ; k < 2 ; k++) {
                    guitarNoteList.add(new Note(41, 1.0f, offset));
                    guitarNoteList.add(new Note(44, 1.0f, offset));
                    guitarNoteList.add(new Note(48, 1.0f, offset));
                    guitarNoteList.add(new Note(41, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(44, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(48, 1.0f, offset + 1.0f));
                    offset += 2;
                    guitarNoteList.add(new Note(37, 1.0f, offset));
                    guitarNoteList.add(new Note(41, 1.0f, offset));
                    guitarNoteList.add(new Note(44, 1.0f, offset));
                    guitarNoteList.add(new Note(37, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(41, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(44, 1.0f, offset + 1.0f));
                    offset += 2;

                    guitarNoteList.add(new Note(39, 1.0f, offset));
                    guitarNoteList.add(new Note(43, 1.0f, offset));
                    guitarNoteList.add(new Note(46, 1.0f, offset));
                    guitarNoteList.add(new Note(39, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(43, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(46, 1.0f, offset + 1.0f));
                    offset += 2;
                    guitarNoteList.add(new Note(32, 1.0f, offset));
                    guitarNoteList.add(new Note(36, 1.0f, offset));
                    guitarNoteList.add(new Note(39, 1.0f, offset));
                    guitarNoteList.add(new Note(32, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(36, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(39, 1.0f, offset + 1.0f));
                    offset += 2;

                    guitarNoteList.add(new Note(37, 1.0f, offset));
                    guitarNoteList.add(new Note(41, 1.0f, offset));
                    guitarNoteList.add(new Note(44, 1.0f, offset));
                    guitarNoteList.add(new Note(37, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(41, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(44, 1.0f, offset + 1.0f));
                    offset += 2;
                    guitarNoteList.add(new Note(34, 1.0f, offset));
                    guitarNoteList.add(new Note(37, 1.0f, offset));
                    guitarNoteList.add(new Note(41, 1.0f, offset));
                    guitarNoteList.add(new Note(34, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(37, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(41, 1.0f, offset + 1.0f));
                    offset += 2;

                    guitarNoteList.add(new Note(36, 1.0f, offset));
                    guitarNoteList.add(new Note(40, 1.0f, offset));
                    guitarNoteList.add(new Note(43, 1.0f, offset));
                    guitarNoteList.add(new Note(36, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(40, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(43, 1.0f, offset + 1.0f));
                    offset += 2;

                    guitarNoteList.add(new Note(36, 1.0f, offset));
                    guitarNoteList.add(new Note(40, 1.0f, offset));
                    guitarNoteList.add(new Note(43, 1.0f, offset));
                    guitarNoteList.add(new Note(36, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(40, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(43, 1.0f, offset + 1.0f));
                    offset += 2;
                }
                part.setGuitarNoteList(guitarNoteList);
                return;
            }
            else if (partStyle==3){// chorus
                offset+=4.0f;

                for(int k = 0 ; k < 2 ; k++) {
                    guitarNoteList.add(new Note(41, 1.0f, offset));
                    guitarNoteList.add(new Note(44, 1.0f, offset));
                    guitarNoteList.add(new Note(48, 1.0f, offset));
                    guitarNoteList.add(new Note(41, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(44, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(48, 1.0f, offset + 1.0f));
                    offset += 2;
                    guitarNoteList.add(new Note(34, 1.0f, offset));
                    guitarNoteList.add(new Note(37, 1.0f, offset));
                    guitarNoteList.add(new Note(41, 1.0f, offset));
                    guitarNoteList.add(new Note(34, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(37, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(41, 1.0f, offset + 1.0f));
                    offset += 2;

                    guitarNoteList.add(new Note(39, 1.0f, offset));
                    guitarNoteList.add(new Note(43, 1.0f, offset));
                    guitarNoteList.add(new Note(46, 1.0f, offset));
                    guitarNoteList.add(new Note(39, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(43, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(46, 1.0f, offset + 1.0f));
                    offset += 2;
                    guitarNoteList.add(new Note(32, 1.0f, offset));
                    guitarNoteList.add(new Note(36, 1.0f, offset));
                    guitarNoteList.add(new Note(39, 1.0f, offset));
                    guitarNoteList.add(new Note(32, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(36, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(39, 1.0f, offset + 1.0f));
                    offset += 2;

                    guitarNoteList.add(new Note(37, 1.0f, offset));
                    guitarNoteList.add(new Note(41, 1.0f, offset));
                    guitarNoteList.add(new Note(44, 1.0f, offset));
                    guitarNoteList.add(new Note(37, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(41, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(44, 1.0f, offset + 1.0f));
                    offset += 2;
                    guitarNoteList.add(new Note(34, 1.0f, offset));
                    guitarNoteList.add(new Note(37, 1.0f, offset));
                    guitarNoteList.add(new Note(41, 1.0f, offset));
                    guitarNoteList.add(new Note(34, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(37, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(41, 1.0f, offset + 1.0f));
                    offset += 2;

                    guitarNoteList.add(new Note(36, 1.0f, offset));
                    guitarNoteList.add(new Note(40, 1.0f, offset));
                    guitarNoteList.add(new Note(43, 1.0f, offset));
                    guitarNoteList.add(new Note(36, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(40, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(43, 1.0f, offset + 1.0f));
                    offset += 2;

                    guitarNoteList.add(new Note(36, 1.0f, offset));
                    guitarNoteList.add(new Note(40, 1.0f, offset));
                    guitarNoteList.add(new Note(43, 1.0f, offset));
                    guitarNoteList.add(new Note(36, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(40, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(43, 1.0f, offset + 1.0f));
                    offset += 2;
                }
                part.setGuitarNoteList(guitarNoteList);
                return;            }
            else if(partStyle==4){// solo
                for(int k = 0 ; k < 2 ; k++) {
                    guitarNoteList.add(new Note(41, 1.0f, offset));
                    guitarNoteList.add(new Note(44, 1.0f, offset));
                    guitarNoteList.add(new Note(48, 1.0f, offset));
                    guitarNoteList.add(new Note(41, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(44, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(48, 1.0f, offset + 1.0f));
                    offset += 2;
                    guitarNoteList.add(new Note(34, 1.0f, offset));
                    guitarNoteList.add(new Note(37, 1.0f, offset));
                    guitarNoteList.add(new Note(41, 1.0f, offset));
                    guitarNoteList.add(new Note(34, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(37, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(41, 1.0f, offset + 1.0f));
                    offset += 2;

                    guitarNoteList.add(new Note(39, 1.0f, offset));
                    guitarNoteList.add(new Note(43, 1.0f, offset));
                    guitarNoteList.add(new Note(46, 1.0f, offset));
                    guitarNoteList.add(new Note(39, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(43, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(46, 1.0f, offset + 1.0f));
                    offset += 2;
                    guitarNoteList.add(new Note(32, 1.0f, offset));
                    guitarNoteList.add(new Note(36, 1.0f, offset));
                    guitarNoteList.add(new Note(39, 1.0f, offset));
                    guitarNoteList.add(new Note(32, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(36, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(39, 1.0f, offset + 1.0f));
                    offset += 2;

                    guitarNoteList.add(new Note(37, 1.0f, offset));
                    guitarNoteList.add(new Note(41, 1.0f, offset));
                    guitarNoteList.add(new Note(44, 1.0f, offset));
                    guitarNoteList.add(new Note(37, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(41, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(44, 1.0f, offset + 1.0f));
                    offset += 2;
                    guitarNoteList.add(new Note(34, 1.0f, offset));
                    guitarNoteList.add(new Note(37, 1.0f, offset));
                    guitarNoteList.add(new Note(41, 1.0f, offset));
                    guitarNoteList.add(new Note(34, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(37, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(41, 1.0f, offset + 1.0f));
                    offset += 2;

                    guitarNoteList.add(new Note(36, 1.0f, offset));
                    guitarNoteList.add(new Note(40, 1.0f, offset));
                    guitarNoteList.add(new Note(43, 1.0f, offset));
                    guitarNoteList.add(new Note(36, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(40, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(43, 1.0f, offset + 1.0f));
                    offset += 2;

                    guitarNoteList.add(new Note(36, 1.0f, offset));
                    guitarNoteList.add(new Note(40, 1.0f, offset));
                    guitarNoteList.add(new Note(43, 1.0f, offset));
                    guitarNoteList.add(new Note(36, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(40, 1.0f, offset + 1.0f));
                    guitarNoteList.add(new Note(43, 1.0f, offset + 1.0f));
                    offset += 2;
                }

                part.setGuitarNoteList(guitarNoteList);
                return;
            }
        }
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
