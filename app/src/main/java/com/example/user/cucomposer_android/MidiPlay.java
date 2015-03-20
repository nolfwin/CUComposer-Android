package com.example.user.cucomposer_android;

import android.os.Environment;
import android.util.Log;

import com.example.user.cucomposer_android.entity.Note;
import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.ProgramChange;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.TimeSignature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nuttapong on 1/25/2015.
 */
public class MidiPlay {

    private int resolution = 96;
    private List<Note> notes;
    private int bpm=120;

    private String LOG_TAG = "MidiPlay";
    public MidiPlay(){
    }

    public MidiPlay(List<Note> notes){
        this.notes = notes;
    }

    public void setNotes(List<Note> notes){
        this.notes=notes;
    }

    public String generateMidi(){

        MidiTrack tempoTrack = new MidiTrack();
        MidiTrack noteTrack = new MidiTrack();
        MidiTrack drumTrack = new MidiTrack();

        int ch = 2;
        ProgramChange drumProg = new ProgramChange(0,ch, ProgramChange.MidiProgram.ACOUSTIC_GUITAR_STEEL.programNumber());
        drumTrack.insertEvent(drumProg);
        drumTrack.insertNote(ch,48,100,0,96);
        drumTrack.insertNote(ch,52,100,96,96);
        drumTrack.insertNote(ch,55,100,192,96);
        drumTrack.insertNote(ch,60,100,288,96);
        drumTrack.insertNote(ch,64,100,384,96);
        drumTrack.insertNote(ch,67,100,480,96);
//        {
//            float offset = 0;
//            for (int i = 0; i < 20; i++) {
//                int channel = 10;
//                int pitch = 60 + i;
//                int velocity = 100;
//                long tick = (long) (offset * resolution);
//                long duration = (long) (0.5 * resolution);
//                NoteOn on = new NoteOn(tick, channel, pitch, velocity);
//                NoteOff off = new NoteOff(tick + duration, channel, pitch, 0);
//                drumTrack.insertEvent(on);
//                drumTrack.insertEvent(off);
//                offset += 0.5;
//            }
//        }

        TimeSignature ts = new TimeSignature();
        ts.setTimeSignature(4,4,TimeSignature.DEFAULT_METER,TimeSignature.DEFAULT_DIVISION);



        Tempo tempo = new Tempo();
        tempo.setBpm(bpm);

        tempoTrack.insertEvent(ts);
        tempoTrack.insertEvent(tempo);

        float offset = 0;

        for(int i=0;i<notes.size();i++){
            Note aNote = notes.get(i);
            if(aNote.getPitch()>=0) {
                int channel = 0;
                int pitch = aNote.getPitch();
                int velocity = 100;
                long tick = (long)(offset*resolution);
                long duration = (long)(aNote.getDuration()*resolution);
                NoteOn on = new NoteOn(tick, channel, pitch, velocity);
                NoteOff off = new NoteOff(tick + duration, channel, pitch, 0);
                noteTrack.insertEvent(on);
                noteTrack.insertEvent(off);
                //long tick = (long)(calculateTime(bpm, offset) * resolution / 1000);
                //long duration = (long)calculateTime(bpm, aNote.getDuration());

                noteTrack.insertNote(channel, pitch, velocity, tick, duration);
            }
            offset += aNote.getDuration();
        }




        ProgramChange prog = new ProgramChange(10*resolution,0, ProgramChange.MidiProgram.FLUTE.programNumber());
        noteTrack.insertEvent(prog);

        ArrayList<MidiTrack> tracks = new ArrayList<MidiTrack>();
        tracks.add(tempoTrack);
        //tracks.add(noteTrack);
        tracks.add(drumTrack);

        MidiFile midiFile = new MidiFile(resolution,tracks);
        File file = new File(Environment.getExternalStorageDirectory(), "test.mid");
        try{
            midiFile.writeToFile(file);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        Log.d(LOG_TAG,file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public int getBpm() {

        return bpm;
    }

    public final static int calculateTick(float bpm,int ppq){
        return (int)(60000/(bpm*ppq));
    }

    public final static float calculateTime(float bpm,float time){
        return (60000/bpm*time);
    }

}
