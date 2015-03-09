package com.example.user.cucomposer_android;

import android.util.Log;
import android.widget.Toast;

import com.example.user.cucomposer_android.entity.Part;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import be.tarsos.dsp.pitch.FastYin;
import be.tarsos.dsp.pitch.PitchDetectionResult;
/**
 * Created by Nontawat on 9/2/2558.
 */
public class Pitch {
    private static final String LOG_TAG = "debuggerFromPitch";

    static String pitchAnswer = "";
    static String pitchBefore = "";
    static int lastDuration = 0;
    static int lastsamp =0;
    static int volume = 80;
    static int lastNote = -1;
    static float lastFreq = -1;
    static String pitchPropString="";
    static ArrayList<Integer> playNote = new ArrayList<Integer>();
    static ArrayList<Integer> playDuration = new ArrayList<Integer>();
    static ArrayList<Integer> reallyPlayNote = new ArrayList<Integer>();
    static ArrayList<Integer> reallyPlayduration = new ArrayList<Integer>();
    static ArrayList<Float> frequencyArray = new ArrayList<Float>();
    static String[] note = { "C0", "C#0", "D0", "D#0", "E0", "F0", "F#0", "G0",
            "G#0", "A0", "A#0", "B0", "C1", "C#1", "D1", "D#1", "E1", "F1",
            "F#1", "G1", "G#1", "A1", "A#1", "B1", "C2", "C#2", "D2", "D#2",
            "E2", "F2", "F#2", "G2", "G#2", "A2", "A#2", "B2", "C3", "C#3",
            "D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3",
            "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4",
            "A#4", "B4", "C5", "C#5", "D5", "D#5", "E5", "F5", "F#5", "G5",
            "G#5", "A5", "A#5", "B5", "C6", "C#6", "D6", "D#6", "E6", "F6",
            "F#6", "G6", "G#6", "A6", "A#6", "B6", "C7", "C#7", "D7", "D#7",
            "E7", "F7", "F#7", "G7", "G#7", "A7", "A#7", "B7", "C8", "C#8",
            "D8", "D#8", "E8", "F8", "F#8", "G8", "G#8", "A8", "A#8", "B8" };
    static double[] freq = { 16.35, 17.32, 18.35, 19.45, 20.6, 21.83, 23.12,
            24.5, 25.96, 27.5, 29.14, 30.87, 32.7, 34.65, 36.71, 38.89, 41.2,
            43.65, 46.25, 49, 51.91, 55, 58.27, 61.74, 65.41, 69.3, 73.42,
            77.78, 82.41, 87.31, 92.5, 98, 103.83, 110, 116.54, 123.47, 130.81,
            138.59, 146.83, 155.56, 164.81, 174.61, 185, 196, 207.65, 220,
            233.08, 246.94, 261.63, 277.18, 293.66, 311.13, 329.63, 349.23,
            369.99, 392, 415.3, 440, 466.16, 493.88, 523.25, 554.37, 587.33,
            622.25, 659.25, 698.46, 739.99, 783.99, 830.61, 880, 932.33,
            987.77, 1046.5, 1108.73, 1174.66, 1244.51, 1318.51, 1396.91,
            1479.98, 1567.98, 1661.22, 1760, 1864.66, 1975.53, 2093, 2217.46,
            2349.32, 2489.02, 2637.02, 2793.83, 2959.96, 3135.96, 3322.44,
            3520, 3729.31, 3951.07, 4186.01, 4434.92, 4698.63, 4978.03,
            5274.04, 5587.65, 5919.91, 6271.93, 6644.88, 7040, 7458.62, 7902.13 };
    static int bufferSize = 1024;
    static int sampFreq = 8000;

    static int[][] key = new int[12][7];
    static double[] majorWeight ={6.35,2.23,3.48,2.33,4.38	,4.09,2.52,5.19,2.39,3.66,2.29,2.88};
    static double [] minorWeight = {6.33,2.68,3.52,5.38,2.60,3.53,2.54,4.75,3.98,2.69,3.34,3.17};
    static int[][] majorKey = new int[12][12];
    static int[][] minorKey = new int[12][12];
    boolean useKrumhansl = true;

