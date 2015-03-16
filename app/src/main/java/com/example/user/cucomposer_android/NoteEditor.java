package com.example.user.cucomposer_android;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.cucomposer_android.entity.Note;
import com.example.user.cucomposer_android.entity.Part;
import com.example.user.cucomposer_android.utility.Key;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class NoteEditor extends Activity {

    private String LOG_TAG = "NoteEditor";
    private Handler playTimeHandler = new Handler();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private TextView playTimer;
    private DrawNoteLine drawNoteLine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        drawNoteLine= (DrawNoteLine)findViewById(R.id.drawNoteLine);
        List<Note> notes = new ArrayList<Note>();
        Part interestedPart = MainActivity.partArray[MainActivity.runningId];
        notes = interestedPart.getNoteList();
        if(notes.size()==0) {
            notes.add(new Note(69, 1));
            notes.add(new Note(76, 1.5f));
            notes.add(new Note(74, 0.5f));
            notes.add(new Note(74, 1));
            notes.add(new Note(72, 0.5f));
            notes.add(new Note(71, 1.5f));
            notes.add(new Note(72, 1));
            notes.add(new Note(71, 1));
            notes.add(new Note(67, 1.25f));
            notes.add(new Note(69, 1.75f));
            notes.add(new Note(72, 1));
            notes.add(new Note(-1, 1));
            notes.add(new Note(71, 2));
            notes.add(new Note(74, 1));
            for (int j = 0; j < 2; j++) {
                for (int i = 0; i < 7; i++) {
                    notes.add(new Note(12 * j + 60 + Key.mapBackToPitch(i, 0, Key.MAJOR), 0.5f));
                }
            }
        }

        drawNoteLine.setNotes(notes,0, Key.MAJOR);

        Button prevButton = (Button)findViewById(R.id.prevButton);
        Button nextButton = (Button)findViewById(R.id.nextButton);
        playTimer = (TextView)findViewById(R.id.playTimer);
        Button midiButton = (Button) findViewById(R.id.midiButton);
        Button chordGenButton = (Button) findViewById(R.id.chordGenButton);
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
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    return;
                }
                MidiPlay midiPlay = new MidiPlay(drawNoteLine.getNotes());
                String filePath = midiPlay.generateMidi();
                mediaPlayer.reset();
                try {

                    mediaPlayer.setDataSource(filePath);
                    mediaPlayer.prepare();
                    mediaPlayer.seekTo((int)(mediaPlayer.getDuration()*drawNoteLine.getPlayOffset()));
                    mediaPlayer.start();
                    drawNoteLine.setPlaying(true);
                    drawNoteLine.setSelectedNoteFromSelectedNotePlay();
                    playTimeHandler.post(updatePlayTime);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        chordGenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChordGenerator chordGen = new ChordGenerator();
         //       chordGen.setNotes(drawNoteLine.getNotes());
         //      chordGen.setKey(0,Key.MAJOR);
         //       chordGen.generateChords();
                Toast toast = Toast.makeText(getApplicationContext(),"print chord generator",Toast.LENGTH_SHORT);
                toast.show();
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
            if(mediaPlayer.isPlaying()){
                playTimeHandler.postDelayed(updatePlayTime,100);
                drawNoteLine.updatePlayingNote(1.0f*mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration());
            }
            else{
                playTimeHandler.removeCallbacks(updatePlayTime);
                playTimer.setText("End");
                drawNoteLine.setPlaying(false);
            }

        }
    };

}
