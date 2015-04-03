package com.example.user.cucomposer_android;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Wongse on 16/3/2558.
 */
public class Listen extends Activity {


    private static String noSongExistText = "There is no song in your device";
    ArrayList<String> songList = new ArrayList<String>();

    MediaPlayer mediaPlayer = new MediaPlayer();

    String selectedFileName = null;

    Handler handler = new Handler();

    private Button playButton;

    private SeekBar seekBar;

    private boolean isUpdateSeekbar = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);

        playButton = (Button) findViewById(R.id.playButton);
        TextView backButton = (TextView) findViewById(R.id.backButton);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUpdateSeekbar = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateSongPosition();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong(selectedFileName);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final ListView listView = (ListView) findViewById(R.id.list);

        File folder = new File(Config.fullSongFolder);

        if(!folder.exists()){
            Toast.makeText(getApplicationContext(),"Song folder not found",Toast.LENGTH_LONG);
        }
        else {

            for (File file : folder.listFiles()) {
                if (file.getName().endsWith(".mid")) {
                    songList.add(file.getName());
                }
            }
        }

        if(songList.size() == 0){
            songList.add(noSongExistText);
        }

        //add song list in songList
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
                    //playSong(itemValue);
                    selectedFileName = itemValue;
                }
            }
        });
    }

    private void playSong(String fileName){
        if(fileName==null){
            return;
        }
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            return;
        }

        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(Config.fullSongFolder+"/"+fileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
            playButton.setText("Stop");
            seekBar.setProgress(0);
            handler.postDelayed(checkIsPlaying,100);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Runnable checkIsPlaying = new Runnable() {
        @Override
        public void run() {
            if(mediaPlayer.isPlaying()){
                handler.postDelayed(this,100);
                if(isUpdateSeekbar)
                    seekBar.setProgress(seekBar.getMax()*mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration());
            }
            else{
                playButton.setText("Play");
                handler.removeCallbacks(this);
            }
        }
    };

    private void updateSongPosition(){
        if(mediaPlayer.isPlaying()){
            int time = mediaPlayer.getDuration()*seekBar.getProgress()/seekBar.getMax();
            mediaPlayer.seekTo(time);
            isUpdateSeekbar = true;
        }
    }




}