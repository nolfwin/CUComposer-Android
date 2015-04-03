package com.example.user.cucomposer_android;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Wongse on 16/3/2558.
 */
public class Final extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        TextView withoutVocalButton = (TextView) findViewById(R.id.withoutVocalButton);
        TextView withVocalButton = (TextView) findViewById(R.id.withVocalButton);
        TextView backButton = (TextView) findViewById(R.id.backButton);
        TextView finishButton = (TextView) findViewById(R.id.nextButton);

        withoutVocalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert code here
            }
        });

        withVocalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert code here
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
                DialogFragment dialog = new FinishDialog();
                dialog.show(getFragmentManager(), "tag");
            }
        });
    }
}