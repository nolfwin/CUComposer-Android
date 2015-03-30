package com.example.user.cucomposer_android;

import com.example.user.cucomposer_android.entity.Note;
import com.example.user.cucomposer_android.utility.Calculator;
import com.example.user.cucomposer_android.utility.Key;
import com.example.user.cucomposer_android.utility.NeuralNetwork.NeuralNetwork;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class BarDetector {

    public static final double accuracy = 0.25;
    public static final double thd = 0.5;

    public static final double epsilon = 1.0e-7;

    private boolean hasWeight = false;
    private NeuralNetwork nn = new NeuralNetwork(17,10,1);
    private NeuralNetwork nn2 = new NeuralNetwork(17,10,1);
    private static String weightFile = "nn_barDetector_weight";
    private static String weightFileRound2 = "nn_barDetector2_weight";
    public List<Note> notes;
    private ArrayList<double[]> input;
    private static boolean[] skip;
    private static double predict2Threshold = 0.225;

    private int keyPitch;
    private boolean keyMode;

    public BarDetector(){
    }
    public BarDetector(List<Note> notes,int keyPitch,boolean keyMode){
        setKey(keyPitch,keyMode);
        setNotes(notes);
    }

    public void setKey(int keyPitch,boolean keyMode){
        this.keyMode = keyMode;
        this.keyPitch = keyPitch;
    }

    public void setNotes(List<Note> notes){
        this.notes = notes;
    }

    public double barDetect(List<Note> notes,int keyPitch,boolean keyMode){
        this.notes = notes;
        this.keyMode = keyMode;
        this.keyPitch = keyPitch;
        return barDetect();
    }

    public double barDetect(){
        if(!hasWeight){
            loadWeight();
            loadWeightRound2();
            hasWeight = true;
        }
        convertToInputFromNotes();
        return this.predict();
    }

    public void convertToInputFromNotes(){
        int[] projectedNotes = Key.ProjectNotes(notes, keyPitch, keyMode);
        input = new ArrayList<double[]>();
        for(Note aNote:notes){
            if(aNote.getPitch()<0)
                continue;
            double[] anInput = new double[16];
            anInput[2] = aNote.getDuration();

            anInput[6] = aNote.getOffset()%4.0;

            anInput[4] = Key.mapToKey(aNote.getPitch(), keyPitch, keyMode) + 1;

            anInput[7] = aNote.getPitch();
            int[] countNote = new int[8];
            int startOffset = (int)(aNote.getOffset()*4);
            for(int i=startOffset;i<startOffset+16;i++){
                if(i<projectedNotes.length){
                    countNote[0] += 1;
                }
                else{
                    countNote[projectedNotes[startOffset]] += 1;
                }
            }
            for(int i=0;i<countNote.length;i++){
                anInput[8+i] = countNote[i];
            }
            input.add(anInput);
        }
    }

    public void readFileToInput(final File file){
        input = new ArrayList<double[]>();
        try {

            Scanner in = new Scanner(file);
            String line = "";
            while(in.hasNext() && (line = in.nextLine())!=""){
                double[] data = new double[16];
                String[] stringData = line.split(" ");
                for(int i=0;i<16;i++){
                    data[i] = Double.parseDouble(stringData[i]);
                }
                input.add(data);

                //System.out.println(line);
            }
            in.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void prepareTrainData(final File file){

        readFileToInput(file);
        double[][] outputData = new double[input.size()][1];
        prepareData();
        for(int i=0;i<outputData.length;i++){
            double[] data = input.get(i);
            outputData[i][0] =  1 - Math.min(data[6], 4-data[6]) / 2;
        }
        nn.setOutputs(outputData);
    }


    public void prepareTrainDataRound2(double predictedOffset){
        prepareDataRound2(predictedOffset);
        double[][] outputData = new double[nn2.getInputs().length][1];
        int j=0;
        for(int i=0;i<input.size();i++){
            if(skip[i])
                continue;
            outputData[j][0] = (isEqualDouble(input.get(i)[6],0))?1:0;

            //System.out.println(Arrays.toString(nn.inputs[j])+ " "+Arrays.toString(outputData[j]));
            j+=1;

        }
        nn2.setOutputs(outputData);
    }

    public void prepareDataRound2(double predictedOffset){
        skip = new boolean[input.size()];
        int inputSize = 0;
        for(int i=0;i<input.size();i++){
            double offset = input.get(i)[6];
            if(offset<predictedOffset){
                offset += 4;
            }
            if(offset>=predictedOffset-epsilon && offset<=predictedOffset+0.5+epsilon ){
                skip[i] = false;
                inputSize += 1;
            }
            else{
                if(offset>predictedOffset){
                    offset -= 4;
                }
                if (offset <= predictedOffset + epsilon && offset >= predictedOffset - 0.5 - epsilon) {
                    skip[i] = false;
                    inputSize += 1;
                } else {
                    skip[i] = true;
                }
            }
        }
        double[][] inputData = new double[inputSize][17];
        double[][] oldInputData = nn.getInputs();
        int j=0;
        for(int i=0;i<skip.length;i++){
            if(skip[i])
                continue;
            inputData[j] = oldInputData[i];

            j++;
        }
        nn2.setInputs(inputData);
    }

    public void prepareData(){
        double avg = 0;
        for(int i=0;i<input.size();i++){
            avg += input.get(i)[7];
        }
        avg /= input.size();
        double[][] inputData = new double[input.size()][17];
        for(int i=0;i<inputData.length;i++){
            double[] data = input.get(i);
            inputData[i][(int) (data[4]-1)] = 1;
            for(int j=0;j<8;j++){
                inputData[i][j+7] = data[8+j]/16;
            }
            inputData[i][15] = (data[7] - avg + 12)/24;
            inputData[i][16] = data[2]/8;

            //System.out.println(Arrays.toString(inputData[i]));
        }
        nn.setInputs(inputData);
    }



    public void trainNN(final File folder,int epoch){
        int trainSet = folder.listFiles().length * 8 / 10;

        double learningRate = 0.01;
        double momentum = 0.5;
        nn.setLearningRate(learningRate);
        for(int i=0;i<epoch;i++){
            double trainError = 0;
            double testError = 0;
            int fNum = 0;
            nn.setLearningRate(learningRate);
            learningRate *= 0.9;
            nn.setMomentum(momentum);
            momentum *= 0.7;
            int fTrain = 0;
            int fTest = 0;
            int tp = 0;
            int fp = 0;
            int fn = 0;

            for (final File fileEntry : folder.listFiles()) {
                if(fNum<trainSet){
                    prepareTrainData(fileEntry);
                    trainError += nn.run(1, 0);
                    //System.out.println(trainError);
                    fTrain += 1;
                }
                else{
                    prepareTrainData(fileEntry);
                    double[][] output = nn.runPredict();
                    double[][] target = nn.getExpectedOutput();
                    for(int j=0;j<output.length;j++){
                        //System.out.println(""+output[j][0]+" "+nn.expectedOutputs[j][0]);
                        testError += Math.pow(output[j][0] - target[j][0], 2);
                        if(Math.abs(output[j][0]-target[j][0])<accuracy){
                            if(output[j][0]>thd){
                                tp+= 1;
                            }
                        }
                        else{
                            if(output[j][0]>thd){
                                fp += 1;
                            }
                            else{
                                fn += 1;
                            }
                        }
                    }
                    //System.out.println(""+testError);
                    fTest += 1;
                }
                fNum+=1;
            }
            System.out.println("train epoch "+i+": "+trainError + " "+fTrain);
            System.out.println("test epoch "+i+": "+testError + " "+fTest);
            System.out.println("precision "+ Calculator.precision(tp, fp)+"    recall "+Calculator.recall(tp, fn));
        }
    }

    public void trainNNRound2(final File folder, int epoch){
        int trainSet = folder.listFiles().length * 8 / 10;
        double learningRate = 0.01;
        double momentum = 0;
        nn.setLearningRate(learningRate);
        nn.setMomentum(momentum);
        loadWeight();
        loadWeightRound2();
        for(int i=0;i<epoch;i++){
            double trainError = 0;
            double testError = 0;
            int fNum = 0;
            //nn.learningRate *= 0.9;
            int fTrain = 0;
            int fTest = 0;
            int tp = 0;
            int fp = 0;
            int fn = 0;
            for (final File fileEntry : folder.listFiles()) {
                readFileToInput(fileEntry);
                double predictedOffset = predict();
                if(!isEqualDouble(predictedOffset,0) && predictedOffset < 3.5-epsilon){
                    fNum += 1;
                    continue;
                }
                prepareTrainDataRound2(predictedOffset);
                if(fNum<trainSet){
                    trainError += nn2.run(1, 0);
                    //System.out.println(trainError);
                    fTrain += 1;
                }
                else{
                    double[][] output = nn2.runPredict();
                    for(int j=0;j<output.length;j++){
                        //System.out.println(""+output[j][0]+" "+nn.expectedOutputs[j][0]);
                        double[][] target = nn2.getExpectedOutput();
                        testError += Math.pow(output[j][0] - target[j][0], 2);
                        if(Math.abs(output[j][0]- target[j][0])<accuracy){
                            if(output[j][0]>thd){
                                tp+= 1;
                            }
                        }
                        else{
                            if(output[j][0]>thd){
                                fp += 1;
                            }
                            else{
                                fn += 1;
                            }
                        }
                    }
                    //System.out.println(""+testError);
                    fTest += 1;
                }
                fNum+=1;
            }
            System.out.println("train epoch "+i+": "+trainError + " "+fTrain);
            System.out.println("test epoch "+i+": "+testError + " "+fTest);
            System.out.println("precision "+Calculator.precision(tp, fp)+"    recall "+Calculator.recall(tp, fn));
        }
        saveWeightRound2();
    }

    public static void trainMain(){
        File trainFolder = new File("C:\\Users\\Nuttapong\\Documents\\Java workspace\\PopTime\\src","trainingData");
        System.out.println(""+trainFolder.getPath());
        //loadWeight();
        BarDetector bt = new BarDetector();
        bt.trainNNRound2(trainFolder,1000);
        bt.saveWeight();
    }

    public static void testMain(boolean dp){

        File testFolder = new File("C:\\Users\\Nuttapong\\Documents\\Java workspace\\PopTime\\src","trainingData");
        BarDetector bt = new BarDetector();
        bt.loadWeight();
        bt.loadWeightRound2();
        double[] countPredictedOffset = new double[16];
        int trainSet = testFolder.listFiles().length*8/10;
        //int trainSet = 0;
        int fNum = 0;
        for (final File fileEntry : testFolder.listFiles()) {
            if(fNum<trainSet){
                fNum += 1;
                continue;
            }
            bt.readFileToInput(fileEntry);

            double predictedOffset = 0;
            if(dp)
                predictedOffset = bt.doublePredict();
            else
                predictedOffset = bt.predict();
            countPredictedOffset[(int)(predictedOffset*4)] += 1;
            fNum += 1;
        }
        Calculator.normalize(countPredictedOffset);
        System.out.println(Arrays.toString(countPredictedOffset));
    }

    public static void main(String[] args){
        testMain(false);
        testMain(true);
//		for(int i=0;i<20;i++){
//			predict2Threshold = 0.2+0.025*i;
//			System.out.println("thd: "+predict2Threshold);
//			testMain(true);
//		}
    }

    public double predict(){
        prepareData();
        double[][] output = nn.runPredict();
        double[] score = new double[16];
        int[] count = new int[16];
        int countTotal = 0;
        for(int i=0;i<output.length;i++){
            int offset = (int)(input.get(i)[6]*4);
            //score[offset] += Math.pow(1-output[i][0],1);
            score[offset] += output[i][0];
            count[offset] += 1;
            countTotal += 1;
        }
        double best = 0;
        double bestScore = 0;
        for(int i=0;i<score.length;i++){
            if(count[i]<countTotal/10)
                continue;
            if(bestScore<score[i]/count[i]){
                bestScore = score[i]/count[i];
                best = i;
            }
        }
        System.out.println(Arrays.toString(score));
        System.out.println(Arrays.toString(count));
        System.out.println("return "+best);
        return best/4;
    }

    public double doublePredict(){
        double predictedOffset = predict();
        prepareDataRound2(predictedOffset);
        double[][] output = nn2.runPredict();
        double[] score = new double[16];
        int[] count = new int[16];
        int countTotal = 0;
        int j = 0;
        for(int i=0;i<input.size();i++){
            if(skip[i])
                continue;
            int offset = (int)(input.get(i)[6]*4);
//            if(output[j][0]>predict2Threshold){
//                score[offset] += 1;
//            }
            score[offset] += output[j][0];
            count[offset] += 1;
            countTotal += 1;
            j+=1;
        }
        double best = 0;
        double bestScore = 0;
        for(int i=0;i<score.length;i++){
            if(count[i]==0)
                continue;
            if(bestScore<score[i]/count[i]){
                bestScore = score[i]/count[i];
                best = i;
            }
        }
        System.out.println(Arrays.toString(score));
        System.out.println(Arrays.toString(count));
        //System.out.println("return "+best);
        return best/4;
    }

    public void loadWeight(){

        nn.loadWeight(R.raw.nn_bar_detector_weight,MainActivity.getAppContext());
    }

    public void loadWeightRound2(){
        nn2.loadWeight(R.raw.nn_bar_detector2_weight,MainActivity.getAppContext());
    }

    public void saveWeight(){
        try {
            nn.saveWeight(weightFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveWeightRound2(){
        try {
            nn2.saveWeight(weightFileRound2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final boolean isEqualDouble(double a,double b){
        if (Math.abs(a-b)<epsilon)
            return true;
        else{
            return false;
        }
    }
}
