package com.example.user.cucomposer_android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.cucomposer_android.entity.Part;

import java.util.Arrays;

/**
 * Created by Wongse on 16/3/2558.
 */
public class SectionSetting extends Activity implements View.OnTouchListener {
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
            Color.argb(0, 0, 0, 0),
            Color.argb(255, 204, 204, 204),
            Color.argb(255, 187, 187, 187),
            Color.argb(255, 221, 221, 221)
    };
    private int[][] var = new int[5][3];
    private int currentPart = 99;
    private int tmp0 = 0;
    private int tmp1 = 0;
    private int tmp2 = 0;


    private Part[] parts = new Part[6];

    private final Part.PartType[] partTypes = {
            Part.PartType.INTRO,
            Part.PartType.VERSE,
            Part.PartType.PRECRORUS,
            Part.PartType.CHORUS,
            Part.PartType.BRIDGE,
            Part.PartType.SOLO,
            Part.PartType.BLANK
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();

        Parcelable[] partsParcel = bundle.getParcelableArray("parts");
        for(int i=0;i<partsParcel.length;i++){
            this.parts[i+1] = (Part)(partsParcel[i]);
            Log.i("", Arrays.toString((this.parts[i+1]).getNoteList().toArray()));
            Log.i("", parts[i+1].getPartType().NAME());
        }




        setContentView(R.layout.activity_section_setting);

        TextView editButton = (TextView) findViewById(R.id.editButton);
        TextView generateButton = (TextView) findViewById(R.id.generateButton);
        TextView playGenButton = (TextView) findViewById(R.id.playGenButton);
        TextView saveButton = (TextView) findViewById(R.id.saveButton);
        TextView playSaveButton = (TextView) findViewById(R.id.playSaveButton);
        SeekBar variation1 = (SeekBar) findViewById(R.id.variation1);
        SeekBar variation2 = (SeekBar) findViewById(R.id.variation2);
        SeekBar variation3 = (SeekBar) findViewById(R.id.variation3);
        TextView variationName1 = (TextView) findViewById(R.id.variationName1);
        TextView variationName2 = (TextView) findViewById(R.id.variationName2);
        TextView variationName3 = (TextView) findViewById(R.id.variationName3);

        editButton.setVisibility(View.GONE);
        generateButton.setVisibility(View.GONE);
        playGenButton.setVisibility(View.GONE);
        saveButton.setVisibility(View.GONE);
        playSaveButton.setVisibility(View.GONE);
        variationName1.setVisibility(View.GONE);
        variationName2.setVisibility(View.GONE);
        variationName3.setVisibility(View.GONE);

        variationName1.setBackgroundColor(color[6]);
        variationName2.setBackgroundColor(color[6]);
        variationName3.setBackgroundColor(color[6]);

        variation1.setVisibility(View.GONE);
        variation2.setVisibility(View.GONE);
        variation3.setVisibility(View.GONE);

        variationName1.setText("Originality");
        variationName2.setText("Complexity");
        variationName3.setText("Randomness");


        editButton.setOnTouchListener(this);
        generateButton.setOnTouchListener(this);
        playGenButton.setOnTouchListener(this);
        saveButton.setOnTouchListener(this);
        playSaveButton.setOnTouchListener(this);

        for (int i = 0; i < id.length; i++) {
            TextView part = (TextView) findViewById(id[i]);
            part.setText(partTypes[i].NAME());
            part.setOnTouchListener(this);
        }

        TextView backButton = (TextView) findViewById(R.id.backButton);
        backButton.setOnTouchListener(this);

        TextView nextButton = (TextView) findViewById(R.id.nextButton);
        nextButton.setOnTouchListener(this);
    }

    public boolean onTouch(View view, MotionEvent event) {
        SeekBar variation1 = (SeekBar) findViewById(R.id.variation1);
        SeekBar variation2 = (SeekBar) findViewById(R.id.variation2);
        SeekBar variation3 = (SeekBar) findViewById(R.id.variation3);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                for (int i = 0; i < 6; i++) {
                    if (view.getId() == id[i]) {
                        TableRow emptyRow1 = (TableRow) findViewById(R.id.emptyRow1);
                        TableRow emptyRow6 = (TableRow) findViewById(R.id.emptyRow6);
                        emptyRow1.setBackgroundColor(color[i]);
                        emptyRow6.setBackgroundColor(color[i]);
                        if (currentPart < 5) {
                            var[currentPart][0] = variation1.getProgress();
                            var[currentPart][1] = variation2.getProgress();
                            var[currentPart][2] = variation3.getProgress();
                        }
                        currentPart = i;
                        if (currentPart < 5) {
                            variation1.setProgress(var[currentPart][0]);
                            variation2.setProgress(var[currentPart][1]);
                            variation3.setProgress(var[currentPart][2]);
                        }
                        showPage();
                        if (i == 5) {
                            TextView editButton = (TextView) findViewById(R.id.editButton);
                            editButton.setVisibility(View.INVISIBLE);
                            variation1.setProgress(0);
                            variation2.setProgress(0);
                            variation3.setProgress(0);
                            variation1.setEnabled(false);
                            variation2.setEnabled(false);
                            variation3.setEnabled(false);


                            TextView variationName1 = (TextView) findViewById(R.id.variationName1);
                            TextView variationName2 = (TextView) findViewById(R.id.variationName2);
                            TextView variationName3 = (TextView) findViewById(R.id.variationName3);
                            variationName1.setVisibility(View.INVISIBLE);
                            variationName2.setVisibility(View.INVISIBLE);
                            variationName3.setVisibility(View.INVISIBLE);

                        }
                        else{
                            if(i==0){
                                TextView editButton = (TextView) findViewById(R.id.editButton);
                                editButton.setVisibility(View.INVISIBLE);
                            }
                        }
                        break;
                    }
                }
                if (view.getId() == R.id.editButton) {
                    edit(currentPart);
                    break;
                }
                if (view.getId() == R.id.generateButton) {
                    generate(currentPart);
                    break;
                }
                if (view.getId() == R.id.playGenButton) {
                    playGen(currentPart);
                    break;
                }
                if (view.getId() == R.id.saveButton) {
                    save(currentPart);
                    break;
                }
                if (view.getId() == R.id.playSaveButton) {
                    playSave(currentPart);
                    break;
                }
                if (view.getId() == R.id.backButton) {
                    back();
                    break;
                }
                if (view.getId() == R.id.nextButton) {
                    next();
                    break;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return true;
    }

    private void edit(int part) {
        //insert code here
        Intent noteEditIntent = new Intent(this, NoteEditor.class);
        noteEditIntent.putExtra("part",parts[part]);
        startActivity(noteEditIntent);
    }

    private void generate(int part) {
        ChordGenerator cg = new ChordGenerator();
        cg.setNotes(parts[part].getNoteList());
        cg.setKey(parts[part].getKeyPitch(),parts[part].getKeyMode());
        int[] chordPath = cg.generateChords();


        SeekBar variation1 = (SeekBar) findViewById(R.id.variation1);
        SeekBar variation2 = (SeekBar) findViewById(R.id.variation2);
        SeekBar variation3 = (SeekBar) findViewById(R.id.variation3);


        // insert accom code here
    }

    private void playGen(int part) {
        //insert code here
    }

    private void save(int part) {
        //insert code here
    }

    private void playSave(int part) {
        //insert code here
    }

    private void back() {
        finish();
    }

    private void next() {
        //insert code here
        int variation1 = ((SeekBar) findViewById(R.id.variation1)).getProgress();
        int variation2 = ((SeekBar) findViewById(R.id.variation2)).getProgress();
        int variation3 = ((SeekBar) findViewById(R.id.variation3)).getProgress();
        int var1 = (variation1 < 10) ? 0 : ((variation1 >= 85) ? 100 : (variation1 + 15));
        int var2 = (variation2 < 10) ? 0 : ((variation2 >= 85) ? 100 : (variation2 + 15));
        int var3 = (variation3 < 10) ? 0 : ((variation3 >= 85) ? 100 : (variation3 + 15));
        Toast toast = Toast.makeText(getApplicationContext(), String.valueOf(var1) + " " + String.valueOf(var2) + " " + String.valueOf(var3), Toast.LENGTH_SHORT);
        toast.show();
        Intent nextIntent = new Intent(this,SectionMerger.class);
        nextIntent.putExtra("parts",parts);
        startActivity(nextIntent);
    }

    private void showPage() {
        SeekBar variation1 = (SeekBar) findViewById(R.id.variation1);
        SeekBar variation2 = (SeekBar) findViewById(R.id.variation2);
        SeekBar variation3 = (SeekBar) findViewById(R.id.variation3);
        TextView variationName1 = (TextView) findViewById(R.id.variationName1);
        TextView variationName2 = (TextView) findViewById(R.id.variationName2);
        TextView variationName3 = (TextView) findViewById(R.id.variationName3);
        TextView editButton = (TextView) findViewById(R.id.editButton);
        TextView generateButton = (TextView) findViewById(R.id.generateButton);
        TextView playGenButton = (TextView) findViewById(R.id.playGenButton);
        TextView saveButton = (TextView) findViewById(R.id.saveButton);
        TextView playSaveButton = (TextView) findViewById(R.id.playSaveButton);

        editButton.setVisibility(View.VISIBLE);
        generateButton.setVisibility(View.VISIBLE);
        playGenButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.VISIBLE);
        playSaveButton.setVisibility(View.VISIBLE);
        variationName1.setVisibility(View.VISIBLE);
        variationName2.setVisibility(View.VISIBLE);
        variationName3.setVisibility(View.VISIBLE);
        variation1.setVisibility(View.VISIBLE);
        variation2.setVisibility(View.VISIBLE);
        variation3.setVisibility(View.VISIBLE);
        variation1.setEnabled(true);
        variation2.setEnabled(true);
        variation3.setEnabled(true);
    }
}
