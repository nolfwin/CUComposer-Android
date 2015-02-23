package com.example.user.cucomposer_android;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wongse on 23/2/2558.
 */
public class Segment {
    public static List<Integer> segment(float[] amp){
        List<Integer> start = new ArrayList<Integer>();
        int k = 0;
        int size = 240; int inc = 80;
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
                e /= 160 + amp.length % inc;
                zcr /= 160 + amp.length % inc;
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
            if(e/zcr > max4 && zcr != 0)
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
                e /= 160 + amp.length % inc;
                zcr /= 160 + amp.length % inc;
            }
            else{
                e /= size;
                zcr /= size;
            }
			/*System.out.println("start: " + startIndex
					+ " e%: " + e/max1*100
					+ " zcr%: " + zcr/max2*100
					+ " e*zcr%: " + e*zcr/max3*100
					+ " e/zcr%: " + e/zcr/max4*100);*/
            if(voiced && (e/max1*100 < 9)){
                start.add(startIndex);
                voiced = false;
            }
            else if(!voiced && !(e/max1*100 < 9)){
                start.add(startIndex);
                voiced = true;
            }
            startIndex += inc;
        }
        return start;
    }
}
