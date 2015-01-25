package com.example.user.cucomposer_android;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

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
    private long startTime = 0;

    private Handler timerHandler = new Handler();

    Spinner spFrequency;
    Button startRec, stopRec, playBack, nextButton;
    TextView timer;

    Boolean recording;
    /** Called when the activity is first created. */
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startRec = (Button)findViewById(R.id.startrec);
        stopRec = (Button)findViewById(R.id.stoprec);
        playBack = (Button)findViewById(R.id.playback);
        timer = (TextView)findViewById(R.id.timer);
        nextButton = (Button)findViewById(R.id.nextButton);

        startRec.setOnClickListener(startRecOnClickListener);
        stopRec.setOnClickListener(stopRecOnClickListener);
        playBack.setOnClickListener(playBackOnClickListener);
        nextButton.setOnClickListener(nextButtonOnClickListener);

        spFrequency = (Spinner)findViewById(R.id.frequency);
        adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, freqset);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFrequency.setAdapter(adapter);

    }

    OnClickListener startRecOnClickListener
            = new OnClickListener(){

        @Override
        public void onClick(View arg0) {

            Thread recordThread = new Thread(new Runnable(){

                @Override
                public void run() {
                    recording = true;
                    if(!startRecord())
                    Log.d(LOG_TAG,"cant find audioRecord");
                }

            });

            recordThread.start();

        }};

    OnClickListener stopRecOnClickListener
            = new OnClickListener(){

        @Override
        public void onClick(View arg0) {
            recording = false;
        }};

    OnClickListener playBackOnClickListener
            = new OnClickListener(){

        @Override
        public void onClick(View v) {
            playRecord();

        }

    };

    OnClickListener nextButtonOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            callNextActivity();
        }
    };

    private void callNextActivity(){
        Intent nextIntent = new Intent(this,NoteEditor.class);
        startActivity(nextIntent);
    }


    public AudioRecord findAudioRecord() {
        //int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100, 48000 };
        int[] mSampleRates = new int[] { 48000, 44100, 22050, 11025, 8000 };
        for (int rate : mSampleRates) {
            for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
                for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
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
                            }
                            else{
                                Log.d(LOG_TAG,"uninitial recorder");
                            }
                        }
                    } catch (Exception e) {
                        Log.e(LOG_TAG, rate + "Exception, keep trying.",e);
                    }
                }
            }
        }
        return null;
    }


    private boolean startRecord(){

        File file = new File(Environment.getExternalStorageDirectory(), "test.pcm");

        //MainActivity.sampleFreq = (Integer)spFrequency.getSelectedItem();

        try {
            file.createNewFile();

            OutputStream outputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
            audioRecord = findAudioRecord();
            int minBufferSize = AudioRecord.getMinBufferSize(audioRecord.getSampleRate(),audioRecord.getChannelConfiguration(),audioRecord.getAudioFormat());
            short[] audioData = new short[minBufferSize];
            //int[] sampleFreqArr = { 8000, 11025, 22050, 44100 };
            //sampleFreq = sampleFreqArr[0]

//            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
//                    sampleFreq,
//                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
//                    AudioFormat.ENCODING_PCM_16BIT,
//                    minBufferSize);

            if(audioRecord == null){
                return false;
            }
            try {
                audioRecord.startRecording();
                startTime = SystemClock.uptimeMillis();
                timerHandler.postDelayed(updateTimerMethod, 300);


            }
            catch (Exception e){

                e.printStackTrace();
            }
            while(recording){
                int numberOfShort = audioRecord.read(audioData, 0, minBufferSize);
                for(int i = 0; i < numberOfShort; i++){
                    dataOutputStream.writeShort(audioData[i]);
                }
            }
            timerHandler.removeCallbacks(updateTimerMethod);
            audioRecord.stop();
            dataOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    void playRecord(){

        File file = new File(Environment.getExternalStorageDirectory(), "test.pcm");

        int shortSizeInBytes = Short.SIZE/Byte.SIZE;

        int bufferSizeInBytes = (int)(file.length()/shortSizeInBytes);
        short[] audioData = new short[bufferSizeInBytes];

        try {
            InputStream inputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);

            int i = 0;
            while(dataInputStream.available() > 0){
                audioData[i] = dataInputStream.readShort();
                i++;
            }

            dataInputStream.close();
            int count = 2048 * 1024;
            byte[] byteData = null;
            byteData = new byte[(int)count];
            FileInputStream in = new FileInputStream( file );
            int bytesread = 0, ret = 0;
            int size = (int) file.length();
            int intSize = AudioTrack.getMinBufferSize(audioRecord.getSampleRate(),AudioFormat.CHANNEL_OUT_MONO,
                    audioRecord.getAudioFormat());
            AudioTrack audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    audioRecord.getSampleRate(),
                    AudioFormat.CHANNEL_OUT_MONO,
                    audioRecord.getAudioFormat(),
                    intSize,
                    AudioTrack.MODE_STREAM);

            audioTrack.play();

            while (bytesread < size) {
                ret = in.read( byteData,0, count);
                if (ret != -1) {
                            // Write the byte array to the track
                    audioTrack.write(byteData,0, ret);
                    bytesread += ret;

                }
                else break;
            }

            in.close();
            audioTrack.stop();
            audioTrack.release();

            //audioTrack.write(audioData, 0, bufferSizeInBytes);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Runnable updateTimerMethod = new Runnable() {

        public void run() {
            long timeInMillies = SystemClock.uptimeMillis()- startTime;
            long finalTime = timeInMillies;

            int seconds = (int) (finalTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int milliseconds = (int) (finalTime % 1000);
            timer.setText("" + minutes + ":"
            + String.format("%02d", seconds) + ":"
            + String.format("%03d", milliseconds));
            timerHandler.postDelayed(this, 0);
        }


    };

}