    //pitch estimation method
    public static void pitchEst(float[] audioFloats,List<Integer> segmentOutput)
            throws Exception {
        initKey();
        playNote.clear();
        playDuration.clear();
//        playNote.add(-1);
//        frequencyArray.add((float) -1.0);
        DecimalFormat df = new DecimalFormat("#.00");

        pitchEst(Arrays.copyOfRange(audioFloats,0, segmentOutput.get(0)));
        String time = " " + df.format(((double) (segmentOutput.get(0))) / sampFreq);
        int duration = (int) (Double.parseDouble(time) * 1000);
        playDuration.add(duration);
        playNote.add(-2);
        playDuration.add(0);

        for(int i = 0 ; i < segmentOutput.size();i+=2){
           pitchEst(Arrays.copyOfRange(audioFloats,segmentOutput.get(i), segmentOutput.get(i+1)));
            if(i+2<segmentOutput.size()) {

                time = " " + df.format(((double) (segmentOutput.get(i+1)-segmentOutput.get(i))) / sampFreq);
                 duration = (int) (Double.parseDouble(time) * 1000)
                        - lastDuration;
                playDuration.add(duration);

                playNote.add(-2);
                playDuration.add(0);
                pitchEst(Arrays.copyOfRange(audioFloats,segmentOutput.get(i+1)+1, segmentOutput.get(i+2)-1));
                time = " " + df.format(((double) (segmentOutput.get(i+2)-segmentOutput.get(i+1))) / sampFreq);
                 duration = (int) (Double.parseDouble(time) * 1000)
                        - lastDuration;
                playDuration.add(duration);
//                time = " " + df.format(((double) (segmentOutput.get(i+2)-segmentOutput.get(i+1))) / sampFreq);
//                duration = (int) (Double.parseDouble(time) * 1000);
                playNote.add(-2);
                playDuration.add(0);
//                frequencyArray.add((float)-2.0);

            }
        }
         time = " " + df.format(((double) (audioFloats.length))/sampFreq);
        String lastTime = " " + df.format(segmentOutput.get(segmentOutput.size()-1)/((double)sampFreq));
         duration = (int) (Double.parseDouble(time) * 1000) -(int) Double.parseDouble(lastTime) * 1000;
        playDuration.add(duration);
        Log.d(LOG_TAG,"TUNE WITH SEGMENT");

        tuneMelody();
        Log.d(LOG_TAG,Arrays.toString(playNote.toArray())+"\n"+Arrays.toString(playDuration.toArray()));
        Log.d(LOG_TAG,Arrays.toString(reallyPlayNote.toArray())+"\n"+Arrays.toString(reallyPlayduration.toArray()));



        Log.d(LOG_TAG,playNote.size()+" "+playDuration.size());
        int bpm = Tempo.tempoDetect(playDuration);
        Log.d(LOG_TAG,"BPM = "+bpm);
        Part part = Tempo.toPart(reallyPlayNote,reallyPlayduration,bpm,0);
        Log.d(LOG_TAG,"noteList is"+part.getNoteList().size());

        MidiPlay mid = new MidiPlay(part.getNoteList());
        mid.setBpm(part.getBpm());
        mid.generateMidi();
    }
    public static void pitchEst(float[] audioFloats)
            throws Exception {

//        playNote.clear();
//        playDuration.clear();
        playNote.add(-1);
        frequencyArray.add((float) -1.0);
        pitchAnalysis(audioFloats,sampFreq,bufferSize,0);

//        tuneMelody();
    }
    public static void pitchEstWithoutSegment(float[] audioFloats){
        initKey();
        playNote.clear();
        playDuration.clear();
        playNote.add(-1);
        frequencyArray.add((float) -1.0);
        pitchAnalysis(audioFloats,sampFreq,bufferSize,0);
        DecimalFormat df = new DecimalFormat("#.00");
        String time = " " + df.format(((double) (audioFloats.length))/sampFreq);
        int duration = (int) (Double.parseDouble(time) * 1000)
                - lastDuration;
        playDuration.add(duration);
        Log.d(LOG_TAG,"TUNE WITHOUT SEGMENT");
        tuneMelody();
        Log.d(LOG_TAG,Arrays.toString(playNote.toArray())+"\n"+Arrays.toString(playDuration.toArray()));
        Log.d(LOG_TAG,Arrays.toString(reallyPlayNote.toArray())+"\n"+Arrays.toString(reallyPlayduration.toArray()));


    }
    private static void tuneMelody(){


         reallyPlayNote = new ArrayList<Integer>();
         reallyPlayduration = new ArrayList<Integer>();
        int currentSize = 0;
        if(playDuration.get(0)<0)playDuration.set(0,0);
//        for (int i = 0; i < playNote.size(); i++) {
//            Log.d(LOG_TAG,"note is " + playNote.get(i)
//                    + " and the duration is " + playDuration.get(i)
//                    + " and the frequency is " + frequencyArray.get(i));
//        }
        Log.d(LOG_TAG,"note is "+ Arrays.toString(playNote.toArray())+"\n"+Arrays.toString(playDuration.toArray()));
        for (int i = 0; i < playNote.size() ; i++) {
//            if (i > 0)
//                Log.d(LOG_TAG,"i = " + i + " " + playNote.get(i) + " vs "
//                        + reallyPlayNote.get(currentSize));

            if (i == 0) {
                reallyPlayNote.add(playNote.get(0));
                reallyPlayduration.add(playDuration.get(0));
//                Log.d(LOG_TAG,"add " + playNote.get(0) + " to index "
//                        + currentSize);
            } else if ((Math.abs(playNote.get(i)
                    - reallyPlayNote.get(currentSize)) < 2)) {
                int j = i;
                int currentDuration = reallyPlayduration.get(currentSize);
                int otherDuration = 0;
                for (; j < playNote.size(); j++) {
                 //   if(playNote.get(j)==-2)break;
                    if (playNote.get(j) == reallyPlayNote.get(currentSize)) {
//                        Log.d(LOG_TAG,"Current duration = "
//                                + currentDuration + " Other duration = "
//                                + otherDuration);
                        currentDuration += playDuration.get(j);
                        if (currentDuration > otherDuration)
                            reallyPlayduration.set(currentSize,
                                    reallyPlayduration.get(currentSize)
                                            + playDuration.get(i));
                        else {
                            if (i > 0)
//                                Log.d(LOG_TAG,"i = " + i + " "
//                                        + playNote.get(i) + " differ "
//                                        + playNote.get(i - 1));
                            reallyPlayNote.add(playNote.get(i));
                            reallyPlayduration.add(playDuration.get(i));
                            currentSize++;
//                            Log.d(LOG_TAG,"add " + playNote.get(i) + " to index "
//                                    + currentSize);
                        }
                        break;
                    } else
                        otherDuration += playDuration.get(j);
                    if (j == playNote.size() - 1) {
                        if (i > 0)
//                            Log.d(LOG_TAG,"i = " + i + " "
//                                    + playNote.get(i) + " differ "
//                                    + playNote.get(i - 1));

                        reallyPlayNote.add(playNote.get(i));
                        reallyPlayduration.add(playDuration.get(i));
                        currentSize++;
//                        Log.d(LOG_TAG,"add " + playNote.get(i) + " to index "
//                                + currentSize);
                    }
                }

                // playNote.set(i+1,playNote.get(i));
                // reallyPlayduration.set(currentSize,reallyPlayduration.get(currentSize)+playDuration.get(i));
            } else {

                reallyPlayNote.add(playNote.get(i));
                reallyPlayduration.add(playDuration.get(i));

                currentSize++;
//                Log.d(LOG_TAG,"add " + playNote.get(i) + " to index "
//                        + currentSize);
            }
        }
        Log.d(LOG_TAG,"---------------end---------------");
//        for (int i = 0; i < reallyPlayNote.size(); i++) {
//            // JOptionPane.showMessageDialog(null, "fu");
//            if(reallyPlayNote.get(i)>-1)
//                Log.d(LOG_TAG,"true note is " + reallyPlayNote.get(i)+ " ("+note[reallyPlayNote.get(i)]+")"
//                        + " and the true duration is " + reallyPlayduration.get(i));
//            else             Log.d(LOG_TAG,"SILENCE - true duration is " + reallyPlayduration.get(i));
//        }
        Log.d(LOG_TAG,Arrays.toString(reallyPlayNote.toArray())+"\n"+Arrays.toString(reallyPlayduration.toArray()));
        Log.d(LOG_TAG,"---------------true end---------------");

        for (int i = 0; i < reallyPlayNote.size(); i++) {
            int dur = reallyPlayduration.get(i);
            if (dur < 150) {
                //first entry
                if (i == 0 && reallyPlayNote.size() > 1) {
                    if (reallyPlayduration.get(i + 1) > dur) {
                        reallyPlayduration.set(i + 1,
                                reallyPlayduration.get(i + 1) + dur);
                        reallyPlayduration.set(i, 0);
                    } else {
                        int j = i + 2;
                        while (j < reallyPlayNote.size()) {
                            if (reallyPlayduration.get(j) > dur) {
                                reallyPlayduration.set(j,
                                        reallyPlayduration.get(j) + dur);
                                reallyPlayduration.set(i, 0);
                                break;
                            }
                            j++;
                        }
                    }
                }
                //last entry
                else if (i == reallyPlayNote.size() - 1) {
                    if (i!= 0 && reallyPlayduration.get(i - 1) > dur) {
                        reallyPlayduration.set(i - 1,
                                reallyPlayduration.get(i - 1) + dur);
                        reallyPlayduration.set(i, 0);
                    } else {
                        int j = i - 2;
                        while (j >= 0) {
                            if (reallyPlayduration.get(j) > dur) {
                                reallyPlayduration.set(j,
                                        reallyPlayduration.get(j) + dur);
                                reallyPlayduration.set(i, 0);
                                break;
                            }
                            j--;
                        }
                    }
                }
                //in the middle of array
                else {
                    int c1 = reallyPlayNote.get(i-1);
                    int c2 = reallyPlayNote.get(i+1);
                    int note = reallyPlayNote.get(i);
                    if(Math.abs(note-c1)<Math.abs(note-c2)){
                        if(dur<reallyPlayduration.get(i+1) && dur>reallyPlayduration.get(i-1) ){
                            reallyPlayduration.set(i+1,dur+reallyPlayduration.get(i+1));
                            reallyPlayduration.set(i,0);
                        }
                        else{
                            reallyPlayduration.set(i-1,dur+reallyPlayduration.get(i-1));
                            reallyPlayduration.set(i,0);
                        }
                    }
                    else{
                        if(dur<reallyPlayduration.get(i-1) && dur>reallyPlayduration.get(i+1) ){
                            reallyPlayduration.set(i-1,dur+reallyPlayduration.get(i-1));
                            reallyPlayduration.set(i,0);
                        }
                        else{
                            reallyPlayduration.set(i+1,dur+reallyPlayduration.get(i+1));
                            reallyPlayduration.set(i,0);
                        }
                    }

                }
            }
        }
        for(int i = reallyPlayNote.size()-1;i>=0;i--){
            if(reallyPlayduration.get(i)==0){
                reallyPlayNote.remove(i);
                reallyPlayduration.remove(i);
            }
        }
//        for (int i = 0; i < reallyPlayNote.size(); i++) {
//            if(reallyPlayNote.get(i)>-1)
//                Log.d(LOG_TAG,"really true note is " + reallyPlayNote.get(i)+ " ("+note[reallyPlayNote.get(i)]+")"
//                        + " and the really true duration is " + reallyPlayduration.get(i));
//            else             Log.d(LOG_TAG,"SILENCE - true duration is " + reallyPlayduration.get(i));
//        }
        Log.d(LOG_TAG,Arrays.toString(reallyPlayNote.toArray())+"\n"+Arrays.toString(reallyPlayduration.toArray()));
        Log.d(LOG_TAG,"---------------really true end---------------");


        int musicKey =calculateKey(reallyPlayNote,reallyPlayduration);
        int musicKey2 = findKeyKrumhanslSchmuckler(reallyPlayNote, reallyPlayduration);
        //	tuneMelody(reallyPlayNote,reallyPlayduration,musicKey);
        tuneMelodyKrumhanslSchmuckler(reallyPlayNote,reallyPlayduration,musicKey2);
//        for (int i = 0; i < reallyPlayNote.size(); i++) {
//            // JOptionPane.showMessageDialog(null, "fu");
//            if(reallyPlayNote.get(i)>-1)
//                Log.d(LOG_TAG,"After tune note is " + reallyPlayNote.get(i)+ " ("+note[reallyPlayNote.get(i)]+")"
//                        + " and the After tune duration is " + reallyPlayduration.get(i));
//            else             Log.d(LOG_TAG,"SILENCE - true duration is " + reallyPlayduration.get(i));
//        }
        Log.d(LOG_TAG,Arrays.toString(reallyPlayNote.toArray())+"\n"+Arrays.toString(reallyPlayduration.toArray()));

        Log.d(LOG_TAG,"---------------After tune---------------");

        Log.d(LOG_TAG,"---------------done------------");
        pitchAnswer = "";
    }
    private static void pitchAnalysis(float[] audioFloats, int sampfreq, int bufferSize,
                                      int overlap) {
        boolean printAnalysisData =false;
        // TODO Auto-generated method stub
        FastYin fyin = new FastYin(sampfreq,bufferSize);
        int tracker = 0;
        int count = 0;
        lastDuration = 0;
        while(tracker+bufferSize-1< audioFloats.length){
            PitchDetectionResult result = fyin.getPitch(Arrays.copyOfRange(audioFloats, tracker, tracker+bufferSize));


            float pitchInHz = result.getPitch();
            float pitchProp = result.getProbability();
            if(printAnalysisData)
            Log.d(LOG_TAG,pitchInHz+" "+pitchProp );
            String time = "";
            count++;
            DecimalFormat df = new DecimalFormat("#.00");
            time = " "
                    + df.format(((double) (count * bufferSize + 1))
                    / sampFreq);
            lastsamp=  (count * bufferSize + 1);
            int noteAns;
            pitchPropString = pitchPropString +pitchProp+"\n";
            if (pitchProp > 0.95){
                noteAns = findNote(pitchInHz);

            }
            else{
                noteAns = lastNote;
            }
            if(printAnalysisData)
            Log.d(LOG_TAG,pitchInHz + " " + noteAns);
            String noteP;
            if (noteAns > 0)
                noteP = note[noteAns];
            else
                noteP = "detect nothing";

            if (pitchInHz == -1) {
                String ans = time + " detect nothing";
                if (!pitchBefore.equals("detect nothing")) {

                    int duration = (int) (Double.parseDouble(time) * 1000)
                            - lastDuration;
                    lastDuration = (int) (Double.parseDouble(time) * 1000);

                    if (noteAns > 0)
                        playNote.add(noteAns + 12);
                    else
                        playNote.add(-1);
                    frequencyArray.add(pitchInHz);
                    lastNote = -1;
                    lastFreq=pitchInHz;
                    playDuration.add(duration);
                    pitchAnswer = pitchAnswer + "\n" + ans;
                    pitchBefore = "detect nothing";
                }
            } else {
                String ans = time + " " + pitchInHz + " " + noteP/*
																			 * +
																			 * " "
																			 * +
																			 * percent
																			 * +
																			 * "%"
																			 */;
                if(printAnalysisData)
                Log.d(LOG_TAG,ans);
                if (!pitchBefore.equals(noteP)) {
                    pitchAnswer = pitchAnswer + "\n" + ans;
                    pitchBefore = noteP;

                    int duration = (int) (Double.parseDouble(time) * 1000)
                            - lastDuration;
                    lastDuration = (int) (Double.parseDouble(time) * 1000);

                    lastNote = noteAns;
                    lastFreq=pitchInHz;
                    frequencyArray.add(pitchInHz);
                    if (noteAns > 0)
                        playNote.add(noteAns + 12);
                    else
                        playNote.add(-1);
                    playDuration.add(duration);
                }
            }
            tracker+=bufferSize;
        }
    }
    //tuning melody
    public static void tuneMelodyKrumhanslSchmuckler(ArrayList<Integer> noteList, ArrayList<Integer> durationList,int musicKey){
        Log.d(LOG_TAG,"Music Key is "+musicKey);
        //   musicKey = 0;
        int[] majorStrict = {0,2,4,5,7,9,11};
        int[] minorStrict = {0,2,3,5,7,8,10};
        for(int i = 0;  i< noteList.size();i++){
            int multiplier = noteList.get(i)/12;
            int note = noteList.get(i)%12;
            int nearestLeft =-1;
            int nearestRight = -1;
            int key;
            int index=0;
            boolean outOfTune=false;
            if(musicKey <12){ // major
                key = musicKey;

                for(int j = 0 ; j< 12 ;j++){
                    if(majorKey[musicKey][j]==note){
                        index = j;
                        break;
                    }
                }
                if(!((index<5&&index%2==0)||(index>=5&&index%2==1)) ){
                    Log.d(LOG_TAG,index+" out true from "+(12*multiplier+note));
                    outOfTune=true;
                }

            }else{
                key = musicKey-12;
                for(int j = 0 ; j< 12 ;j++){
                    if(minorKey[key][j]==note){
                        index = j;
                        break;
                    }
                }

                if(!(((index<3&&index%2==0)||(index>=3&&index<8&&index%2==1))||(index>=8&&index%2==0)) ){
                    Log.d(LOG_TAG,index+" out true from "+(12*multiplier+note));
                    outOfTune=true;
                }
            }
            if(outOfTune){

                nearestLeft = index-1;
                nearestRight = index+1;

                double weightLeft = (musicKey>11)? minorWeight[scaleMod(nearestLeft,12)]:majorWeight[scaleMod(nearestLeft,12)];
                double weightRight = (musicKey>11)? minorWeight[scaleMod(nearestRight,12)]:majorWeight[scaleMod(nearestRight,12)];

                if(weightRight>=weightLeft){
                    noteList.set(i,12*multiplier+note+1);
                }
                else{
                    noteList.set(i,12*multiplier+note-1);
                }

            }
        }
    }
    public static void tuneMelody(ArrayList<Integer> noteList, ArrayList<Integer> durationList,int musicKey){
        Log.d(LOG_TAG,Arrays.deepToString(key));
        Log.d(LOG_TAG,"Music Key is "+musicKey);
        //   musicKey = 0;
        for(int i = 0;  i< noteList.size();i++){
            int multiplier = noteList.get(i)/12;
            int note = noteList.get(i)%12;
            int nearestLeft =-1;
            int nearestRight = 12;



            for(int j = 0 ; j < key[0].length;j++){
                if(key[musicKey][j]<note){
                    if(nearestLeft<key[musicKey][j])nearestLeft=key[musicKey][j];
                }
                else if(key[musicKey][j]>note){
                    if(nearestRight>key[musicKey][j])nearestRight=key[musicKey][j];
                }
                else{
                    nearestLeft=-100;
                    break;
                }
            }
            if(nearestLeft!= -100){
                if(Math.abs(note-nearestLeft)>Math.abs(note-nearestRight)){
                    noteList.set(i,12*multiplier+nearestRight);
                }
                else if(Math.abs(note-nearestLeft)<Math.abs(note-nearestRight)){
                    noteList.set(i,12*multiplier+nearestLeft);
                }
                else{
                    double a = Math.random();
                    if(a>0.5) noteList.set(i,12*multiplier+nearestRight);
                    else noteList.set(i,12*multiplier+nearestLeft);
                }

            }
        }
    }
    public static int scaleMod(int a,int b){
        return a < 0 ? b + a : a % b;
    }

