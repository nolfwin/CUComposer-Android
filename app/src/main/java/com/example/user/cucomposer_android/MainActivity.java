package com.example.user.cucomposer_android;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.NoiseSuppressor;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.cucomposer_android.entity.Note;
import com.example.user.cucomposer_android.entity.Part;

import org.jfugue.Player;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.kshoji.javax.sound.midi.UsbMidiSystem;

public class MainActivity extends Activity {

    private static final String LOG_TAG = "debugger";
    Integer[] freqset = {11025, 16000, 22050, 44100};
    private ArrayAdapter<Integer> adapter;
    private AudioRecord audioRecord = null;
    private AudioTrack audioTrack = null;
    private long startTime = 0;
    private boolean startPlay = false;
    private int carryPlaybackOffset;
    private int bufferSize = 4096;
    private DataInputStream dataInputStream;
    private int partSize = 4;
    private LinearLayout[] partLayout = new LinearLayout[partSize];
    private TextView[] partName = new TextView[partSize];
    private Button[] recordButton = new Button[partSize];
    private Button[] playButton = new Button[partSize];
    private Button[] pitchButton = new Button[partSize];
    private Button[] modifyButton = new Button[partSize];
    private boolean isFoundAudioRecord = false;
    public static int runningId = -1;

    public static int sampleRate;
    public static int audioFormat;
    public static int channelConfig;

    public static Part[] partArray = new Part[4];
    private Handler timerHandler = new Handler();

    Button nextButton;
    Button mergeButton;

    TextView timer;

    Boolean recording;
    Boolean playing;
    UsbMidiSystem usbMidiSystem;
    /**
     * Called when the activity is first created.
     */
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout outerLayout = (LinearLayout) findViewById(R.id.outerLayout);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        for(int i=0;i<partSize;i++) {
            partLayout[i] = new LinearLayout(this);
            partLayout[i].setOrientation(LinearLayout.HORIZONTAL);
            outerLayout.addView(partLayout[i],layoutParams);

            partName[i] = new TextView(this);
            partName[i].setText("part"+i);
            partLayout[i].addView(partName[i],layoutParams);

            recordButton[i] = new Button(this);
            recordButton[i].setText("record");
            recordButton[i].setId(i);
            recordButton[i].setOnClickListener(startRecOnClickListener);
            partLayout[i].addView(recordButton[i],layoutParams);

            playButton[i] = new Button(this);
            playButton[i].setText("play");
            playButton[i].setId(i);
            playButton[i].setOnClickListener(playBackOnClickListener);
            partLayout[i].addView(playButton[i],layoutParams);

            pitchButton[i] = new Button(this);
            pitchButton[i].setText("pitch");
            pitchButton[i].setId(i);
            pitchButton[i].setOnClickListener(pitchOnClickListener);
            partLayout[i].addView(pitchButton[i],layoutParams);

            modifyButton[i] = new Button(this);
            modifyButton[i].setText("modify");
            modifyButton[i].setId(i);
            modifyButton[i].setOnClickListener(modifyOnClickListener);
            partLayout[i].addView(modifyButton[i],layoutParams);
        }
        timer = (TextView) findViewById(R.id.timer);
        nextButton = (Button) findViewById(R.id.nextButton);
        mergeButton = (Button) findViewById(R.id.mergeButton);


        nextButton.setOnClickListener(nextButtonOnClickListener);
        mergeButton.setOnClickListener(mergeButtonOnClickListener);

