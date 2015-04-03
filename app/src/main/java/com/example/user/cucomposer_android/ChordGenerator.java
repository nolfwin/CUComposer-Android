package com.example.user.cucomposer_android;

import com.example.user.cucomposer_android.data.Database;
import com.example.user.cucomposer_android.entity.Note;
import com.example.user.cucomposer_android.utility.Calculator;
import com.example.user.cucomposer_android.utility.Key;

import java.util.List;

public class ChordGenerator {
    private List<Note> notes;
    private int keyPitch;
    private boolean keyMode;


    private String LOG_TAG = "chordGen";

    private int numState = 35;
    private int[] keySequence = null;
    private double[][] noteInChord = null;
    private double[][] nextChord = null;
    private int[] projectedNotes = null;

    private int fixedLastChord = -1;



//    private int[] projectedNotes = {
//            6, 6, 6, 6, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 1, 1, 7, 7, 7, 7, 7, 7, 1, 1, 1, 1, 7, 7, 7, 7, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 1, 1, 1, 1, 0, 0, 0, 0, 7, 7, 7, 7, 7, 7, 7, 7, 2, 2, 2, 2, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 1, 1, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 7, 7, 7, 7, 7, 7, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 7, 7, 7, 7, 7, 7, 5, 5, 5, 5, 6, 6, 6, 6, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 6, 6, 6, 6, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 6, 6, 6, 6, 5, 5, 5, 5, 5, 5, 5, 5, 3, 3, 3, 3, 1, 1, 1, 1, 7, 7, 7, 7, 1, 1, 1, 1, 1, 1, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 5, 5, 5, 5, 0, 0, 1, 1, 2, 2, 2, 2, 3, 3, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 7, 7, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 6, 6, 1, 1, 3, 3, 3, 3, 2, 2, 2, 2, 1, 1, 1, 1, 6, 6, 1, 1, 1, 1, 2, 2, 1, 1, 1, 1, 0, 0, 0, 0, 6, 6, 7, 7, 3, 3, 3, 3, 2, 2, 1, 1, 1, 1, 1, 1, 6, 6, 6, 6, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1
//    };

    private double[][] entropyObservation = null;
    private double[] chordAppearance = null;
    private int considerTime = 8;
    private int baseNote = 0;

    private double notesContribute = 0.3;
    private double popularChordContribute = 0.3;
    private double variation = 0.5;
    private double expertParameter = 0.75;

    public int[] generateChords(){
        if(projectedNotes == null){
            entropyObservation = null;
            initProjectedNotes();
        }
        if(keySequence == null){
            noteInChord = null;
            nextChord = null;
            chordAppearance = null;
            entropyObservation = null;
            initKeySequence();
        }
        if(noteInChord==null){
            initNoteInChord();
        }
        if(nextChord == null){
            initNextChord();
        }
        if(chordAppearance == null){
            initChordAppearance();
        }
        if(entropyObservation==null){
            calculateEntropyObservation();
        }


        double[] hmmNow = new double[numState*considerTime];
        double[] hmmNext = new double[numState*considerTime];
        int[][] path = new int[entropyObservation.length][numState*considerTime];
        for(int i=0;i<numState*considerTime;i++){
            path[0][i] = -1;
            hmmNow[i] = -1000000;
        }
        hmmNow[baseNote] = 0;



        for(int i=1;i<entropyObservation.length;i++){
            for(int t=0;t<considerTime;t++){
                for(int j=0;j<numState;j++){
                    double bestScore = -100000;
                    //hmmNext[j+t*numState] = -1000000;

                    int bestCandidate = 0;
                    for(int tb = 0;tb<considerTime;tb++){
                        for(int k=0;k<numState;k++){
                            if((j==baseNote)&&(t!=0))
                                continue;
                            if((t==0)&&(j!=baseNote))
                                continue;
                            if((t!=tb+1)&&(t!=0))
                                continue;
                            if((t==0)&&(j==baseNote)&&(tb<3)){
                                continue;
                            }
                            double score = notesContribute*entropyObservation[i][j]+(1.0-notesContribute)*nextChord[k][j]+popularChordContribute*chordAppearance[j] + hmmNow[k+tb*numState] + variation * Math.random();

                            if(score>=hmmNow[j] && hmmNow[j]>-10000){
                                System.out.println(""+hmmNow[j]+" "+score);
                            }
                            if(score>bestScore){
                                bestScore = score;
                                bestCandidate = k+tb*numState;
                            }
                        }
                    }
                    hmmNext[j+t*numState] = bestScore;
                    path[i][j+t*numState] = bestCandidate;
                }
            }
            System.arraycopy(hmmNext, 0, hmmNow, 0, numState * considerTime);
        }

        int bestCandidate = 0;
        double bestScore = -100000;
        for(int j=0;j<numState*considerTime;j++){
            if(fixedLastChord>=0 && j%7 != fixedLastChord)
                continue;
            if (bestScore < hmmNow[j]) {
                bestCandidate = j;
                bestScore = hmmNow[j];
            }
        }
        System.out.println(entropyObservation.length);
        int[] chordPath = new int[entropyObservation.length];
        for(int i=entropyObservation.length-1;i>0;i--){
            System.out.print(bestCandidate+" ");
            chordPath[i] = bestCandidate;
            bestCandidate = path[i][bestCandidate];
        }
        System.out.println();
        chordPath[0] = bestCandidate;
        for(int i=0;i<chordPath.length;i++){
            chordPath[i] = chordPath[i]%35;
            System.out.print(" "+ chordName(chordPath[i]));
        }
        System.out.println();
        return chordPath;
    }


