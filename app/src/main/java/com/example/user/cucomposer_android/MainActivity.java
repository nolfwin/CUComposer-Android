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

public class MainActivity extends Activity {

    private static final String LOG_TAG = "debugger";
    Integer[] freqset = {8000, 11025, 16000, 22050, 44100};
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
    private int runningId = -1;


    private Handler timerHandler = new Handler();

    Button nextButton;
    TextView timer;

    Boolean recording;
    Boolean playing;

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

        }
        timer = (TextView) findViewById(R.id.timer);
        nextButton = (Button) findViewById(R.id.nextButton);

        nextButton.setOnClickListener(nextButtonOnClickListener);

        File appFolder = new File(Config.appFolder);
        if (!appFolder.exists()) {
            if (!appFolder.mkdir()) {
                Log.d(LOG_TAG, "cant make app folder " + Config.appFolder);
            }

        }
    }

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
        }
    }

    private void enableAllButton(){
        for(int i=0;i<partSize;i++){
            recordButton[i].setEnabled(true);
            playButton[i].setEnabled(true);
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

    OnClickListener nextButtonOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            callNextActivity();
        }
    };
    OnClickListener pitchOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast toast = Toast.makeText(getApplicationContext(), "in Progress...", Toast.LENGTH_SHORT);
            toast.show();
        }
    };

    private void callNextActivity() {
        Intent nextIntent = new Intent(this, NoteEditor.class);
        startActivity(nextIntent);
    }


    public AudioRecord findAudioRecord() {
        int[] mSampleRates = new int[]{8000, 11025, 22050, 44100, 48000};
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
                audioRecord = findAudioRecord();
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

            try {
                audioRecord.startRecording();
                startTime = SystemClock.uptimeMillis();
                timerHandler.postDelayed(updateTimerMethod, 300);


            } catch (Exception e) {

                e.printStackTrace();
            }
            while (recording) {
                int numberOfShort = audioRecord.read(audioData, 0, minBufferSize);
                for (int i = 0; i < numberOfShort; i++) {
                    dataOutputStream.writeShort(audioData[i]);
                }
            }
            audioRecord.stop();
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
        int shortSizeInBytes = Short.SIZE/Byte.SIZE;
        int bufferSizeInBytes = (int)(file.length()/shortSizeInBytes);
        Log.d(LOG_TAG,"audioData size: "+bufferSizeInBytes);
        short[] audioData = new short[bufferSize];
        boolean done = true;
        if(audioRecord==null){
            audioRecord = findAudioRecord();

        }

        try {
            bufferSize = AudioTrack.getMinBufferSize(audioRecord.getSampleRate(),AudioFormat.CHANNEL_OUT_MONO,audioRecord.getAudioFormat());
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
                    audioRecord.getSampleRate(),
                    AudioFormat.CHANNEL_OUT_MONO,
                    audioRecord.getAudioFormat(),
                    //bufferSizeInBytes,
                    bufferSize*shortSizeInBytes,
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