package com.example.user.cucomposer_android.utility;

/**
 * Created by Nuttapong on 2/16/2015.
 */
public class Calculator {
    public static final double entropy(double p){
        return -p*(Math.log10(p)/Math.log10(2));
    }
    public static final void normalize(double[] arr){
        double sum = 0;
        for(int i=0;i<arr.length;i++){
            sum += arr[i];
        }
        if(sum!=0) {
            for (int i = 0; i < arr.length; i++) {
                arr[i] /= sum;
            }
        }
    }

    public static final void normalizeMinus(double[] arr,double floorValue){
        double min = Double.MAX_VALUE;
        for(int i=0;i<arr.length;i++){

        }
    }

    public static final double calEntropyArray(double[] observationArray, double[] probArray){
        double retEntropy = 1;
        for(int i=0;i<observationArray.length;i++){
            double p = 0;
            if(observationArray[i]==0&&probArray[i]==0){
                p = 1.0/1000;
            }
            else{
                if(probArray[i]==0){
                    p = 1.0/100/probArray[i];
                }
                else{
                    if(observationArray[i]==0){
                        p = 1.0/100/observationArray[i];
                    }
                    else{
                        p = observationArray[i]/probArray[i];
                        if(p>1){
                            p = 1.0/p;
                        }
                    }
                }
                if(p>=1){
                    p = 0.99;
                }
                retEntropy += entropy(p);
            }
        }
        return retEntropy;
    }

    public static final double calLogLikelihoodArray(double[] observationArray,double[] probArray){
        double returnValue = 0;
        for(int i=0;i<observationArray.length;i++){
            if(probArray[i]==0){
                probArray[i] = 0.00001;
            }
            returnValue += Math.log10(probArray[i]) * observationArray[i];
        }
        return returnValue;
    }

    public static final double calDotArray(double[] observationArray,double[] probArray){
        double returnValue = 0;
        for(int i=0;i<observationArray.length;i++){
            if(probArray[i]==0){
                probArray[i] = 0.00001;
            }
            returnValue += probArray[i] * observationArray[i];
        }
        return returnValue;
    }

    public static void normalize(double[][] arr){
        double sum = 0;
        for(int i=0;i<arr.length;i++){
            for(int j=0;j<arr[0].length;j++){
                sum += arr[i][j];
            }
        }
        if(sum != 0){
            for(int i=0;i<arr.length;i++){
                for(int j=0;j<arr[0].length;j++){
                    arr[i][j] /= sum;
                }
            }
        }
    }

    public static final void calLogArray(double arr[]){
        for(int i=0;i<arr.length;i++){
            if(arr[i]==0){
                arr[i] = 0.000001;
            }
            arr[i] = Math.log10(arr[i]);
        }
    }
}