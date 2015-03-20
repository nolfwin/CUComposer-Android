package com.example.user.cucomposer_android.utility;

import com.example.user.cucomposer_android.entity.Note;

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
}
