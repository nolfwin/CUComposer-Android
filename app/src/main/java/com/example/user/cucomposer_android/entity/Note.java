package com.example.user.cucomposer_android.entity;

/**
 * Created by Nuttapong on 1/21/2015.
 */
public class Note {

    private int pitch;
    private float duration;

    public Note(int pitch,float duration){
        this.pitch = pitch;
        this.duration = duration;
    }

    public int getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }


}
