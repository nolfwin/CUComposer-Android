package com.example.user.cucomposer_android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.cucomposer_android.entity.Note;
import com.example.user.cucomposer_android.entity.Part;
import com.example.user.cucomposer_android.utility.NotesUtil;

import java.io.IOException;
import java.util.List;


public class NoteEditor extends Activity {

    private String LOG_TAG = "NoteEditor";
    private Handler playTimeHandler = new Handler();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private TextView playTimer;
    private DrawNoteLine drawNoteLine;
    private Part currentPart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        drawNoteLine= (DrawNoteLine)findViewById(R.id.drawNoteLine);
        Bundle bundle = getIntent().getExtras();
        currentPart = (Part) bundle.getParcelable("part");

        List<Note> notes = currentPart.getNoteList();

        //List<Note> notes = new ArrayList<Note>();
        //final Part interestedPart = null;//MainActivity.partArray[MainActivity.runningId];
        //notes = interestedPart.getNoteList();

//        if(notes.size()==0) {
//            notes.add(new Note(69, 1));
//            notes.add(new Note(76, 1.5f));
//            notes.add(new Note(74, 0.5f));
//            notes.add(new Note(74, 1));
//            notes.add(new Note(72, 0.5f));
//            notes.add(new Note(71, 1.5f));
//            notes.add(new Note(72, 1));
//            notes.add(new Note(71, 1));
//            notes.add(new Note(67, 1.25f));
//            notes.add(new Note(69, 1.75f));
//            notes.add(new Note(72, 1));
//            notes.add(new Note(-1, 1));
//            notes.add(new Note(71, 2));
//            notes.add(new Note(74, 1));
//            for (int j = 0; j < 2; j++) {
//                for (int i = 0; i < 7; i++) {
//                    notes.add(new Note(12 * j + 60 + Key.mapBackToPitch(i, 0, Key.MAJOR), 0.5f));
//                }
//            }
//        }

        NotesUtil.calculateOffset(notes);

        drawNoteLine.setNotes(notes,currentPart.getKeyPitch(), currentPart.getKeyMode());

        TextView prevButton = (TextView)findViewById(R.id.prevButton);
        TextView nextButton = (TextView)findViewById(R.id.nextButton);
        playTimer = (TextView)findViewById(R.id.playTimer);
        final TextView midiButton = (TextView) findViewById(R.id.midiButton);
        TextView saveButton = (TextView) findViewById(R.id.saveButton);
        TextView loadButton = (TextView) findViewById(R.id.loadButton);
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
                //MidiPlay midiPlay = new MidiPlay(drawNoteLine.getNotes());
                MidiPlay midiPlay = new MidiPlay();
                String filePath = midiPlay.generateMidiFromNotes(drawNoteLine.getNotes(),currentPart.getBpm());
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
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawNoteLine.setDefaultNotes();
                Toast.makeText(getApplicationContext(),"Save as default complete",Toast.LENGTH_SHORT).show();
            }
        });
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawNoteLine.loadDefaultNotes();
                Toast.makeText(getApplicationContext(),"Load default complete",Toast.LENGTH_SHORT).show();
            }
        });


        TextView backButton = (TextView) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyNotesChange();
            }
        });

        TextView top = (TextView) findViewById(R.id.topEdit);
        top.setText("Note Editor - " + currentPart.getPartType().NAME());
        prevButton.setText("<<<");
        nextButton.setText(">>>");
    }

    public void applyNotesChange(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Save Note");
        alert.setMessage("Which version of note do you want to save?");
        alert.setPositiveButton("Latest version", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                currentPart.setNoteList(drawNoteLine.getNotes());
                sendDataBack();
            }
        });

        alert.setNegativeButton("Current default version", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                drawNoteLine.loadDefaultNotes();
                currentPart.setNoteList(drawNoteLine.getNotes());
                sendDataBack();
            }
        });
        alert.show();
    }

    private void sendDataBack(){
        Intent output= new Intent();
        output.putExtra("part",currentPart);
        setResult(RESULT_OK,output);
        finish();
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
            //playTimer.setText(" " + nowPlayTime + " / " + duration);
            if(mediaPlayer.isPlaying()){
                playTimeHandler.postDelayed(updatePlayTime,100);
                drawNoteLine.updatePlayingNote(1.0f*mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration());
            }
            else{
                playTimeHandler.removeCallbacks(updatePlayTime);
                //playTimer.setText("End");
                drawNoteLine.setPlaying(false);
            }

        }
    };

}