        File appFolder = new File(Config.appFolder);
        if (!appFolder.exists()) {
            if (!appFolder.mkdir()) {
                Log.d(LOG_TAG, "cant make app folder " + Config.appFolder);
            }

        }
    }
    @Override
    OnClickListener startRecOnClickListener
            = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if(runningId>=0){
                recording = false;
                Button b = (Button) v;
                b.setText("Record");
            }
            else {
                runningId = v.getId();

                Thread recordThread = new Thread(recordRunnable);
                recordThread.start();
                Button b = (Button) v;
                b.setText("Stop");
                disableOtherButton(runningId,true);
            }
        }
    };

    private Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {

            recording = true;
            if (!startRecord(Config.appFolder + "/test"+runningId+".pcm"))
                Log.d(LOG_TAG, "cant find audioRecord");
        }
    };

    private void disableOtherButton(int id,boolean isRecord){
        for(int i=0;i<partSize;i++){
            if(!isRecord||id!=i)
                recordButton[i].setEnabled(false);
            if(isRecord||id!=i)
                playButton[i].setEnabled(false);
            if(isRecord||id!=i)
                pitchButton[i].setEnabled(false);
            if(isRecord||id!=i)
                modifyButton[i].setEnabled(false);
        }
    }

    private void enableAllButton(){
        for(int i=0;i<partSize;i++){
            recordButton[i].setEnabled(true);
            playButton[i].setEnabled(true);
            pitchButton[i].setEnabled(true);
            modifyButton[i].setEnabled(true);
        }
    }

    private Runnable playRunnable = new Runnable() {
        @Override
        public void run() {
            playRecord(Config.appFolder+"/test"+runningId+".pcm");
        }
    };



    OnClickListener stopRecOnClickListener
            = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            recording = false;
        }
    };

    OnClickListener playBackOnClickListener
            = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(runningId<0) {
                runningId = v.getId();
                Thread playThread = new Thread(playRunnable);
                playThread.start();
                disableOtherButton(runningId, false);
                Button b = (Button) v;
                b.setText("Stop");
            }
            else{
                playing = false;
                Button b = (Button) v;
                b.setText("Play");

            }
            //playRecord2(Config.appFolder + "/test" + v.getId() + ".pcm");
        }

    };
    OnClickListener mergeButtonOnClickListener =  new OnClickListener() {
        @Override
        public void onClick(View v) {
//            Player player = new Player();
//            player.play("C D E F G A B");
//
//            Toast toast = Toast.makeText(getApplicationContext(),"Let's Merge",Toast.LENGTH_SHORT);
//            toast.show();

            for(int i = 0; i < partSize ; i++){
                runningId = i;
                partArray[i]=null;
                partArray[i] = getPartFromRunningID(runningId);
                if(partArray[i]==null) Log.d("NULL","running ID = "+i+" is null");
                else  Log.d("Part","get running ID = "+i+" part");
                Log.d("Key",partArray[i].toString());
            }
            int baseKey = calculateMediumKey(partArray);
            int meanBpm = calculateMeanBpm(partArray);
            for(int i = 0 ; i < partArray.length;i++){
                if(partArray[i]==null) continue;
                partArray[i].setKey(baseKey);
                partArray[i].setBpm(meanBpm);
                Log.d("Key",partArray[i].toString());
            }
//            Toast toast = Toast.makeText(getApplicationContext(),"Let's Merge",Toast.LENGTH_SHORT);
//            toast.show();
        }
    };
    OnClickListener nextButtonOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            callNextActivity();
        }
    };
    OnClickListener pitchOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
