package com.example.user.cucomposer_android;

import android.os.Environment;
import android.util.Log;

import com.example.user.cucomposer_android.entity.Note;
import com.example.user.cucomposer_android.entity.Part;
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
import java.util.Random;

/**
 * Created by Nuttapong on 1/25/2015.
 */
public class MidiPlay {

    private int resolution = 96;
    private Part part;
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

    private MidiTrack tempoTrack = new MidiTrack();
    private MidiTrack noteTrack = new MidiTrack();
    private MidiTrack pianoTrack = new MidiTrack();
    private MidiTrack bassTrack = new MidiTrack();
    private MidiTrack guitarTrack = new MidiTrack();

    private String LOG_TAG = "MidiPlay";
    public MidiPlay(){
    }

    public MidiPlay(Part part){
        this.part = part;
    }

    public MidiPlay(Part part,int[] chordSequence, int key, boolean isMajor){
        this.part = part;
        for (int index = 0; index < chordSequence.length; index++)
        {
            chords.add(chordSequence[index]);
        }
        this.key=key;
        this.isMajor=isMajor;
        majorScale = new int[]{key, 2 + key, 4 + key, 5 + key, 7 + key, 9 + key, 11 + key};
        minorScale = new int[]{key, 2 + key, 3 + key, 5 + key, 7 + key, 8 + key, 10 + key};
    }

    public void setPart(Part part){
        this.part = part;
    }

