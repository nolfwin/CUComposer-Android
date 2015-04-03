package com.example.user.cucomposer_android.utility;

import com.example.user.cucomposer_android.entity.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nuttapong on 3/20/2015.
 */
public class NotesUtil {
    public static void calculateOffset(List<Note> notes){
        float lastOffset = 0;
        for(int i=0;i<notes.size();i++){
            notes.get(i).setOffset(lastOffset);
            lastOffset += notes.get(i).getDuration();
        }
    }

    public static void offsetTransition(List<Note> notes, float transOffset){
        if(notes == null)
            return;
        for(Note note:notes){
            note.setOffset(note.getOffset() + transOffset);
        }
    }

    public static void addNotes(List<Note> notes1 ,List<Note> notes2){
        if(notes1==null || notes2 == null)
            return;
        for(Note note:notes2){
            notes1.add(new Note(note));
        }
    }

    public static List<Note> copyNotes(List<Note> notes2){
        ArrayList<Note> retNotes = new ArrayList<Note>();
        for(Note note:notes2){
            retNotes.add(new Note(note));
        }
        return retNotes;
    }
}
