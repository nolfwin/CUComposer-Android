package com.example.user.cucomposer_android.entity;

import java.util.List;

/**
 * Created by Wongse on 23/2/2558.
 */
public class Part {
    private int key;
    private int bpm;
    private List<Note> noteList;

    public Part(List<Note> noteList, int bpm, int key) {
        this.noteList = noteList;
        this.bpm = bpm;
        this.key = key;
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

    public int getKey() {
        return key;
    }

    public int getBpm() {
        return bpm;
    }

    public List<Note> getNoteList() {
        return noteList;
    }
}
