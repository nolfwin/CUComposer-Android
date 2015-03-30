package com.example.user.cucomposer_android;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Wongse on 16/3/2558.
 */
public class Record extends Activity implements View.OnTouchListener {
    private int selectedPart = 6;
    private int sectionList[] = {6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6};
    private final int id[] = {
            R.id.partIntro,
            R.id.partVerse,
            R.id.partPrechorus,
            R.id.partChorus,
            R.id.partBridge,
            R.id.partSolo
    };
    private final int color[] = {
            Color.argb(255, 255, 152, 0),
            Color.argb(255, 255, 221, 0),
            Color.argb(255, 164, 255, 22),
            Color.argb(255, 0, 231, 133),
            Color.argb(255, 0, 226, 201),
            Color.argb(255, 0, 181, 255),
            Color.argb(0, 0, 0, 0)
    };
    private final String text[] = {"I", "V", "P", "C", "B", "S", ""};
    private final String fullText[] = {
            "The \"introduction\" is an instrumental section at the beginning of your song. \nThere is at most one intro in your song.",
            "When two or more sections of the song have almost identical music and different lyrics, \neach section is considered one \"verse.\"",
            "An optional section that may occur after the verse is the \"pre-chorus.\"  \nThe pre-chorus functions to connect the verse to the chorus.",
            "The \"chorus\" is the element of the song that repeats at least once both musically and lyrically. \nIt contains the main idea, or big picture, of what is being expressed lyrically and musically in your song.",
            "The \"bridge\" is used to break up the repetitive pattern of the song and keep the listeners attention. \nThere is at most one bridge in your song.",
            "A \"solo\" is a section designed to showcase an instrumentalist. \nThere is at most one solo in your song."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        TextView recordButton = (TextView) findViewById(R.id.recordButton);
        TextView playButton = (TextView) findViewById(R.id.playButton);
        recordButton.setOnTouchListener(this);
        playButton.setOnTouchListener(this);

        for (int i = 0; i < id.length; i++) {
            TextView section = (TextView) findViewById(id[i]);
            section.setOnTouchListener(this);
        }
    }

    public boolean onTouch(View view, MotionEvent event) {
        TextView recordMessage = (TextView) findViewById(R.id.recordMessage);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < 6; i++) {
                    if (view.getId() == id[i]) {
                        TextView partText = (TextView) findViewById(R.id.partText);
                        partText.setBackgroundColor(color[i]);
                        partText.setText(fullText[i]);
                        if(i == 0 || i == 5) {
                            recordMessage.setText("This part is auto-generated, thus you cannot record your voice for it.");
                            hideButton();
                        }
                        else {
                            recordMessage.setText("This part is recorded.");
                            showButton();
                        }
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(selectedPart == 0 || selectedPart == 5) ;
                else {
                    if (view.getId() == R.id.recordButton) {
                        record();
                        break;
                    }
                    if (view.getId() == R.id.playButton) {
                        play();
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return true;
    }

    private void hideButton(){
        TextView recordButton = (TextView) findViewById(R.id.recordButton);
        TextView playButton = (TextView) findViewById(R.id.playButton);
        recordButton.setText("");
        playButton.setText("");
        recordButton.setBackgroundColor(Color.argb(0,0,0,0));
        playButton.setBackgroundColor(Color.argb(0,0,0,0));
    }

    private void showButton(){
        TextView recordButton = (TextView) findViewById(R.id.recordButton);
        TextView playButton = (TextView) findViewById(R.id.playButton);
        recordButton.setText("Record");
        playButton.setText("Play");
        recordButton.setBackgroundColor(Color.argb(255,221,221,221));
        playButton.setBackgroundColor(Color.argb(255,204,204,204));
    }

    private void play(){
        //insert code for play here
    }

    private void record(){
        //insert code for record here
    }
}
