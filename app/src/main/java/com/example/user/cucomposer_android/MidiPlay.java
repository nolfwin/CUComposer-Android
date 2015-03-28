package com.example.user.cucomposer_android;

import android.os.Environment;
import android.util.Log;

import com.example.user.cucomposer_android.entity.Note;
import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.Controller;
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
    private List<Integer> chords = new ArrayList<Integer>();
    private int key=0;
    private boolean isMajor;
    private int[] majorScale = {key,2+key,4+key,5+key,7+key,9+key,11+key};
    private int[] minorScale = {key,2+key,3+key,5+key,7+key,8+key,10+key};

    private int noteCh = 2;
    private int pianoCh= 3;
    private int guitarCh = 4;
    private int bassCh = 5;

    private  MidiTrack tempoTrack = new MidiTrack();
    private MidiTrack noteTrack = new MidiTrack();
    private MidiTrack pianoTrack = new MidiTrack();
    private MidiTrack bassTrack = new MidiTrack();
    private MidiTrack guitarTrack = new MidiTrack();

    private String LOG_TAG = "MidiPlay";
    public MidiPlay(){
    }

    public MidiPlay(List<Note> notes){
        this.notes = notes;
    }

    public MidiPlay(List<Note> notes,int[] chordSequence, int key, boolean isMajor){
        this.notes = notes;
        for (int index = 0; index < chordSequence.length; index++)
        {
            chords.add(chordSequence[index]);
        }
        this.key=key;
        this.isMajor=isMajor;
        majorScale = new int[]{key, 2 + key, 4 + key, 5 + key, 7 + key, 9 + key, 11 + key};
        minorScale = new int[]{key, 2 + key, 3 + key, 5 + key, 7 + key, 8 + key, 10 + key};
    }

    public void setNotes(List<Note> notes){
        this.notes=notes;
    }

    public String generateMidi(){
        pianoTrack.insertEvent(new Controller(0, 0, pianoCh,7,85));
        guitarTrack.insertEvent(new Controller(0, 0, guitarCh,7,90));
        bassTrack.insertEvent(new Controller(0, 0, bassCh,7, 127));
        noteTrack.insertEvent(new Controller(0, 0, noteCh,7, 100));
        noteTrack.insertEvent(new ProgramChange(0,noteCh, ProgramChange.MidiProgram.ALTO_SAX.programNumber()));
        noteTrack.insertEvent(new Controller(
                0, 0, noteCh, 91, 100));
        noteTrack.insertEvent(new Controller(
                0, 0, noteCh, 93, 100));
        pianoTrack.insertEvent(new ProgramChange(0,pianoCh, ProgramChange.MidiProgram.ACOUSTIC_GRAND_PIANO.programNumber()));
        guitarTrack.insertEvent(new ProgramChange(0,guitarCh, ProgramChange.MidiProgram.ACOUSTIC_GUITAR_NYLON.programNumber()));
        bassTrack.insertEvent(new ProgramChange(0,bassCh, ProgramChange.MidiProgram.ELECTRIC_BASS_PICK.programNumber()));

//        drumTrack.insertNote(ch,48,100,0,96);




        TimeSignature ts = new TimeSignature();
        ts.setTimeSignature(4,4,TimeSignature.DEFAULT_METER,TimeSignature.DEFAULT_DIVISION);



        Tempo tempo = new Tempo();
        tempo.setBpm(bpm);

        tempoTrack.insertEvent(ts);
        tempoTrack.insertEvent(tempo);

        setTrackForMainMelody(notes);
        setTrackForPiano(2);
        setTrackForGuitar(1);
        setTrackForBass(1);

        ArrayList<MidiTrack> tracks = new ArrayList<MidiTrack>();
        tracks.add(tempoTrack);
        tracks.add(noteTrack);
        tracks.add(pianoTrack);
        tracks.add(guitarTrack);
        tracks.add(bassTrack);

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
    public void setTrackForMainMelody(List<Note> notes){
        float offset = 0;
        for(int i=0;i<notes.size();i++){
            Note aNote = notes.get(i);
            if(aNote.getPitch()>=0) {
                int pitch = aNote.getPitch();
                int velocity = 100;
                long tick = (long)(offset*resolution);
                long duration = (long)(aNote.getDuration()*resolution);

                NoteOn on = new NoteOn(tick, noteCh, pitch, velocity);
                NoteOff off = new NoteOff(tick + duration, noteCh, pitch, 0);

                noteTrack.insertEvent(on);
                noteTrack.insertEvent(off);

                //long tick = (long)(calculateTime(bpm, offset) * resolution / 1000);
                //long duration = (long)calculateTime(bpm, aNote.getDuration());
                noteTrack.insertNote(noteCh, pitch, velocity, tick, duration);
            }
            offset += aNote.getDuration();
        }
    }
    public void setTrackForBass(int accomStyle){
        int[] scale = isMajor ? majorScale : minorScale;
        float offset = 0;
        int octave = 3;
        for(int i = 0 ; i < chords.size();i++) {
            octave = 2;
            int velocity = 100;

            int k = chords.get(i);
            k%=7;
            if (accomStyle == 0) {
                long duration = (long) (1.0f * resolution);
                long tick = (long) (offset * resolution);
                NoteOn on = new NoteOn(tick, bassCh, scale[k] + 12 * octave, velocity);
                NoteOff off = new NoteOff(tick + duration, bassCh, scale[k], 0);

                bassTrack.insertEvent(on);
                bassTrack.insertEvent(off);
                offset += 1.0f;
            } else if (accomStyle == 1) {
                int[] step = {0, 2, 4, 2};
                for (int j = 0; j < step.length; j++) {
                    long duration = (long) (1.0f * resolution);
                    long tick = (long) (offset * resolution);
                    NoteOn on = new NoteOn(tick, bassCh, scale[(k + step[j]) % 7] + 12 * (octave + (k + step[j]) / 7), velocity);
                    NoteOff off = new NoteOff(tick + duration, bassCh, scale[(k + step[j]) % 7] + 12 * (octave + (k + step[j]) / 7), 0);

                    bassTrack.insertEvent(on);
                    bassTrack.insertEvent(off);
                    offset += 1.0f;
                }
            } else if (accomStyle == 2) {
                int[] step = {0, 2, 4, 2, 7, 4, 2, 4};

                for (int j = 0; j < step.length; j++) {
                    long duration = (long) (0.5f * resolution);
                    long tick = (long) (offset * resolution);
                    NoteOn on = new NoteOn(tick, bassCh, scale[(k + step[j]) % 7] + 12 * (octave + (k + step[j]) / 7), velocity);
                    NoteOff off = new NoteOff(tick + duration, bassCh, scale[(k + step[j]) % 7] + 12 * (octave + (k + step[j]) / 7), 0);

                    bassTrack.insertEvent(on);
                    bassTrack.insertEvent(off);
                    offset += 0.5f;
                }
            }
        }
     }

    public void setTrackForPiano(int accomStyle){
        int[] scale = isMajor ? majorScale : minorScale;
        float offset = 0;

        for(int i = 0 ; i < chords.size();i++){
            int octave = 5;
            int velocity = 100;

            int k = chords.get(i);
            k%=7;
            if(accomStyle == 0 ) {
                int[] step = {0,2,4,2};
                for (int j = 0; j < step.length; j++) {
                    long duration = (long) (1.0f * resolution);
                    long tick = (long) (offset * resolution);
                    NoteOn on = new NoteOn(tick, pianoCh, scale[(k+step[j])%7]+12*(octave+(k+step[j])/7), velocity);
                    NoteOff off = new NoteOff(tick + duration, pianoCh, scale[(k+step[j])%7]+12*(octave+(k+step[j])/7), 0);

                    pianoTrack.insertEvent(on);
                    pianoTrack.insertEvent(off);
                    offset += 1.0f;
                }
            }
            else if (accomStyle==1) {
                for (int j = 0; j < 4; j++) {

                    long duration = (long) (1.0f * resolution);
                    long tick = (long) (offset * resolution);
                    NoteOn on = new NoteOn(tick, pianoCh, scale[k] + 12 * octave, velocity);
                    NoteOff off = new NoteOff(tick + duration, pianoCh, scale[k], 0);
                    NoteOn on1 = new NoteOn(tick, pianoCh, scale[(k + 2) % 7] + 12 * octave, velocity);
                    NoteOff off1 = new NoteOff(tick + duration, pianoCh, scale[(k + 2) % 7], 0);
                    NoteOn on2 = new NoteOn(tick, pianoCh, scale[(k + 4) % 7] + 12 * octave, velocity);
                    NoteOff off2 = new NoteOff(tick + duration, pianoCh, scale[(k + 4) % 7], 0);

                    pianoTrack.insertEvent(on);
                    pianoTrack.insertEvent(on1);
                    pianoTrack.insertEvent(on2);
                    pianoTrack.insertEvent(off);
                    pianoTrack.insertEvent(off1);
                    pianoTrack.insertEvent(off2);
                    offset += 1.0f;
                }
            }
            else if (accomStyle==2) {
               int[] step = {0,2,4,2,7,4,2,4};
//            scale[(k+step[j])%7]+12*octave

                for (int j = 0; j < step.length; j++) {

                    long duration = (long) (0.5f * resolution);
                    long tick = (long) (offset * resolution);
                    NoteOn on = new NoteOn(tick, pianoCh, scale[(k+step[j])%7]+12*(octave+(k+step[j])/7), velocity);
                    NoteOff off = new NoteOff(tick + duration, pianoCh, scale[(k+step[j])%7]+12*(octave+(k+step[j])/7), 0);

                    pianoTrack.insertEvent(on);
                    pianoTrack.insertEvent(off);
                    offset += 0.5f;
                }
            }
        }
    }
    public void setTrackForGuitar(int accomStyle){
        int[] scale = isMajor ? majorScale : minorScale;
        float offset = 0;
        int octave = 5;
        for(int i = 0 ; i < chords.size();i++){
            octave = 5;
            int velocity = 100;

            int k = chords.get(i);
            if(accomStyle == 0 ) {
                int[] step = {0,2,4,2};
                for (int j = 0; j < step.length; j++) {
                    long tick = (long) (offset * resolution);
                    long duration = (long) (1.0f * resolution);
                    NoteOn on = new NoteOn(tick, guitarCh, scale[(k+step[j])%7]+12*(octave+(k+step[j])/7), velocity);
                    NoteOff off = new NoteOff(tick + duration, guitarCh, scale[(k+step[j])%7]+12*(octave+(k+step[j])/7), 0);

                    guitarTrack.insertEvent(on);
                    guitarTrack.insertEvent(off);
                    offset += 1.0f;
                }
            }
            else if (accomStyle==1) {
                for (int j = 0; j < 4; j++) {
                    long tick = (long) (offset * resolution);
                    long duration = (long) (1.0f * resolution);
                    NoteOn on = new NoteOn(tick, guitarCh, scale[k] + 12 * octave, velocity);
                    NoteOff off = new NoteOff(tick + duration, guitarCh, scale[k], 0);
                    NoteOn on1 = new NoteOn(tick, guitarCh, scale[(k + 2) % 7] + 12 * octave, velocity);
                    NoteOff off1 = new NoteOff(tick + duration, guitarCh, scale[(k + 2) % 7], 0);
                    NoteOn on2 = new NoteOn(tick, guitarCh, scale[(k + 4) % 7] + 12 * octave, velocity);
                    NoteOff off2 = new NoteOff(tick + duration, guitarCh, scale[(k + 4) % 7], 0);

                    guitarTrack.insertEvent(on);
                    guitarTrack.insertEvent(on1);
                    guitarTrack.insertEvent(on2);
                    guitarTrack.insertEvent(off);
                    guitarTrack.insertEvent(off1);
                    guitarTrack.insertEvent(off2);
                    offset += 1.0f;
                }
            }
            else if (accomStyle==2) {
                int[] step = {0,2,4,2,7,4,2,4};
//            scale[(k+step[j])%7]+12*octave

                for (int j = 0; j < step.length; j++) {
                    long tick = (long) (offset * resolution);
                    long duration = (long) (0.5f * resolution);
                    NoteOn on = new NoteOn(tick, guitarCh, scale[(k+step[j])%7]+12*(octave+(k+step[j])/7), velocity);
                    NoteOff off = new NoteOff(tick + duration, guitarCh, scale[(k+step[j])%7]+12*(octave+(k+step[j])/7), 0);

                    guitarTrack.insertEvent(on);
                    guitarTrack.insertEvent(off);
                    offset += 0.5f;
                }
            }
        }
    }

    public final static int calculateTick(float bpm,int ppq){
        return (int)(60000/(bpm*ppq));
    }

    public final static float calculateTime(float bpm,float time){
        return (60000/bpm*time);
    }

}
