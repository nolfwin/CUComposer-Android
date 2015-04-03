package com.example.user.cucomposer_android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.cucomposer_android.entity.Note;
import com.example.user.cucomposer_android.entity.Part;

import java.io.File;
import java.util.List;

/**
 * Created by Wongse on 16/3/2558.
 */
public class Final extends Activity {

    private Part song;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        song = getIntent().getExtras().getParcelable("song");
        setContentView(R.layout.activity_final);

        TextView withoutVocalButton = (TextView) findViewById(R.id.withoutVocalButton);
        TextView withVocalButton = (TextView) findViewById(R.id.withVocalButton);
        TextView backButton = (TextView) findViewById(R.id.backButton);
        TextView finishButton = (TextView) findViewById(R.id.nextButton);

        withoutVocalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFile(false);
            }
        });

        withVocalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFile(true);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void saveFile(final boolean withVocal){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Save");
        alert.setMessage("Name of your song");
        final EditText input = new EditText(this);

        alert.setView(input);
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String songName = input.getText().toString().trim();
                MidiPlay midiPlay = new MidiPlay(song);
                String oldName = "";
                if(!withVocal){
                    List<Note> oldNoteList = song.getNoteList();
                    song.setNoteList(null);
                    oldName = midiPlay.generateMidiFromPart();
                    song.setNoteList(oldNoteList);
                }
                else{
                    oldName = midiPlay.generateMidiFromPart();
                }
                File from = new File(oldName);
                File to = new File(Config.fullSongFolder+"/"+songName+".mid");
                from.renameTo(to);

                Toast.makeText(getApplicationContext(), "Your song is saved", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
    }
}