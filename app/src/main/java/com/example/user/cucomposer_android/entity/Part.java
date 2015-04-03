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
    private List<Note> pianoNoteList;
    private List<Note> guitarNoteList;
    private List<Note> bassNoteList;

    private PartType partType;

    public enum PartType{

        INTRO("Intro","I", Color.argb(255, 255, 152, 0),
                "The \"introduction\" is an instrumental section at the beginning of your song. \n" +"There is at most one intro in your song."),
        VERSE("Verse","V", Color.argb(255, 255, 221, 0),
                "When two or more sections of the song have almost identical music and different lyrics, \neach section is considered one \"verse.\""),
        PRECRORUS("Pre-chorus","P",Color.argb(255, 164, 255, 22),
                "An optional section that may occur after the verse is the \"pre-chorus.\"  \nThe pre-chorus functions to connect the verse to the chorus."
        ),
        CHORUS("Chorus","C",Color.argb(255, 0, 231, 133),
                "The \"chorus\" is the element of the song that repeats at least once both musically and lyrically. \nIt contains the main idea, or big picture, of what is being expressed lyrically and musically in your song."
        ),
        BRIDGE("Bridge","B",Color.argb(255, 0, 226, 201),
                "The \"bridge\" is used to break up the repetitive pattern of the song and keep the listeners attention. \nThere is at most one bridge in your song."
        ),
        SOLO("Solo","S",Color.argb(255, 0, 181, 255),
                "A \"solo\" is a section designed to showcase an instrumentalist. \nThere is at most one solo in your song."
        ),
        BLANK("","",Color.argb(0,0,0,0),"");
        ;

        private int color;
        private String description;
        private String name;
        private String nickname;

        PartType(String name,String nickname, int color,String description){
            this.nickname = nickname;
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

        public String NICKNAME() {return nickname;}
    }


    public Part(List<Note> noteList, int bpm, int key,PartType partType) {
        this.noteList = noteList;
        this.bpm = bpm;
        this.key = key;
        this.partType=partType;
        pianoNoteList = null;
        guitarNoteList = null;
        bassNoteList = null;
    }

    public Part(Part part){
        this.bpm = part.getBpm();
        this.key = part.getKey();
        this.partType = part.getPartType();

        this.noteList = copyNoteList(part.getNoteList());
        this.pianoNoteList = copyNoteList(part.getPianoNoteList());
        this.guitarNoteList = copyNoteList(part.getGuitarNoteList());
        this.bassNoteList = copyNoteList(part.getBassNoteList());
    }

    public List<Note> copyNoteList(List<Note> list){
        if(list == null)
            return null;
        ArrayList<Note> retList = new ArrayList<Note>();
        for(Note note:list){
            retList.add(new Note(note));
        }
        return retList;
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

    public int getKeyPitch(){
        return key%12;
    };

    public boolean getKeyMode(){
        return key<12;
    }

    public void setKey(int keyPitch, boolean keyMode){
        this.key = keyPitch + ((keyMode)?0:12);
    }

    public int getBpm() {
        return bpm;
    }

    public List<Note> getNoteList() {
        return noteList;
    }


    public List<Note> getPianoNoteList() {
        return pianoNoteList;
    }

    public void setPianoNoteList(List<Note> pianoNoteList) {
        this.pianoNoteList = pianoNoteList;
    }

    public List<Note> getGuitarNoteList() {
        return guitarNoteList;
    }

    public void setGuitarNoteList(List<Note> guitarNoteList) {
        this.guitarNoteList = guitarNoteList;
    }

    public List<Note> getBassNoteList() {
        return bassNoteList;
    }

    public void setBassNoteList(List<Note> bassNoteList) {
        this.bassNoteList = bassNoteList;
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
        if (in.readByte() == 0x01) {
            pianoNoteList = new ArrayList<Note>();
            in.readList(pianoNoteList, Note.class.getClassLoader());
        } else {
            pianoNoteList = null;
        }
        if (in.readByte() == 0x01) {
            guitarNoteList = new ArrayList<Note>();
            in.readList(guitarNoteList, Note.class.getClassLoader());
        } else {
            guitarNoteList = null;
        }
        if (in.readByte() == 0x01) {
            bassNoteList = new ArrayList<Note>();
            in.readList(bassNoteList, Note.class.getClassLoader());
        } else {
            bassNoteList = null;
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
        if (pianoNoteList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(pianoNoteList);
        }
        if (guitarNoteList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(guitarNoteList);
        }
        if (bassNoteList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(bassNoteList);
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