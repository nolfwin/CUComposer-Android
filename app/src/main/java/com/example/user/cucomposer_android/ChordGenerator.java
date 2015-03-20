package com.example.user.cucomposer_android;

import com.example.user.cucomposer_android.data.Database;
import com.example.user.cucomposer_android.entity.Note;
import com.example.user.cucomposer_android.utility.Calculator;
import com.example.user.cucomposer_android.utility.Key;

import java.util.List;

/**
 * Created by Nuttapong on 2/9/2015.
 */
public class ChordGenerator {
    private List<Note> notes;
    private int keyPitch;
    private boolean keyMode;


    private String LOG_TAG = "chordGen";

    private int numState = 35;
    private int[] keySequence = null;
    private double[][] noteInChord = null;
    private double[][] nextChord = null;
    private int[] projectedPitch = null;
    //    private int[] projectedPitch = {
//    		6, 6, 6, 6, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 1, 1, 7, 7, 7, 7, 7, 7, 1, 1, 1, 1, 7, 7, 7, 7, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 1, 1, 1, 1, 0, 0, 0, 0, 7, 7, 7, 7, 7, 7, 7, 7, 2, 2, 2, 2, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 1, 1, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 7, 7, 7, 7, 7, 7, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 7, 7, 7, 7, 7, 7, 5, 5, 5, 5, 6, 6, 6, 6, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 6, 6, 6, 6, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 6, 6, 6, 6, 5, 5, 5, 5, 5, 5, 5, 5, 3, 3, 3, 3, 1, 1, 1, 1, 7, 7, 7, 7, 1, 1, 1, 1, 1, 1, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 5, 5, 5, 5, 0, 0, 1, 1, 2, 2, 2, 2, 3, 3, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 7, 7, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 6, 6, 1, 1, 3, 3, 3, 3, 2, 2, 2, 2, 1, 1, 1, 1, 6, 6, 1, 1, 1, 1, 2, 2, 1, 1, 1, 1, 0, 0, 0, 0, 6, 6, 7, 7, 3, 3, 3, 3, 2, 2, 1, 1, 1, 1, 1, 1, 6, 6, 6, 6, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1
//    };
    private double[][] entropyObservation = null;
    private double[] chordAppearance = null;
    private int considerTime = 8;
    private int baseNote = 0;

    public void generateChords(){
        if(projectedPitch == null){
            initProjectedPitch();
        }
        //System.out.println("note size: "+notes.size());
//        System.out.println("pitch size: "+projectedPitch.length);
//        for(int i=0;i<projectedPitch.length;i++){
//
//            System.out.println("pitch "+i+": "+projectedPitch[i]);
//        }
        if(keySequence == null){
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
                            //if(j==k)
                            //	continue;
                            if((j==baseNote)&&(t!=0))
                                continue;
                            if((t==0)&&(j!=baseNote))
                                continue;
                            if((t!=tb+1)&&(t!=0))
                                continue;
                            if((t==0)&&(j==baseNote)&&(tb<3)){
                                continue;
                            }
                            double score = entropyObservation[i][j]+nextChord[k][j]+chordAppearance[j] + hmmNow[k+tb*numState];
//		                	{
//		                		System.out.println(""+score+" "+entropyObservation[i][j]+" "+nextChord[k][j]+" "+chordAppearance[j]+" "+hmmNow[k+tb*numState]);
//		                	}
                            if(score>=hmmNow[j] && hmmNow[j]>-1000){
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
            for(int j=0;j<numState*considerTime;j++){
                hmmNow[j] = hmmNext[j];
                System.out.print(" "+hmmNow[j]);
            }
            System.out.println();
        }

        int bestCandidate = 0;
        double bestScore = -100000;
        for(int j=0;j<numState*considerTime;j++){
            if(bestScore < hmmNow[j]){
                bestCandidate = j;
                bestScore = hmmNow[j];
            }
        }
        System.out.println(entropyObservation.length);
        int[] chordPath = new int[entropyObservation.length];
        for(int i=entropyObservation.length-1;i>0;i--){
            chordPath[i] = bestCandidate%numState;
            bestCandidate = path[i][bestCandidate];
        }
        chordPath[0] = bestCandidate;
        for(int i=0;i<chordPath.length;i++){

            System.out.print(" "+ (chordPath[i]%numState>=7 )+(chordPath[i]%numState%7 + 1));
        }
    }


    public void setNotes(List<Note> notes){
        this.notes = notes;
    }

    public void setKey(int keyPitch,boolean keyMode){
        this.keyPitch = keyPitch;
        this.keyMode = keyMode;
        if(keyMode == Key.MAJOR){
            baseNote = 0;
        }
        else{
            baseNote = 7;
        }
    }

    public void initProjectedPitch(){
        Note lastNote = notes.get(notes.size()-1);
        System.out.println("last note: "+lastNote.getOffset()+" "+lastNote.getDuration());
        projectedPitch = new int[((int)(Math.ceil((lastNote.getOffset()+lastNote.getDuration())/4)))*16];
        for(int i=0;i<notes.size();i++){
            Note aNote = notes.get(i);
            if(aNote.getPitch()<0){
                continue;
            }
            for(int j=(int)(aNote.getOffset()*4);j<(int)((aNote.getOffset()+aNote.getDuration())*4);j++){
                projectedPitch[j] = Key.mapToKey(aNote.getPitch(),keyPitch,keyMode)+1;
            }
        }
    }

    public void initNoteInChord(){
        double[][] db;
        if(keyMode == Key.MAJOR){
            db = Database.noteInChordMajor;
        }
        else{
            db = Database.noteInChordMinor;
        }
        noteInChord = new double[35][7];
        for(int i=0;i<noteInChord.length;i++){
            for(int j=0;j<noteInChord[i].length;j++){
                noteInChord[i][j] = db[i][keySequence[j]];
                //System.out.println("note in chord "+i+" "+j+": "+noteInChord[i][j]);
            }
            Calculator.normalize(noteInChord[i]);
            Calculator.calLogArray(noteInChord[i]);
            //System.out.println(""+i+": "+Arrays.toString(noteInChord[i]));
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
        entropyObservation = new double[projectedPitch.length/16][numState];
        for(int i=0;i<entropyObservation.length;i++) {
            double[] noteCounter = new double[7];
            boolean hasPitch = false;
            for(int j=i*16;j<i*16+16;j++){
                if(projectedPitch[j]!=0) {
                    noteCounter[projectedPitch[j]-1] += 1;
                    hasPitch = true;
                }
            }



            if(hasPitch){
                Calculator.normalize(noteCounter);
                for(int j=0;j<numState;j++) {
                    entropyObservation[i][j] = Calculator.calDotArray(noteCounter,noteInChord[j]);
                }
            }
            else{
                for(int j=0;j<numState;j++) {
                    entropyObservation[i][j] = 0;
                }
            }
            Calculator.normalize(entropyObservation[i]);
        }
    }

    public static void main(String[] args){
        ChordGenerator cg = new ChordGenerator();
        cg.setKey(0, Key.MAJOR);
        cg.generateChords();
    }

}
