package com.example.user.cucomposer_android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Wongse on 16/3/2558.
 */
public class Listen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);

        Button playButton = (Button) findViewById(R.id.playButton);
        TextView backButton = (TextView) findViewById(R.id.backButton);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final ListView listView = (ListView) findViewById(R.id.list);
        //add song list in songList
        String[] songList = {"There is no song in your device", "test"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, songList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) listView.getItemAtPosition(position);
                if(!itemValue.equals("There is no song in your device")) {
                    for (int i = 0; i < adapter.getCount(); i++) {
                        listView.getChildAt(i).setBackgroundColor(Color.argb(0, 200, 200, 200));
                    }
                    view.setBackgroundColor(Color.argb(255, 200, 200, 200));
                    LinearLayout player = (LinearLayout) findViewById(R.id.player);
                    player.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}