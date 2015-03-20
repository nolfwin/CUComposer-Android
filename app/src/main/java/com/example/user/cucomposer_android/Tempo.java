package com.example.user.cucomposer_android;

import android.util.Log;

import com.example.user.cucomposer_android.entity.Note;
import com.example.user.cucomposer_android.entity.Part;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Wongse on 23/2/2558.
 */
public class Tempo {
    public static int tempoDetect(List<Integer> duration){
        int increment = 1, min = 60, max = 120;
        double minError = 999999999;
        int minBpm = 0;
        for(int bpm=min ; bpm<=max ; bpm+=increment){
            double error = 0;
            for(int i=0 ; i<duration.size() ; i++){
                int num1,num2;
                double error1,error2;
                double div = 15000.0 / bpm;
                num1 = (int) (duration.get(i) / div);
                error1 = (duration.get(i) / div - num1) / increment;
                num2 = (int) Math.ceil(duration.get(i) / div);
                error2 = (num2 - duration.get(i) / div) / increment;
                //System.out.println("duration: " + duration[i] + " num1: " + num1 + " num2: " + num2 + " BPM: " + BPM);
                if(error1 < error2){
                    error += error1 * error1;
                }
                else if(error1 > error2){
                    error += error2 * error2;
                }
                else if(num1 % 2 == 0){
                    error += error1 * error1;
                }
                else{
                    error += error2 * error2;
                }
                //System.out.println(error1 + " " + error2);
            }
            //System.out.println("BPM: " + BPM + " error: " + error);
            if(minError > error){
                minError = error;
                minBpm = bpm;
            }
        }
        //System.out.println(minError + " " + minBPM);
        return minBpm;
    }

    public static Part toPart(List<Integer> note, List<Integer> duration, int bpm, int key){
        double div = 15000.0 / bpm;
        Log.d("noteList", Arrays.toString(note.toArray()));
        Log.d("noteDur",Arrays.toString(duration.toArray()));
        Log.d("bpm","THE BPM IS "+bpm);
        List<Note> noteList = new ArrayList<Note>();
        for(int i=0;i<duration.size();i++){
            if(duration.get(i)*2 < div)
                continue;
            int duration1 = (int) (duration.get(i) / div);
            double error1 = (duration.get(i) / div - duration1);
            int duration2 = (int) Math.ceil(duration.get(i) / div);
            double error2 = (duration2 - duration.get(i) / div);
            if(error1 < error2) {
                if(duration1 == 0)
                    continue;
                noteList.add(new Note(note.get(i), ((float)duration1)/4));
            }
            else if(error1 > error2) {
                if(duration2 == 0)
                    continue;
                noteList.add(new Note(note.get(i), ((float)duration2)/4));
            }
            else if(duration1 % 2 == 0) {
                if(duration1 == 0)
                    continue;
                noteList.add(new Note(note.get(i),((float) duration1)/4));
            }
            else {
                if(duration2 == 0)
                    continue;
                noteList.add(new Note(note.get(i), ((float)duration2)/4));
            }
        }
            Log.d("noteListShow",Arrays.toString(noteList.toArray()));
        return new Part(noteList, bpm, key,Part.PartType.values()[MainActivity.runningId]);
    }
}
