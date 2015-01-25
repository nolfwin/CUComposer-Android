package com.example.user.cucomposer_android;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;


public class NoteEditor extends Activity {

    private String LOG_TAG = "NoteEditor";
    private Handler playTimeHandler = new Handler();
    private MediaPlayer mediaPlayer;
    private TextView playTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        final DrawNoteLine drawNoteLine= (DrawNoteLine)findViewById(R.id.drawNoteLine);
        Button prevButton = (Button)findViewById(R.id.prevButton);
        Button nextButton = (Button)findViewById(R.id.nextButton);
        playTimer = (TextView)findViewById(R.id.playTimer);
        final Button midiButton = (Button) findViewById(R.id.midiButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawNoteLine.incRoomOffset(-1);
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawNoteLine.incRoomOffset(1);
            }
        });
        midiButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MidiPlay midiPlay = new MidiPlay(drawNoteLine.getNotes());
                String filePath = midiPlay.generateMidi();
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(filePath);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    playTimeHandler.post(updatePlayTime);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void generateMidi(){
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Runnable updatePlayTime = new Runnable(){
        @Override
        public void run() {
            int nowPlayTime = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            playTimer.setText(" " + nowPlayTime + " / " + duration);
            if(nowPlayTime==duration){

            }
            if(mediaPlayer.isPlaying()){
                playTimeHandler.postDelayed(updatePlayTime,100);
            }
            else{
                playTimeHandler.removeCallbacks(updatePlayTime);
                playTimer.setText("End");
            }

        }
    };

}
