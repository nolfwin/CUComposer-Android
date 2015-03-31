package com.example.user.cucomposer_android.entity;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.user.cucomposer_android.Pitch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wongse on 23/2/2558.
 */
public class Part implements Parcelable {
    private int key;
    private int bpm;
    private List<Note> noteList;
    private PartType partType;

    public enum PartType{

        INTRO("Intro", Color.argb(255, 255, 152, 0),
                "The \"introduction\" is an instrumental section at the beginning of your song. \n" +"There is at most one intro in your song."),
        VERSE("Verse", Color.argb(255, 255, 221, 0),
                "When two or more sections of the song have almost identical music and different lyrics, \neach section is considered one \"verse.\""),
        PRECRORUS("PreChorus",Color.argb(255, 164, 255, 22),
                "An optional section that may occur after the verse is the \"pre-chorus.\"  \nThe pre-chorus functions to connect the verse to the chorus."
        ),
        CHORUS("Chorus",Color.argb(255, 0, 231, 133),
                "The \"chorus\" is the element of the song that repeats at least once both musically and lyrically. \nIt contains the main idea, or big picture, of what is being expressed lyrically and musically in your song."
        ),
        BRIDGE("Bridge",Color.argb(255, 0, 226, 201),
                "The \"bridge\" is used to break up the repetitive pattern of the song and keep the listeners attention. \nThere is at most one bridge in your song."
        ),
        SOLO("Solo",Color.argb(255, 0, 181, 255),
                "A \"solo\" is a section designed to showcase an instrumentalist. \nThere is at most one solo in your song."
        );

        ;

        private int color;
        private String description;
        private String name;

        PartType(String name, int color,String description){
            this.name = name;
            this.color = color;
            this.description = description;
        }

        public int COLOR(){
            return color;
        }

        public String DESCRIPTION(){
            return description;
        }

        public String NAME() {return name;}
    }


    public Part(List<Note> noteList, int bpm, int key,PartType partType) {
        this.noteList = noteList;
        this.bpm = bpm;
        this.key = key;
        this.partType=partType;
    }
    public String toString(){
        return "number of note = "+noteList.size() + "partType = "+partType +" bpm = "+bpm+" key = "+ Pitch.keyString[key];
    }
    public void setKey(int key) {
        this.key = key;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
    }

    public void setPartType(PartType type){
        this.partType = type;
    }
    public PartType getPartType(){
        return this.partType;
    }

    public int getKey() {
        return key;
    }

    public int getBpm() {
        return bpm;
    }

    public List<Note> getNoteList() {
        return noteList;
    }


    protected Part(Parcel in) {
        key = in.readInt();
        bpm = in.readInt();
        if (in.readByte() == 0x01) {
            noteList = new ArrayList<Note>();
            in.readList(noteList, Note.class.getClassLoader());
        } else {
            noteList = null;
        }
        partType = (PartType) in.readSerializable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(key);
        dest.writeInt(bpm);
        if (noteList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(noteList);
        }
        dest.writeSerializable(partType);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Part> CREATOR = new Parcelable.Creator<Part>() {
        @Override
        public Part createFromParcel(Parcel in) {
            return new Part(in);
        }

        @Override
        public Part[] newArray(int size) {
            return new Part[size];
        }
    };
}