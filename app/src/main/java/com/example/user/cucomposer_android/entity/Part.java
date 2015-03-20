package com.example.user.cucomposer_android.entity;
import com.example.user.cucomposer_android.Pitch;
import java.util.List;

/**
 * Created by Wongse on 23/2/2558.
 */
public class Part {
    private int key;
    private int bpm;
    private List<Note> noteList;
    private PartType partType;

    public enum PartType {
        VERSE, PRECHORUS, CHORUS, BRIDGE,
        INTRO, SOLO
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
