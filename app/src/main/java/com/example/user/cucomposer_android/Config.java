package com.example.user.cucomposer_android;

import android.graphics.Color;
import android.os.Environment;

/**
 * Created by Nuttapong on 2/1/2015.
 */
public class Config {
    public final static String appFolder = Environment.getExternalStorageDirectory()+"/PopTime";


    public enum SectionDescription{

        Intro(Color.argb(255, 255, 152, 0),
                "The \"introduction\" is an instrumental section at the beginning of your song. \n" +"There is at most one intro in your song."),
        Verse(Color.argb(255, 255, 221, 0),
                "When two or more sections of the song have almost identical music and different lyrics, \neach section is considered one \"verse.\""),
        PreChorus(Color.argb(255, 164, 255, 22),
                "An optional section that may occur after the verse is the \"pre-chorus.\"  \nThe pre-chorus functions to connect the verse to the chorus."
                ),
        Chorus(Color.argb(255, 0, 231, 133),
                "The \"chorus\" is the element of the song that repeats at least once both musically and lyrically. \nIt contains the main idea, or big picture, of what is being expressed lyrically and musically in your song."
                ),
        Bridge(Color.argb(255, 0, 226, 201),
                "The \"bridge\" is used to break up the repetitive pattern of the song and keep the listeners attention. \nThere is at most one bridge in your song."
                ),
        Solo(Color.argb(255, 0, 181, 255),
                "A \"solo\" is a section designed to showcase an instrumentalist. \nThere is at most one solo in your song."
         );

        ;


        private int color;
        private String description;

        SectionDescription(int color,String description){
            this.color = color;
            this.description = description;
        }

        public int COLOR(){
            return color;
        }

        public String DESCRIPTION(){
            return description;
        }
    }

}
