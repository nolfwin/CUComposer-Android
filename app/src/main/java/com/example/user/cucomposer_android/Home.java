package com.example.user.cucomposer_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;

/**
 * Created by Wongse on 16/3/2558.
 */
public class Home extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button newButton = (Button) findViewById(R.id.newButton);
        Button listenButton = (Button) findViewById(R.id.listenButton);

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(nextIntent);
            }
        });

        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextIntent = new Intent(getApplicationContext(), Listen.class);

                startActivity(nextIntent);
            }
        });

        File songFolder = new File(Config.fullSongFolder);
        if(!songFolder.exists()){
            boolean success = songFolder.mkdir();
        }
    }
}