    public String generateMidiFromPart(){
        ArrayList<MidiTrack> tracks = new ArrayList<MidiTrack>();
        if(part == null)
            return null;
        addTempoTrack(tracks);
        addMainMelodyTrack(tracks);
        addPianoTrack(tracks);
        addGuitarTrack(tracks);
        addBassTrack(tracks);

        MidiFile midiFile = new MidiFile(resolution,tracks);
        File file = new File(Config.appFolder, "/generate_"+part.getPartType().NAME()+".mid");
        try{
            midiFile.writeToFile(file);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        Log.d(LOG_TAG,file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    public void addGuitarTrack(List<MidiTrack> tracks){
        if(part.getGuitarNoteList() == null)
            return;
        MidiTrack guitarTrack = new MidiTrack();
        guitarTrack.insertEvent(new Controller(0, 0, guitarCh,7,80));
        guitarTrack.insertEvent(new ProgramChange(0,guitarCh, ProgramChange.MidiProgram.ACOUSTIC_GUITAR_NYLON.programNumber()));
        setTrack(part.getGuitarNoteList(),guitarTrack,guitarCh);
        tracks.add(guitarTrack);
    }

    public void addPianoTrack(List<MidiTrack> tracks){
        if(part.getPianoNoteList() == null)
            return;
        MidiTrack pianoTrack = new MidiTrack();
        pianoTrack.insertEvent(new Controller(0, 0, pianoCh,7,90));
        pianoTrack.insertEvent(new ProgramChange(0,pianoCh, ProgramChange.MidiProgram.ACOUSTIC_GRAND_PIANO.programNumber()));
        setTrack(part.getPianoNoteList(),pianoTrack,pianoCh);
        tracks.add(pianoTrack);
    }

    public void addBassTrack(List<MidiTrack> tracks){
        if(part.getBassNoteList() == null)
            return;
        MidiTrack bassTrack = new MidiTrack();
        bassTrack.insertEvent(new Controller(0, 0, bassCh,7, 127));
        bassTrack.insertEvent(new ProgramChange(0,bassCh, ProgramChange.MidiProgram.ELECTRIC_BASS_PICK.programNumber()));
        setTrack(part.getBassNoteList(),bassTrack,bassCh);
        tracks.add(bassTrack);
    }

    public void addMainMelodyTrack(List<MidiTrack> tracks){
        if(part.getNoteList() == null)
            return;
        MidiTrack mainMelodyTrack = new MidiTrack();
        mainMelodyTrack.insertEvent(new Controller(0, 0, noteCh, 7, 127));
        int programInstru = part.getPartType()== Part.PartType.SOLO? ProgramChange.MidiProgram.ELECTRIC_GUITAR_JAZZ.programNumber() :ProgramChange.MidiProgram.CLARINET.programNumber();

        mainMelodyTrack.insertEvent(new ProgramChange(0, noteCh,programInstru ));
        mainMelodyTrack.insertEvent(new Controller(
                0, 0, noteCh, 91, 100));
        mainMelodyTrack.insertEvent(new Controller(
                0, 0, noteCh, 93, 100));
        setTrack(part.getNoteList(),mainMelodyTrack,noteCh);
        tracks.add(mainMelodyTrack);
    }

    public void addTempoTrack(List<MidiTrack> tracks){
        TimeSignature ts = new TimeSignature();
        ts.setTimeSignature(4,4,TimeSignature.DEFAULT_METER,TimeSignature.DEFAULT_DIVISION);
        Tempo tempo = new Tempo();
        tempo.setBpm(part.getBpm());
        MidiTrack tempoTrack = new MidiTrack();
        tempoTrack.insertEvent(ts);
        tempoTrack.insertEvent(tempo);
        tracks.add(tempoTrack);
    }

    public String generateMidiFromNotes(List<Note> notes,int bpm){

        MidiTrack midiTrack = new MidiTrack();
        midiTrack.insertEvent(new ProgramChange(0,noteCh,ProgramChange.MidiProgram.CLARINET.programNumber()));
        setTrack(notes,midiTrack,noteCh);

        TimeSignature ts = new TimeSignature();
        ts.setTimeSignature(4,4,TimeSignature.DEFAULT_METER,TimeSignature.DEFAULT_DIVISION);
        Tempo tempo = new Tempo();
        tempo.setBpm(bpm);
        MidiTrack tempoTrack = new MidiTrack();
        tempoTrack.insertEvent(ts);
        tempoTrack.insertEvent(tempo);

        ArrayList<MidiTrack> tracks = new ArrayList<MidiTrack>();
        tracks.add(tempoTrack);
        tracks.add(midiTrack);

        MidiFile midiFile = new MidiFile(resolution,tracks);
        File file = new File(Config.appFolder, "/generate.mid");

        try{
            midiFile.writeToFile(file);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        Log.d(LOG_TAG,file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    public String generateMidi(){
        pianoTrack.insertEvent(new Controller(0, 0, pianoCh,7,90));
        guitarTrack.insertEvent(new Controller(0, 0, guitarCh,7,80));
        bassTrack.insertEvent(new Controller(0, 0, bassCh,7, 127));
        noteTrack.insertEvent(new Controller(0, 0, noteCh,7, 127));
        noteTrack.insertEvent(new ProgramChange(0,noteCh, ProgramChange.MidiProgram.CLARINET.programNumber()));
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

        setTrack(part.getNoteList(),noteTrack,noteCh);
        setTrackForPiano(6);
        setTrackForGuitar(0);
        setTrackForBass(2);

        ArrayList<MidiTrack> tracks = new ArrayList<MidiTrack>();
        tracks.add(tempoTrack);
        tracks.add(noteTrack);
        tracks.add(pianoTrack);
        tracks.add(guitarTrack);
        tracks.add(bassTrack);

        MidiFile midiFile = new MidiFile(resolution,tracks);
//        File file = new File(Environment.getExternalStorageDirectory(), "test.mid");
        File file = new File(Environment.getExternalStorageDirectory(), "/test"+MainActivity.runningId+".mid");

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
        for(int i=0;i<notes.size();i++){
            Note aNote = notes.get(i);
            if(aNote.getPitch()>=0) {
                int pitch = aNote.getPitch()+12;
                int velocity = 100;
                long tick = (long)(aNote.getOffset()*resolution);
                long duration = (long)(aNote.getDuration()*resolution);

                NoteOn on = new NoteOn(tick, noteCh, pitch, velocity);
                NoteOff off = new NoteOff(tick + duration, noteCh, pitch, 0);

                noteTrack.insertEvent(on);
                noteTrack.insertEvent(off);

                //long tick = (long)(calculateTime(bpm, offset) * resolution / 1000);
                //long duration = (long)calculateTime(bpm, aNote.getDuration());
                noteTrack.insertNote(noteCh, pitch, velocity, tick, duration);
            }
        }
    }

    public void setTrack(List<Note> notes, MidiTrack midiTrack, int ch){
        for(int i=0;i<notes.size();i++){
            Note aNote = notes.get(i);
            if(aNote.getPitch()>=0) {
                int pitch = aNote.getPitch()+12;
                int velocity = 100;
                long tick = (long)(aNote.getOffset()*resolution);
                long duration = (long)(aNote.getDuration()*resolution);

                NoteOn on = new NoteOn(tick, ch, pitch, velocity);
                NoteOff off = new NoteOff(tick + duration, ch, pitch, 0);

                midiTrack.insertEvent(on);
                midiTrack.insertEvent(off);

                midiTrack.insertNote(ch, pitch, velocity, tick, duration);
            }
        }

    }

    public void setTrackForBass(int accomStyle){
        int[] scale = isMajor ? majorScale : minorScale;
        float offset = 0;
        int octave = 3;
        for(int i = 0 ; i < chords.size();i++) {
            Random ran = new Random();
             accomStyle = ran.nextInt(8) + 0;

            octave = 2;
            int velocity = 100;
            int k = chords.get(i);
            int chordType = k/7;
            int sevenOffset=0;
            if(chordType==3) sevenOffset--;
            k%=7;

            if (accomStyle == 0) {
                int[] step = {0, 0,0,0};
                runstep(scale, step, offset, k, octave, sevenOffset, velocity, bassCh, bassTrack);
                offset+=4.0f;
            } else if (accomStyle == 1) {
                int[] step = {0, 2, 4, 2};
                if(chordType==2){
                    step[1]=4;
                    step[2]=6;
                    step[3]=4;
                }
                runstep(scale, step,offset, k,octave,sevenOffset, velocity,bassCh,bassTrack);
                offset+=4.0f;
            } else if (accomStyle == 2) {
                int[] step = {0, 2, 4, 2, 7, 4, 2, 4};
                if(chordType>1){
                    step[4]=6;
                }
                runstep(scale, step,offset, k,octave,sevenOffset, velocity,bassCh,bassTrack);
                offset+=4.0f;
            }
            else if (accomStyle==3){
                int[] step = {0, 4,0,4};
                runstep(scale, step,offset, k,octave,sevenOffset, velocity,bassCh,bassTrack);
                offset+=4.0f;
            }
            else if (accomStyle==4){
                int[] step = {0, 4,4,0};
                runstep(scale, step,offset, k,octave,sevenOffset, velocity,bassCh,bassTrack);
                offset+=4.0f;
            }
            else if (accomStyle==4){
                int[] step = {0, 4,7,0};
                runstep(scale, step,offset, k,octave,sevenOffset, velocity,bassCh,bassTrack);
                offset+=4.0f;
            }
            else if (accomStyle==5){
                int[] step = {0, 4,7,0};
                runstep(scale, step,offset, k,octave,sevenOffset, velocity,bassCh,bassTrack);
                offset+=4.0f;
            }
            else if (accomStyle==6){
                int[] step = {0, 0,0,0,0,0,0,0};
                runstep(scale, step,offset, k,octave,sevenOffset, velocity,bassCh,bassTrack);
                offset+=4.0f;
            }
            else if (accomStyle==7){
                int[] step = {0, 7,7,0};
                runstep(scale, step,offset, k,octave,sevenOffset, velocity,bassCh,bassTrack);
                offset+=4.0f;
            }
            else if (accomStyle==8){
                int[] step = {0, 0,1,1,2,2,1,1};
                runstep(scale, step,offset, k,octave,sevenOffset, velocity,bassCh,bassTrack);
                offset+=4.0f;
            }
        }
     }
public void runstep(int[] scale, int[] step, float offset,int k,int octave,int sevenOffset,int velocity,int ch,MidiTrack track){
    for (int j = 0; j < step.length; j++) {
        long duration = (long) (4.0f/step.length * resolution);
        long tick = (long) (offset * resolution);
        int note = scale[(k + step[j]) % 7] + 12 * (octave + (k + step[j]) / 7) ;
        if(step[j]==6)note+=sevenOffset;
        NoteOn on = new NoteOn(tick, ch,note , velocity);
        NoteOff off = new NoteOff(tick + duration, ch,note, 0);

        track.insertEvent(on);
        track.insertEvent(off);
        offset += 4.0f/step.length;
    }
}

    public void setTrackForPiano(int accomStyle){
        int[] scale = isMajor ? majorScale : minorScale;
        float offset = 0;

        for(int i = 0 ; i < chords.size();i++){
            int octave = 5;
            int velocity = 100;
            Random ran = new Random();
            accomStyle = ran.nextInt(8) + 0;

            int k = chords.get(i);
            int chordType = k/7;
            int sevenOffset=0;
            if(chordType==3) sevenOffset--;
            k%=7;
            if(accomStyle == 0 ) {
                int[] step = {0,2,4,2};
                if(chordType>1){
                    step[1]=4;
                    step[2]=6;
                    step[3]=4;
                }
                runstep(scale, step,offset, k,octave,sevenOffset, velocity,pianoCh,pianoTrack);
                offset+=4.0f;
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
                    NoteOn on3 = new NoteOn(tick, pianoCh, scale[(k + 6) % 7]+sevenOffset + 12 * octave, velocity);
                    NoteOff off3 = new NoteOff(tick + duration, pianoCh, scale[(k + 6) % 7]+sevenOffset, 0);

                    pianoTrack.insertEvent(on);
                    pianoTrack.insertEvent(on1);
                    pianoTrack.insertEvent(on2);
                    if(chordType>1){
                        pianoTrack.insertEvent(on3);
                    }
                    pianoTrack.insertEvent(off);
                    pianoTrack.insertEvent(off1);
                    pianoTrack.insertEvent(off2);
                    if(chordType>1){
                        pianoTrack.insertEvent(off3);
                    }
                    offset += 1.0f;
                }
            }
            else if (accomStyle==2) {
               int[] step = {0,2,4,2,7,4,2,4};
//            scale[(k+step[j])%7]+12*octave
                if(chordType>1){
                    step[4]=6;
                }
                runstep(scale, step,offset, k,octave,sevenOffset, velocity,pianoCh,pianoTrack);
                offset+=4.0f;
            }
            else if (accomStyle ==3){
                int[] step = {0, 2,4,2,0,2,4,2};
                runstep(scale, step,offset, k,octave,sevenOffset, velocity,pianoCh,pianoTrack);
                offset+=4.0f;
            }
            else if (accomStyle ==4){
                int[] step = {0, 4,7,8,9,8,7,4};
                runstep(scale, step,offset, k,octave,sevenOffset, velocity,pianoCh,pianoTrack);
                offset+=4.0f;
            }
            else if (accomStyle ==5){
                int[] step = {0, 4,7,4,0,4,7,4};
                runstep(scale, step,offset, k,octave,sevenOffset, velocity,pianoCh,pianoTrack);
                offset+=4.0f;
            }
            else if (accomStyle==6) {
                for (int j = 0; j < 4; j++) {
                    if(j==1){
                        offset+=1.0f;
                        continue;
                    }
                    long duration = (long) (1.0f * resolution);
                    long tick = (long) (offset * resolution);
                    NoteOn on = new NoteOn(tick, pianoCh, scale[k] + 12 * octave, velocity);
                    NoteOff off = new NoteOff(tick + duration, pianoCh, scale[k], 0);
                    NoteOn on1 = new NoteOn(tick, pianoCh, scale[(k + 2) % 7] + 12 * octave, velocity);
                    NoteOff off1 = new NoteOff(tick + duration, pianoCh, scale[(k + 2) % 7], 0);
                    NoteOn on2 = new NoteOn(tick, pianoCh, scale[(k + 4) % 7] + 12 * octave, velocity);
                    NoteOff off2 = new NoteOff(tick + duration, pianoCh, scale[(k + 4) % 7], 0);
                    NoteOn on3 = new NoteOn(tick, pianoCh, scale[(k + 6) % 7]+sevenOffset + 12 * octave, velocity);
                    NoteOff off3 = new NoteOff(tick + duration, pianoCh, scale[(k + 6) % 7]+sevenOffset, 0);

                    pianoTrack.insertEvent(on);
                    pianoTrack.insertEvent(on1);
                    pianoTrack.insertEvent(on2);
                    if(chordType>1){
                        pianoTrack.insertEvent(on3);
                    }
                    pianoTrack.insertEvent(off);
                    pianoTrack.insertEvent(off1);
                    pianoTrack.insertEvent(off2);
                    if(chordType>1){
                        pianoTrack.insertEvent(off3);
                    }
                    offset += 1.0f;
                }
            }
            else if (accomStyle == 7){
                int[] step = {0, 4,7,4,0,4,7,4};
                runstep(scale, step,offset, k,octave,sevenOffset, velocity,pianoCh,pianoTrack);

                for(int z = 0 ; z < 2 ; z++) {
                    long duration = (long) (2.0f * resolution);
                    long tick = (long) (offset * resolution);
                    NoteOn on = new NoteOn(tick, pianoCh, scale[k] + 12 * octave, velocity);
                    NoteOff off = new NoteOff(tick + duration, pianoCh, scale[k], 0);
                    NoteOn on1 = new NoteOn(tick, pianoCh, scale[(k + 2) % 7] + 12 * octave, velocity);
                    NoteOff off1 = new NoteOff(tick + duration, pianoCh, scale[(k + 2) % 7], 0);
                    NoteOn on2 = new NoteOn(tick, pianoCh, scale[(k + 4) % 7] + 12 * octave, velocity);
                    NoteOff off2 = new NoteOff(tick + duration, pianoCh, scale[(k + 4) % 7], 0);
                    NoteOn on3 = new NoteOn(tick, pianoCh, scale[(k + 6) % 7] + sevenOffset + 12 * octave, velocity);
                    NoteOff off3 = new NoteOff(tick + duration, pianoCh, scale[(k + 6) % 7] + sevenOffset, 0);

                    pianoTrack.insertEvent(on);
                    pianoTrack.insertEvent(on1);
                    pianoTrack.insertEvent(on2);
                    if (chordType > 1) {
                        pianoTrack.insertEvent(on3);
                    }
                    pianoTrack.insertEvent(off);
                    pianoTrack.insertEvent(off1);
                    pianoTrack.insertEvent(off2);
                    if (chordType > 1) {
                        pianoTrack.insertEvent(off3);
                    }
                    offset += 2.0f;
                }
            }
            else if(accomStyle==8){
                for (int j = 0; j < 8; j++) {

                    long duration = (long) (0.5f * resolution);
                    long tick = (long) (offset * resolution);
                    NoteOn on = new NoteOn(tick, pianoCh, scale[k] + 12 * octave, velocity);
                    NoteOff off = new NoteOff(tick + duration, pianoCh, scale[k], 0);
                    NoteOn on1 = new NoteOn(tick, pianoCh, scale[(k + 2) % 7] + 12 * octave, velocity);
                    NoteOff off1 = new NoteOff(tick + duration, pianoCh, scale[(k + 2) % 7], 0);
                    NoteOn on2 = new NoteOn(tick, pianoCh, scale[(k + 4) % 7] + 12 * octave, velocity);
                    NoteOff off2 = new NoteOff(tick + duration, pianoCh, scale[(k + 4) % 7], 0);
                    NoteOn on3 = new NoteOn(tick, pianoCh, scale[(k + 6) % 7]+sevenOffset + 12 * octave, velocity);
                    NoteOff off3 = new NoteOff(tick + duration, pianoCh, scale[(k + 6) % 7]+sevenOffset, 0);

                    pianoTrack.insertEvent(on);
                    pianoTrack.insertEvent(on1);
                    pianoTrack.insertEvent(on2);
                    if(chordType>1){
                        pianoTrack.insertEvent(on3);
                    }
                    pianoTrack.insertEvent(off);
                    pianoTrack.insertEvent(off1);
                    pianoTrack.insertEvent(off2);
                    if(chordType>1){
                        pianoTrack.insertEvent(off3);
                    }
                    offset += 0.5f;
                }
            }
        }
    }
    public void setTrackForGuitar(int accomStyle){
        int[] scale = isMajor ? majorScale : minorScale;
        float offset = 0;

        for(int i = 0 ; i < chords.size();i++){
            int octave = 5;
            int velocity = 100;
            Random ran = new Random();
            accomStyle = ran.nextInt(8) + 0;

            int k = chords.get(i);
            int chordType = k/7;
            int sevenOffset=0;
            if(chordType==3) sevenOffset--;
            k%=7;
            if(accomStyle == 0 ) {
                int[] step = {0,2,4,2};
                if(chordType>1){
                    step[1]=4;
                    step[2]=6;
                    step[3]=4;
                }
                runstep(scale, step,offset, k,octave,sevenOffset, velocity,guitarCh,guitarTrack);
                offset+=4.0f;
            }
            else if (accomStyle==1) {
                for (int j = 0; j < 4; j++) {

                    long duration = (long) (1.0f * resolution);
                    long tick = (long) (offset * resolution);
                    NoteOn on = new NoteOn(tick, guitarCh, scale[k] + 12 * octave, velocity);
                    NoteOff off = new NoteOff(tick + duration, guitarCh, scale[k], 0);
                    NoteOn on1 = new NoteOn(tick, guitarCh, scale[(k + 2) % 7] + 12 * octave, velocity);
                    NoteOff off1 = new NoteOff(tick + duration, guitarCh, scale[(k + 2) % 7], 0);
                    NoteOn on2 = new NoteOn(tick, guitarCh, scale[(k + 4) % 7] + 12 * octave, velocity);
                    NoteOff off2 = new NoteOff(tick + duration, guitarCh, scale[(k + 4) % 7], 0);
                    NoteOn on3 = new NoteOn(tick, guitarCh, scale[(k + 6) % 7]+sevenOffset + 12 * octave, velocity);
                    NoteOff off3 = new NoteOff(tick + duration, guitarCh, scale[(k + 6) % 7]+sevenOffset, 0);

                    guitarTrack.insertEvent(on);
                    guitarTrack.insertEvent(on1);
                    guitarTrack.insertEvent(on2);
                    if(chordType>1){
                        guitarTrack.insertEvent(on3);
                    }
                    guitarTrack.insertEvent(off);
                    guitarTrack.insertEvent(off1);
                    guitarTrack.insertEvent(off2);
                    if(chordType>1){
                        guitarTrack.insertEvent(off3);
                    }
                    offset += 1.0f;
                }
            }
            else if (accomStyle==2) {
                int[] step = {0,2,4,2,7,4,2,4};
//            scale[(k+step[j])%7]+12*octave
                if(chordType>1){
                    step[4]=6;
                }
                runstep(scale, step,offset, k,octave,sevenOffset, velocity,guitarCh,guitarTrack);
                offset+=4.0f;
            }
            else if (accomStyle ==3){
                int[] step = {0, 2,4,2,0,2,4,2};
                runstep(scale, step,offset, k,octave,sevenOffset, velocity,guitarCh,guitarTrack);
                offset+=4.0f;
            }
            else if (accomStyle ==4){
                int[] step = {0, 4,7,8,9,8,7,4};
                runstep(scale, step,offset, k,octave,sevenOffset, velocity,guitarCh,guitarTrack);
                offset+=4.0f;
            }
            else if (accomStyle ==5){
                int[] step = {0, 4,7,4,0,4,7,4};
                runstep(scale, step,offset, k,octave,sevenOffset, velocity,guitarCh,guitarTrack);
                offset+=4.0f;
            }
            else if (accomStyle==6) {
                for (int j = 0; j < 4; j++) {
                    if(j==1){
                        offset+=1.0f;
                        continue;
                    }
                    long duration = (long) (1.0f * resolution);
                    long tick = (long) (offset * resolution);
                    NoteOn on = new NoteOn(tick, guitarCh, scale[k] + 12 * octave, velocity);
                    NoteOff off = new NoteOff(tick + duration, guitarCh, scale[k], 0);
                    NoteOn on1 = new NoteOn(tick, guitarCh, scale[(k + 2) % 7] + 12 * octave, velocity);
                    NoteOff off1 = new NoteOff(tick + duration, guitarCh, scale[(k + 2) % 7], 0);
                    NoteOn on2 = new NoteOn(tick, guitarCh, scale[(k + 4) % 7] + 12 * octave, velocity);
                    NoteOff off2 = new NoteOff(tick + duration, guitarCh, scale[(k + 4) % 7], 0);
                    NoteOn on3 = new NoteOn(tick, guitarCh, scale[(k + 6) % 7]+sevenOffset + 12 * octave, velocity);
                    NoteOff off3 = new NoteOff(tick + duration, guitarCh, scale[(k + 6) % 7]+sevenOffset, 0);

                    guitarTrack.insertEvent(on);
                    guitarTrack.insertEvent(on1);
                    guitarTrack.insertEvent(on2);
                    if(chordType>1){
                        guitarTrack.insertEvent(on3);
                    }
                    guitarTrack.insertEvent(off);
                    guitarTrack.insertEvent(off1);
                    guitarTrack.insertEvent(off2);
                    if(chordType>1){
                        guitarTrack.insertEvent(off3);
                    }
                    offset += 1.0f;
                }
            }
            else if (accomStyle == 7){
                int[] step = {0, 4,7,4,0,4,7,4};
                runstep(scale, step,offset, k,octave,sevenOffset, velocity,guitarCh,guitarTrack);

                for(int z = 0 ; z < 2 ; z++) {
                    long duration = (long) (2.0f * resolution);
                    long tick = (long) (offset * resolution);
                    NoteOn on = new NoteOn(tick, guitarCh, scale[k] + 12 * octave, velocity);
                    NoteOff off = new NoteOff(tick + duration, guitarCh, scale[k], 0);
                    NoteOn on1 = new NoteOn(tick, guitarCh, scale[(k + 2) % 7] + 12 * octave, velocity);
                    NoteOff off1 = new NoteOff(tick + duration, guitarCh, scale[(k + 2) % 7], 0);
                    NoteOn on2 = new NoteOn(tick, guitarCh, scale[(k + 4) % 7] + 12 * octave, velocity);
                    NoteOff off2 = new NoteOff(tick + duration, guitarCh, scale[(k + 4) % 7], 0);
                    NoteOn on3 = new NoteOn(tick, guitarCh, scale[(k + 6) % 7] + sevenOffset + 12 * octave, velocity);
                    NoteOff off3 = new NoteOff(tick + duration, guitarCh, scale[(k + 6) % 7] + sevenOffset, 0);

                    guitarTrack.insertEvent(on);
                    guitarTrack.insertEvent(on1);
                    guitarTrack.insertEvent(on2);
                    if (chordType > 1) {
                        guitarTrack.insertEvent(on3);
                    }
                    guitarTrack.insertEvent(off);
                    guitarTrack.insertEvent(off1);
                    guitarTrack.insertEvent(off2);
                    if (chordType > 1) {
                        guitarTrack.insertEvent(off3);
                    }
                    offset += 2.0f;
                }
            }
            else if(accomStyle==8){
                for (int j = 0; j < 8; j++) {

                    long duration = (long) (0.5f * resolution);
                    long tick = (long) (offset * resolution);
                    NoteOn on = new NoteOn(tick, guitarCh, scale[k] + 12 * octave, velocity);
                    NoteOff off = new NoteOff(tick + duration, guitarCh, scale[k], 0);
                    NoteOn on1 = new NoteOn(tick, guitarCh, scale[(k + 2) % 7] + 12 * octave, velocity);
                    NoteOff off1 = new NoteOff(tick + duration, guitarCh, scale[(k + 2) % 7], 0);
                    NoteOn on2 = new NoteOn(tick, guitarCh, scale[(k + 4) % 7] + 12 * octave, velocity);
                    NoteOff off2 = new NoteOff(tick + duration, guitarCh, scale[(k + 4) % 7], 0);
                    NoteOn on3 = new NoteOn(tick, guitarCh, scale[(k + 6) % 7]+sevenOffset + 12 * octave, velocity);
                    NoteOff off3 = new NoteOff(tick + duration, guitarCh, scale[(k + 6) % 7]+sevenOffset, 0);

                    guitarTrack.insertEvent(on);
                    guitarTrack.insertEvent(on1);
                    guitarTrack.insertEvent(on2);
                    if(chordType>1){
                        guitarTrack.insertEvent(on3);
                    }
                    guitarTrack.insertEvent(off);
                    guitarTrack.insertEvent(off1);
                    guitarTrack.insertEvent(off2);
                    if(chordType>1){
                        guitarTrack.insertEvent(off3);
                    }
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