    //key finding
    public static void initKey(){
        key[0][0]=0;key[0][1]=2;key[0][2]=4;key[0][3]=5;key[0][4]=7;key[0][5]=9 ; key[0][6]=11;
        for(int i = 1 ; i < key.length;i++){
            for(int j = 0 ; j < key[0].length;j++){
                key[i][j] = (key[i-1][j]+1)%12;
            }
        }
        for(int i = 1 ; i<key.length;i++){
            Arrays.sort(key[i]);
        }

        majorKey[0][0]=0;majorKey[0][1]=1;majorKey[0][2]=2;majorKey[0][3]=3;majorKey[0][4]=4;majorKey[0][5]=5 ; majorKey[0][6]=6;
        majorKey[0][7]=7;majorKey[0][8]=8;majorKey[0][9]=9;majorKey[0][10]=10;majorKey[0][11]=11;
        for(int i = 1 ; i < majorKey.length;i++){
            for(int j = 0 ; j < majorKey[0].length;j++){
                majorKey[i][j] = (majorKey[i-1][j]+1)%12;
            }
        }
        minorKey[0][0]=0;minorKey[0][1]=1;minorKey[0][2]=2;minorKey[0][3]=3;minorKey[0][4]=4;minorKey[0][5]=5 ; minorKey[0][6]=6;
        minorKey[0][7]=7;minorKey[0][8]=8;minorKey[0][9]=9;minorKey[0][10]=10;minorKey[0][11]=11;
        for(int i = 1 ; i < minorKey.length;i++){
            for(int j = 0 ; j < minorKey[0].length;j++){
                minorKey[i][j] = (minorKey[i-1][j]+1)%12;
            }
        }
    }
    public static int findKeyKrumhanslSchmuckler(ArrayList<Integer> noteList,ArrayList<Integer> durationList){
        double [][] bucketMajor = new double[12][12];
        double [][] bucketMinor = new double[12][12];
        for(int i = 0; i< bucketMajor.length;i++){
            for(int j = 0 ; j<bucketMajor[0].length;j++){
                bucketMajor[i][j]=0;
                bucketMinor[i][j]=0;
            }
        }

        for(int i = 0 ; i < noteList.size();i++){
            int note = noteList.get(i);
            for(int j = 0 ; j <12;j++){
                for(int k=0; k<12;k++){
                    if(majorKey[j][k]==note%12) bucketMajor[j][k]+=durationList.get(i);
                    if(minorKey[j][k]==note%12) bucketMinor[j][k]+=durationList.get(i);
                }
            }
        }

        double[] correlationMajor = new double[12];
        double[] correlationMinor = new double[12];

        for(int i = 0 ; i < 12 ; i++){
            correlationMajor[i] = correlation(majorWeight,bucketMajor[i]);
            correlationMinor[i] = correlation(minorWeight,bucketMinor[i]);
        }
   /*     Log.d(LOG_TAG,Arrays.deepToString(bucketMajor));
        Log.d(LOG_TAG,Arrays.deepToString(bucketMinor));
        Log.d(LOG_TAG,Arrays.toString(correlationMajor));
        Log.d(LOG_TAG,Arrays.toString(correlationMinor));*/
        int maxIndexMajor = 0;
        int maxIndexMinor = 0;
        for(int i = 1 ; i < 12 ; i++){
            if(correlationMajor[i]>correlationMajor[maxIndexMajor])maxIndexMajor = i;
            if(correlationMinor[i]>correlationMinor[maxIndexMinor])maxIndexMinor = i;
        }


        if(correlationMajor[maxIndexMajor]>=correlationMinor[maxIndexMinor]) return maxIndexMajor;
        else return maxIndexMinor+12;
    }
    public static double correlation(double[] a,double[] b){

        double up = partialCor(a,b);
        double down1 = partialCor(a, a);
        double down2 = partialCor(b, b);
        double down = Math.sqrt(down1*down2);
        return up/down;
    }
    public static double partialCor(double[]a,double[] b){
        double meanA = mean(a);
        double meanB = mean(b);
        double ans  = 0;
        for(int i = 0 ; i < a.length ;i++){
            ans+= ((a[i]-meanA)*(b[i]-meanB));
        }
        return ans;
    }
    public static double mean(double[] a){
        int ans=0;
        for(int i = 0 ; i < a.length ;i++){
            ans+=a[i];
        }
        return ans/a.length;
    }
    public static int calculateKey(ArrayList<Integer> noteList,ArrayList<Integer> durationList){
        int[] bucket = {0,0,0,0,0,0,0,0,0,0,0,0};
        for(int i = 0; i < noteList.size();i++){
            int note = noteList.get(i)%12;
            for(int j = 0 ; j < key.length ; j++){
                for(int k = 0 ; k < key[0].length; k++){
                    if(note==key[j][k])
                    {bucket[j]+=durationList.get(i);
                        break;
                    }
                }
            }
        }
        int maxIndex=0;
        for(int i =1;i<bucket.length;i++){
            if(bucket[maxIndex]<bucket[i])maxIndex=i;
        }
        Log.d(LOG_TAG,Arrays.toString(bucket));
        return maxIndex;
    }


    public static void main(String[] args) throws Exception {
       // String fileDestination = "C:/test.wav";
        // pitchEst(MatlabMethod.wavRead(fileDestination));
    }
    public static void playNote(int note, int duration) {
    }
    //find note from frequency
    public static int findNote(double freq) {
        int noteAns = -2;
        // double percent = 0;
        double noteIndex = (Math.log((double) freq) - 2.794372868) / 0.0578;
        if (noteIndex - Math.floor(noteIndex) >= 0.5) {
            noteAns = (int) Math.floor(noteIndex) + 1;
            // percent = (noteIndex-Math.floor(noteIndex))*100;
        } else {
            noteAns = (int) Math.floor(noteIndex);
            // percent = 100-(noteIndex-Math.floor(noteIndex))*100;
        }
        return noteAns;
    }
}
