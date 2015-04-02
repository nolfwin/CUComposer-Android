package com.example.user.cucomposer_android.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nuttapong on 1/21/2015.
 */
public class Note implements Parcelable {

    private int pitch;
    private float duration;
    private float offset;

    public Note(){
        this.pitch = -1;
    }

    public Note(int pitch,float duration){
        this.pitch = pitch;
        this.duration = duration;
    }

    public Note(Note note){
        this.pitch = note.getPitch();
        this.duration = note.getDuration();
        this.offset = note.getOffset();
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

    public float getOffset() {
        return offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    public  String toString(){
        return "("+pitch+","+duration+","+offset+")";

    }

    protected Note(Parcel in) {
        pitch = in.readInt();
        duration = in.readFloat();
        offset = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(pitch);
        dest.writeFloat(duration);
        dest.writeFloat(offset);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}