//pitchfucca
            runningId = v.getId();
            getPartFromRunningID(runningId);
        }
    };
    OnClickListener modifyOnClickListener = new OnClickListener() {
        @Override
            public void onClick(View v) {
                runningId = v.getId();
                Log.d("RUNNING ID","Running ID "+runningId);
                partArray[runningId]=null;
                partArray[runningId] = getPartFromRunningID(runningId);
                runningId=v.getId();
                if(partArray[runningId]==null) {
                    Log.d("NULL","running ID = "+runningId+" is null");
                    return;
                }
                else  Log.d("Part","get running ID = "+runningId+" part");

            callNextActivity();
        }
    };
    public int calculateMeanBpm(Part[] partArray){
        int ans = 0;
        int count = 0;
        for(int i = 0 ; i < partArray.length;i++){
            if(partArray[i]==null)continue;
            ans+=partArray[i].getBpm();
            count++;
        }
        return ans/count;
    }
    public int calculateMediumKey(Part[] partArray){
        boolean[] isMajor = new boolean[partArray.length];
        int baseKey = -1;
        for(int i = 0 ; i< partArray.length;i++){
            if(partArray[i]==null) continue;
            if(partArray[i].getKey()>11)isMajor[i]=false;
            else isMajor[i]=true;
        }
        boolean isAllMajor=true;
        boolean isAllMinor=true;
        for(int i = 0; i < isMajor.length;i++){
            if(partArray[i]==null)continue;
            if(!isMajor[i]){
                isAllMajor=false;
            }
            else{
                isAllMinor=false;
            }
        }

        if(isAllMajor||isAllMinor){
            //now based on the mode value of the key in each part
            int[] keyArray = new int[partArray.length];
            for(int i = 0; i < partArray.length;i++){
                if(partArray[i]==null)keyArray[i]=-1;
                else keyArray[i]=partArray[i].getKey();
            }
            baseKey = getMode(keyArray);
            for(int i = 0; i < partArray.length;i++){
                if(partArray[i]==null)continue;
                transpose(partArray[i],baseKey-partArray[i].getKey());
            }

        }
        else{
            float[] mergeAudio = new float[0];

            for(int i = 0; i < partArray.length;i++){
                float[] audioFloats = getAudioFloatFromRunningID(i);
                if(audioFloats==null)continue;
                else mergeAudio = concat(mergeAudio,audioFloats);
            }
            List<Integer> segmentOutput = Segment.segment(mergeAudio);
            try {
                Part mergePart = Pitch.pitchEstWithoutSegment(mergeAudio,-1);
                baseKey = mergePart.getKey();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for(int i = 0; i < partArray.length;i++){
                float[] audioFloats = getAudioFloatFromRunningID(i);
                segmentOutput = Segment.segment(audioFloats);
                if(audioFloats==null)continue;
                try {
                    partArray[i] = Pitch.pitchEstWithoutSegment(audioFloats,baseKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return baseKey;
    }
    public static int getMode(int[] values) {
        HashMap<Integer,Integer> freqs = new HashMap<Integer,Integer>();
        for (int val : values) {
            if(val<0)continue;
            Integer freq = freqs.get(val);
            freqs.put(val, (freq == null ? 1 : freq+1));
        }

        int mode = 0;
        int maxFreq = 0;

        for (Map.Entry<Integer,Integer> entry : freqs.entrySet()) {
            int freq = entry.getValue();
            if (freq > maxFreq) {
                maxFreq = freq;
                mode = entry.getKey();
            }
        }

        return mode;
    }
    public float[] concat(float[] a, float[] b) {
        int aLen = a.length;
        int bLen = b.length;
        float[] c= new float[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
    public void transpose(Part part,int trans){
        List<Note> noteList = part.getNoteList();

        for(int i = 0 ; i < noteList.size();i++){
            Note note = noteList.get(i);
            note.setPitch(note.getPitch()+trans);
            noteList.set(i,note);
        }
        part.setNoteList(noteList);
    }
    public float[] getAudioFloatFromRunningID(int ID){
        runningId = ID;
        File file = new File(Config.appFolder + "/test"+runningId+".pcm");
        int shortSizeInBytes = Short.SIZE/Byte.SIZE;

        int bufferSizeInBytes = (int)(file.length()/shortSizeInBytes);
        short[] audioData = new short[bufferSizeInBytes];

        boolean done = true;
        if(audioRecord==null){
            audioRecord = findAudioRecord();
        }
        int i = 0;
        try {
            bufferSize = AudioTrack.getMinBufferSize(audioRecord.getSampleRate(),AudioFormat.CHANNEL_OUT_MONO,audioRecord.getAudioFormat());
            InputStream inputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            dataInputStream = new DataInputStream(bufferedInputStream);


            while(dataInputStream.available() > 0){
                if(i==audioData.length){
                    done = false;
                    break;
                }
                audioData[i] = dataInputStream.readShort();
                i++;
            }
            dataInputStream.close();
        } catch (FileNotFoundException e) {
            Log.d(LOG_TAG,"error file "+ID+" file not found");
            return  null;

        } catch (IOException e) {
            Log.d(LOG_TAG,"error file "+ID+" IOException");
            return null;
        }
        Log.d(LOG_TAG,"audioData size: "+bufferSizeInBytes+" "+audioData.length+" "+i);
        return floatMe(audioData);
    }
    public  Part getPartFromRunningID(int ID){
        runningId = ID;
        float[] audioFloats = getAudioFloatFromRunningID(ID);

        Part partWithSegment=null;
        Part partWithoutSegment=null;
        try {
            List<Integer> segmentOutput = Segment.segment(audioFloats);
           partWithoutSegment= Pitch.pitchEstWithoutSegment(audioFloats,-1);
           partWithSegment =  Pitch.pitchEst(audioFloats,segmentOutput,-1);

            Log.d(LOG_TAG,Arrays.toString(segmentOutput.toArray()));
            Log.d(LOG_TAG,audioFloats.length+"");
            Log.d(LOG_TAG,"Number of syllables:"+segmentOutput.size()/2);
            // Pitch.pitchEst(floatMe(audioData));
        } catch (Exception e) {
            e.printStackTrace();
        }
        runningId= -1;
        return partWithoutSegment;
    }
    public static short[] shortMe(byte[] bytes) {
        short[] out = new short[bytes.length / 2]; // will drop last byte if odd number
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        for (int i = 0; i < out.length; i++) {
            out[i] = bb.getShort();
        }
        return out;
    }
    public static float[] floatMe(short[] pcms) {
        float[] floaters = new float[pcms.length];
        for (int i = 0; i < pcms.length; i++) {
            floaters[i] = pcms[i];
        }
        return floaters;
    }
    private void callNextActivity() {
        Intent nextIntent = new Intent(this, NoteEditor.class);
        startActivity(nextIntent);
    }


    public AudioRecord findAudioRecord() {
        int[] mSampleRates = new int[]{11025, 22050, 44100, 48000};
        //int[] mSampleRates = new int[] { 48000, 44100, 22050, 11025, 8000 };
        for (int rate : mSampleRates) {
            for (short audioFormat : new short[]{AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT}) {
                for (short channelConfig : new short[]{AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO}) {
                    try {
                        Log.d(LOG_TAG, "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
                                + channelConfig);
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, rate, channelConfig, audioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                                Log.d(LOG_TAG, "success");
                                MainActivity.sampleRate = recorder.getSampleRate();
                                MainActivity.audioFormat = recorder.getAudioFormat();
                                MainActivity.channelConfig = recorder.getChannelConfiguration();
                                isFoundAudioRecord = true;
                                return recorder;
                            } else {
                                Log.d(LOG_TAG, "uninitial recorder");
                            }
                        }
                    } catch (Exception e) {
                        Log.e(LOG_TAG, rate + "Exception, keep trying.", e);
                    }
                }
            }
        }
        return null;
    }


    private boolean startRecord(String fileName) {

        File file = new File(fileName);
        try {
            file.createNewFile();

            OutputStream outputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
            if (audioRecord == null) {
                if(isFoundAudioRecord){
                    audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,channelConfig,audioFormat,AudioRecord.getMinBufferSize(sampleRate,channelConfig,audioFormat));
                }
                else {
                    audioRecord = findAudioRecord();
                }
                if(NoiseSuppressor.isAvailable()) {
                    Log.d(LOG_TAG,"Noise suppressor is available");
                    NoiseSuppressor.create(audioRecord.getAudioSessionId());
                }
                else{
                    Log.d(LOG_TAG,"Noise suppressor is not available");
                }
            }
            int minBufferSize = AudioRecord.getMinBufferSize(audioRecord.getSampleRate(), audioRecord.getChannelConfiguration(), audioRecord.getAudioFormat());
            short[] audioData = new short[minBufferSize];

            if (audioRecord == null) {
                return false;
            }
            else{
                if (audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED){
                    audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,channelConfig,audioFormat,AudioRecord.getMinBufferSize(sampleRate,channelConfig,audioFormat));
                }
            }

            try {
                audioRecord.startRecording();
                startTime = SystemClock.uptimeMillis();
                timerHandler.postDelayed(updateTimerMethod, 300);


            } catch (Exception e) {

                e.printStackTrace();
                return false;
            }
            while (recording) {
                int numberOfShort= audioRecord.read(audioData, 0, minBufferSize);
                for (int i = 0; i < numberOfShort; i++) {
                    dataOutputStream.writeShort(audioData[i]);
                }
            }
            audioRecord.stop();
            audioRecord.release();
            dataOutputStream.close();
            runningId = -1;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private Runnable updateTimerMethod = new Runnable() {

        public void run() {
            if(runningId>=0) {
                long timeInMillies = SystemClock.uptimeMillis() - startTime;
                long finalTime = timeInMillies;

                int seconds = (int) (finalTime / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                int milliseconds = (int) (finalTime % 1000);
                timer.setText("" + minutes + ":"
                        + String.format("%02d", seconds) + ":"
                        + String.format("%03d", milliseconds));
                timerHandler.postDelayed(this, 100);
            }
            else{
                timerHandler.removeCallbacks(this);
                enableAllButton();
            }
        }


    };

    private Runnable updatePlayTimerMethod = new Runnable() {
        @Override
        public void run() {
            if(runningId>=0){
                long timeInMillies = SystemClock.uptimeMillis() - startTime;
                long finalTime = timeInMillies;

                int seconds = (int) (finalTime / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                int milliseconds = (int) (finalTime % 1000);
                timer.setText("" + minutes + ":"
                        + String.format("%02d", seconds) + ":"
                        + String.format("%03d", milliseconds));
                timerHandler.postDelayed(this, 100);

            }
            else{
                timerHandler.removeCallbacks(this);
                enableAllButton();
            }
        }
    };


    private void playRecord(String fileName){
        File file = new File(fileName);
        int bufferSizeInBytes =(int)file.length();
        Log.d(LOG_TAG,"audioData size: "+bufferSizeInBytes);
        short[] audioData = new short[bufferSize];
        boolean done = true;
        if(!isFoundAudioRecord){
            audioRecord = findAudioRecord();
            if(audioRecord!=null)
                audioRecord.release();
            else
                return;
        }

        try {

            int channelOut;
            if(channelConfig == AudioFormat.CHANNEL_IN_STEREO){
                channelOut = AudioFormat.CHANNEL_OUT_STEREO;
            }
            else{
                channelOut = AudioFormat.CHANNEL_OUT_MONO;
            }
            bufferSize = AudioTrack.getMinBufferSize(sampleRate,channelOut,audioFormat);
            InputStream inputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            dataInputStream = new DataInputStream(bufferedInputStream);

            int i = 0;
            while(dataInputStream.available() > 0){
                if(i==audioData.length){
                    done = false;
                    break;
                }
                audioData[i] = dataInputStream.readShort();
                i++;
            }

            audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    channelOut,
                    audioFormat,
                    bufferSize,
                    AudioTrack.MODE_STREAM);

            audioTrack.play();
            playing = true;
            startTime = SystemClock.uptimeMillis();
            timerHandler.post(updatePlayTimerMethod);
            while(!done&&playing) {
                done = true;
                i = 0;
                while (dataInputStream.available() > 0) {
                    if (i==audioData.length) {
                        done = false;
                        break;
                    }
                    audioData[i] = dataInputStream.readShort();
                    i++;
                }
                audioTrack.write(audioData,0,i);
            }
            dataInputStream.close();
            audioTrack.stop();
            audioTrack.release();
            runningId = -1;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}