    public void setNotes(List<Note> notes){
        projectedNotes = null;
        this.notes = notes;
    }

    public void setKey(int keyPitch,boolean keyMode){
        if(this.keyMode != keyMode){
            keySequence = null;
            projectedNotes = null;
            this.keyMode = keyMode;
            this.keyMode = keyMode;
        }
        else{
            if(this.keyPitch != keyPitch){
                projectedNotes = null;
            }
        }
        this.keyPitch = keyPitch;
        if(keyMode){
            baseNote = 0;
        }
        else{
            baseNote = 7;
        }
    }

    public void initProjectedNotes(){
        this.projectedNotes = Key.ProjectNotes(notes, keyPitch, keyMode);
    }

    public void initNoteInChord(){
        double[][] db;
        if(keyMode){
            db = Database.noteInChordMajor;
        }
        else{
            db = Database.noteInChordMinor;
        }
        noteInChord = new double[35][7];
        for(int i=0;i<noteInChord.length;i++){
            for(int j=0;j<noteInChord[i].length;j++){
                noteInChord[i][j] = db[i][keySequence[j]];
            }
            Calculator.normalize(noteInChord[i]);
            Calculator.calLogArray(noteInChord[i]);
        }
    }

    public void initKeySequence(){
        if(keyMode == Key.MAJOR){
            keySequence = Key.MAJOR_SEQUENCE;
        }
        else{
            keySequence = Key.MINOR_SEQUENCE;
        }
    }

    public void initNextChord(){
        if(keyMode == Key.MAJOR){
            nextChord = Database.nextChordMajor;
        }
        else{
            nextChord = Database.nextChordMinor;
        }
    }

    public void initChordAppearance(){
        if(keyMode == Key.MAJOR){
            chordAppearance = Database.chordAppearanceMajor;
        }
        else{
            chordAppearance = Database.chordAppearanceMinor;
        }
    }

    public void calculateEntropyObservation(){
        entropyObservation = new double[projectedNotes.length/16][numState];
        for(int i=0;i<entropyObservation.length;i++) {
            double[] noteCounter = new double[7];
            boolean hasPitch = false;
            for(int j=i*16;j<i*16+16;j++){
                if(projectedNotes[j]!=0) {
                    noteCounter[projectedNotes[j]-1] += 1;
                    hasPitch = true;
                }
            }



            if(hasPitch){
                for(int j=0;j<numState;j++) {
                    entropyObservation[i][j] = Calculator.calDotArray(noteCounter,noteInChord[j]) * ((j<14)?1.0:expertParameter);
                }
            }
            else{
                for(int j=0;j<numState;j++) {
                    entropyObservation[i][j] = 0;
                }
            }
        }
    }

    public static void main(String[] args){
        ChordGenerator cg = new ChordGenerator();
        cg.setKey(0, Key.MAJOR);
        cg.generateChords();
    }

    public static String chordName(int chordNum){
        String name = ""+(chordNum%7+1)+"_";
        switch (chordNum/7){
            case 0: name += "maj";
                break;
            case 1: name += "min";
                break;
            case 2: name += "maj7";
                break;
            case 3: name += "7";
                break;
            case 4: name += "min7";
                break;
        }
        return name;
    }

    public void setRandomness(double variation){
        this.variation = variation;
        System.out.println("randomness "+variation);
    }

    public void setOriginality(double variation){
        this.notesContribute = variation;
        System.out.println("originality "+notesContribute);
    }

    public void setComplexity(double variation){
        this.expertParameter = variation;
        System.out.println("complexity "+expertParameter);
    }

    public void setFixedLastChord(int chord){
        fixedLastChord = chord-1;
    }


}
