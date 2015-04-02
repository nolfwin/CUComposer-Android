package com.example.user.cucomposer_android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Wongse on 16/3/2558.
 */
public class Instrument extends Activity implements View.OnTouchListener {
    private final int bgId[] = {
            R.drawable.bg1,
            R.drawable.bg2,
            R.drawable.bg3,
            R.drawable.bg4,
            R.drawable.bg5,
            R.drawable.bg6
    };
    private final int combiId[] = {
            R.id.combi1,
            R.id.combi2,
            R.id.combi3,
            R.id.combi4,
            R.id.combi5,
            R.id.combi6
    };
    private final int color[] = {
            Color.argb(255, 255, 152, 0),
            Color.argb(255, 0, 231, 133),
            Color.argb(255, 255, 221, 0),
            Color.argb(255, 0, 226, 201),
            Color.argb(255, 164, 255, 22),
            Color.argb(255, 0, 181, 255)
    };

    private int selectedCombi = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrument);

        for (int i = 0; i < combiId.length; i++) {
            TextView combi = (TextView) findViewById(combiId[i]);
            combi.setOnTouchListener(this);
        }

        TextView backButton = (TextView) findViewById(R.id.backButton);
        backButton.setOnTouchListener(this);

        TextView nextButton = (TextView) findViewById(R.id.nextButton);
        nextButton.setOnTouchListener(this);
    }

    public boolean onTouch(View view, MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                for (int i = 0; i < combiId.length; i++) {
                    if (view.getId() == combiId[i]) {
                        view.setBackground(getResources().getDrawable(bgId[i]));
                        selectedCombi = i;
                        highlightButton(combiId[i]);
                        for (int j = 0; j < combiId.length; j++) {
                            if (j != i) {
                                TextView combi = (TextView) findViewById(combiId[j]);
                                combi.setBackgroundColor(color[j]);
                            }
                        }
                    }
                }
                if (view.getId() == R.id.nextButton) {
                    if (selectedCombi != 99) {
                        next();
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please select an instrument combination.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                if(view.getId() == R.id.backButton){
                    back();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return true;
    }

    private void next() {
        Intent nextIntent = new Intent(this,SectionSetting.class);
        nextIntent.putExtra("parts", getIntent().getExtras().getParcelableArray("parts"));
        nextIntent.putExtra("instrument",selectedCombi);
        startActivity(nextIntent);
        //insert code here
    }

    private void back(){
        finish();
    }

    private void highlightButton(int selectedCombiId){
        for(int id:combiId){
            if(id == selectedCombiId){
                findViewById(id).setPressed(true);
                
            }
            else{
                findViewById(id).setPressed(false);
            }
        }
    }


}
