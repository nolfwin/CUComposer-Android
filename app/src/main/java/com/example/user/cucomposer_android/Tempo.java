package com.example.user.cucomposer_android;

/**
 * Created by Wongse on 23/2/2558.
 */
public class Tempo {
    public static int tempoDetect(int[] duration){
        int increment = 1, min = 60, max = 120;
        double minError = 999999999;
        int minBpm = 0;
        for(int bpm=min ; bpm<=max ; bpm+=increment){
            double error = 0;
            for(int i=0 ; i<duration.length ; i++){
                int num1,num2;
                double error1,error2;
                double div = 15000.0 / bpm;
                num1 = (int) (duration[i] / div);
                error1 = (duration[i] / div - num1) / increment;
                num2 = (int) Math.ceil(duration[i] / div);
                error2 = (num2 - duration[i] / div) / increment;
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


}
