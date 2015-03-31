package com.example.user.cucomposer_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.NoiseSuppressor;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.cucomposer_android.entity.Note;
import com.example.user.cucomposer_android.entity.Part;
import com.example.user.cucomposer_android.utility.Key;
import com.example.user.cucomposer_android.utility.NotesUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private static final String LOG_TAG = "debugger";
    Integer[] freqset = {11025, 16000, 22050, 44100};
    private ArrayAdapter<Integer> adapter;
    private AudioRecord audioRecord = null;
    private AudioTrack audioTrack = null;
    private long startTime = 0;
    private boolean startPlay = false;
    private int carryPlaybackOffset;
    private int bufferSize = 4096;
    private DataInputStream dataInputStream;
//    private int partSize = 4;
//    private LinearLayout[] partLayout = new LinearLayout[partSize];
//    private TextView[] partName = new TextView[partSize];
//    private Button[] recordButton = new Button[partSize];
//    private Button[] playButton = new Button[partSize];
//    private Button[] pitchButton = new Button[partSize];
//    private Button[] modifyButton = new Button[partSize];
    private Button playButton;
    private Button recordButton;
    private TextView actionText;


    private boolean isFoundAudioRecord = false;
    public static int runningId = -1;
    private int selectedSectionId = -1;

    public static int sampleRate;
    public static int audioFormat;
    public static int channelConfig;

    private Handler timerHandler = new Handler();

    Button nextButton;
    Button mergeButton;

    private static Context appContext;

    TextView timer;

    Boolean recording;
    Boolean playing;

    private Part.PartType[] section = {
            Part.PartType.VERSE,
            Part.PartType.PRECRORUS,
            Part.PartType.CHORUS,
            Part.PartType.BRIDGE
    };

    private int[] sectionId = {
            R.id.partVerse,
            R.id.partPrechorus,
            R.id.partChorus,
            R.id.partBridge
    };

    private int findSectionId(int id){
        for(int i=0;i<sectionId.length;i++){
            if(id == sectionId[i])
                return i;
        }
        return -1;
    }






    /**
     * Called when the activity is first created.
     */
    @Override

    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG,"on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appContext = getApplicationContext();

        File appFolder = new File(Config.appFolder);
        if (!appFolder.exists()) {
            if (!appFolder.mkdir()) {
                Log.d(LOG_TAG, "cant make app folder " + Config.appFolder);
            }

        }
        for(int id:sectionId){
            findViewById(id).setOnClickListener(segmentOnClickListener);
        }

        recordButton = (Button) findViewById(R.id.recordButton);
        playButton = (Button) findViewById(R.id.playButton);
        actionText = (TextView) findViewById(R.id.actionText);

        playButton.setOnClickListener(playBackOnClickListener);
        recordButton.setOnClickListener(startRecOnClickListener);

        timer = (TextView) findViewById(R.id.playTimer);

        findViewById(R.id.nextButton).setOnClickListener(nextOnClickListener);

        findViewById(R.id.direction).setOnClickListener(pitchOnClickListener);

        findViewById(R.id.backButton).setOnClickListener(mergeOnClickListener);



    }
    @Override
    protected void onResume(){
        super.onResume();
        runningId = -1;
        setSelectedSectionId(0);
        enableAllButton();
        recording = false;
        playing = false;

    }

    OnClickListener segmentOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(runningId>0)
                return;
            int id = findSectionId(v.getId());
            if (id >= 0) {
                setSelectedSectionId(id);
            }
        }
    };

    public void setSelectedSectionId(int id){
        selectedSectionId = id;
        TableRow emptyRow1 = (TableRow) findViewById(R.id.emptyRow1);
        emptyRow1.setBackgroundColor(section[id].COLOR());
        TextView partText = (TextView) findViewById(R.id.partText);
        partText.setBackgroundColor(section[id].COLOR());
        partText.setText(section[id].DESCRIPTION());
        timer.setText("");
        actionText.setText("");
    }

    OnClickListener startRecOnClickListener
            = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if(runningId>=0){
                recording = false;
                enableAllButton();
                ((TextView)findViewById(sectionId[selectedSectionId])).setText("["+section[selectedSectionId].NAME()+"]");
            }
            else {
                runningId = selectedSectionId;

                Thread recordThread = new Thread(recordRunnable);
                recordThread.start();
                Button b = (Button) v;
                b.setText("Stop");
                playButton.setEnabled(false);
                actionText.setText("Recording..");
                //disableOtherButton(runningId,true);
            }
        }
    };

    private Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {

            recording = true;
            if (!startRecord(Config.appFolder + "/test"+selectedSectionId+".pcm"))
                Log.d(LOG_TAG, "cant find audioRecord");
        }
    };

//    private void disableOtherButton(int id,boolean isRecord){
//        for(int i=0;i<partSize;i++){
//            if(!isRecord||id!=i)
//                recordButton[i].setEnabled(false);
//            if(isRecord||id!=i)
//                playButton[i].setEnabled(false);
//            if(isRecord||id!=i)
//                pitchButton[i].setEnabled(false);
//            if(isRecord||id!=i)
//                modifyButton[i].setEnabled(false);
//        }
//    }
//
//    private void enableAllButton(){
//        for(int i=0;i<partSize;i++){
//            recordButton[i].setEnabled(true);
//            playButton[i].setEnabled(true);
//            pitchButton[i].setEnabled(true);
//            modifyButton[i].setEnabled(true);
//        }
//    }

    private void enableAllButton(){
        recordButton.setText("Record");
        recordButton.setEnabled(true);
        playButton.setText("Play");
        playButton.setEnabled(true);
        actionText.setText(".");
    }

    private Runnable playRunnable = new Runnable() {
        @Override
        public void run() {
            playRecord(Config.appFolder+"/test"+selectedSectionId+".pcm");
        }
    };



    OnClickListener stopRecOnClickListener
            = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            recording = false;
        }
    };

    OnClickListener playBackOnClickListener
            = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(runningId<0) {
                runningId = selectedSectionId;
                Thread playThread = new Thread(playRunnable);
                playThread.start();
                recordButton.setEnabled(false);
                //disableOtherButton(runningId, false);
                Button b = (Button) v;
                b.setText("Stop");
                actionText.setText("Playing..");
            }
            else{
                playing = false;
                enableAllButton();
            }
            //playRecord2(Config.appFolder + "/test" + v.getId() + ".pcm");
        }

    };

    OnClickListener mergeOnClickListener =  new OnClickListener() {
        @Override
        public void onClick(View v) {

            runningId=5;
//            int[] chordSequence = {0,4,5,2,3,0,0,4,5,2,3,0};
           int[] chordSequencez = { 0, 4, 1, 5, 3, 3, 4, 4, 0, 4, 1, 5, 3, 3, 4, 0};
            ChordGenerator cg = new ChordGenerator();
            List<Note> noteList = new ArrayList<Note>();
            noteList.add(new Note(-1,32.0f));

            cg.setKey(9,Key.MAJOR);
            cg.setNotes(noteList);
            cg.setFixedLastChord(5);
            int[] chordSequence = cg.generateChords();
            Log.d(LOG_TAG,"CHORD LOGD = "+Arrays.toString(chordSequence));
            
            int offset = 9;
            int[] major = {offset,2+offset,4+offset,5+offset,7+offset,9+offset,11+offset};
            int[] minor = {offset,2+offset,3+offset,5+offset,7+offset,8+offset,10+offset};
            int[] scale = major;
            int octave = 5;
            int beforeChord = -1;
            int lastNote = -1;
            int round = 0;
            int bpm = 100;
            ArrayList<Note> testNoteList = new ArrayList<Note>();
            for(int i = 0 ; i < chordSequence.length ; i++){
                int k = chordSequence[i];
                for(int j = 0 ; j<4;j++ ) {
                    if(j==3 && i==chordSequence.length-1&&k==0){
                        int noteToPlay;
                        noteToPlay = scale[0]+octave*12;
                        testNoteList.add(new Note(noteToPlay,4.0f));
                        break;
                    }
                    if (Math.random() > 0.5) {
                        int noteToPlay;
                        double rand = Math.random();
                        if(rand>0.66){
                            noteToPlay = scale[((k)%7)]+octave*12;
                        }
                        else if(rand>0.33){
                            noteToPlay = scale[((k+2)%7)]+octave*12;
                        }
                        else{
                            noteToPlay = scale[((k+4)%7)]+octave*12;
                        }
                        boolean isPlus = lastNote>noteToPlay;
                        double randThres =0.75;
                        if(Math.abs(noteToPlay-lastNote)>6)randThres = 0.25;
                        if(Math.random()>randThres){
                            if(isPlus&&octave<6){
                                noteToPlay+=12;
                                octave++;
                            }
                            else if(octave>4) {
                                noteToPlay-=12;
                                octave--;
                            }
                        }
                        testNoteList.add(new Note(noteToPlay,1.0f));
                        lastNote = noteToPlay;
                    } else {
                        for(int times = 0 ; times < 2 ;times++) {
                            int noteToPlay;
                            if(Math.random()>0.3){
                                double rand = Math.random();
                                if (rand > 0.66) {
                                    noteToPlay = scale[((k)%7)]+octave*12;
                                } else if (rand > 0.33) {
                                    noteToPlay = scale[((k+2)%7)]+octave*12;
                                } else {
                                    noteToPlay = scale[((k+4)%7)]+octave*12;
                                }
                                boolean isPlus = lastNote>noteToPlay;
                                double randThres =0.75;
                                if(Math.abs(noteToPlay-lastNote)>6)randThres = 0.25;
                                if(Math.random()>randThres){
                                    if(isPlus&&octave<6){
                                        noteToPlay+=12;
                                        octave++;
                                    }
                                    else if(octave>4) {
                                        noteToPlay-=12;
                                        octave--;
                                    }
                                }
                                testNoteList.add(new Note(noteToPlay, 0.5f));
                                lastNote = noteToPlay;
                            }
                            else{
                                for(int sixteenthTimes = 0 ; sixteenthTimes < 2 ; sixteenthTimes++){
                                    double rand = Math.random();
                                    if (rand > 0.66) {
                                        noteToPlay = scale[((k)%7)]+octave*12;
                                    } else if (rand > 0.33) {
                                        noteToPlay = scale[((k+2)%7)]+octave*12;
                                    } else {
                                        noteToPlay = scale[((k+4)%7)]+octave*12;
                                    }
                                    boolean isPlus = lastNote>noteToPlay;
                                    double randThres =0.75;
                                    if(Math.abs(noteToPlay-lastNote)>6)randThres = 0.25;
                                    if(Math.random()>randThres){
                                        if(isPlus&&octave<6){
                                            noteToPlay+=12;
                                            octave++;
                                        }
                                        else if(octave>4) {
                                            noteToPlay-=12;
                                            octave--;
                                        }
                                    }
                                    testNoteList.add(new Note(noteToPlay, 0.25f));
                                    lastNote = noteToPlay;
                                }
                            }


                        }
                    }
                }
            }



            Log.d(LOG_TAG,"Size of testNote = "+testNoteList.size());
            for(int i = 0 ; i < testNoteList.size();i++){
                Log.d(LOG_TAG,(testNoteList.get(i)).getPitch()+"");
            }
            MidiPlay midiPlay = new MidiPlay(testNoteList,chordSequence,offset,true);

            midiPlay.setBpm(bpm);
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
                return;
            }

            String filePath = midiPlay.generateMidi();
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(filePath);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            runningId = -1;
//            for(int i = 0; i < partSize ; i++){
//                runningId = i;
//                partArray[i]=null;
//                partArray[i] = getPartFromRunningID(runningId);
//                if(partArray[i]==null) Log.d("NULL","running ID = "+i+" is null");
//                else  Log.d("Part","get running ID = "+i+" part");
//                Log.d("Key",partArray[i].toString());
//            }
//            int baseKey = calculateMediumKey(partArray);
//            int meanBpm = calculateMeanBpm(partArray);
//            for(int i = 0 ; i < partArray.length;i++){
//                if(partArray[i]==null) continue;
//                partArray[i].setKey(baseKey);
//                partArray[i].setBpm(meanBpm);
//                Log.d("Key",partArray[i].toString());
//            }
//            Toast toast = Toast.makeText(getApplicationContext(),"Let's Merge",Toast.LENGTH_SHORT);
//            toast.show();
        }
    };

    OnClickListener nextOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(runningId<0)
                callNextActivity();
            else{
                if(playing) {
                    Toast.makeText(appContext, "Playing...", Toast.LENGTH_LONG).show();
                }
                if(recording)
                    Toast.makeText(appContext,"Recording...",Toast.LENGTH_LONG).show();
            }
        }
    };
    OnClickListener pitchOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
//pitchfucca
            runningId = selectedSectionId;
            Part part = getPartFromRunningID(runningId);
            ChordGenerator cg = new ChordGenerator();
            NotesUtil.calculateOffset(part.getNoteList());
            boolean isMajor = part.getKey()<=11 ? true: false;
            int key = isMajor? part.getKey():part.getKey()%12;
            BarDetector bd = new BarDetector(part.getNoteList(),key,isMajor);
            double barOffset = bd.barDetect();

            part.getNoteList().add(0,new Note(-1,4.0f-(float)barOffset));
            NotesUtil.calculateOffset(part.getNoteList());
            cg.setNotes(part.getNoteList());

            if(isMajor){
                cg.setKey(key, Key.MAJOR);
            }
            else{
                cg.setKey(key, Key.MINOR);
            }
            int[] chordPath= cg.generateChords();

            if(isMajor){
                Log.d(LOG_TAG,"KEY IS "+key+" MAJOR");
            }
            else{
                Log.d(LOG_TAG,"KEY IS "+key+" MINOR");
            }
            Log.d(LOG_TAG,"NOTES ARE"+Arrays.toString(part.getNoteList().toArray()));
            Log.d(LOG_TAG,"CHORDS ARE "+Arrays.toString(chordPath));

            MidiPlay midiPlay = new MidiPlay(part.getNoteList(),chordPath,key,isMajor);
            midiPlay.setBpm(part.getBpm());



            Log.d(LOG_TAG,"BAR OFFSET IS "+barOffset);
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
                return;
            }

            String filePath = midiPlay.generateMidi();
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(filePath);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            runningId=-1;
        }
    };
    OnClickListener modifyOnClickListener = new OnClickListener() {
        @Override
            public void onClick(View v) {
                runningId = selectedSectionId;
                Log.d("RUNNING ID","Running ID "+runningId);
                runningId= selectedSectionId;

            callNextActivity();
        }
    };
    public int calculateMeanBpm(Part[] partArray){
        int ans = 0;
        int count = 0;
        for(int i = 0 ; i < partArray.length;i++){
            if(partArray[i]==null)continue;
            ans+=partArray[i].getBpm();
            count++;
        }
        return ans/count;
    }
    public int calculateMediumKey(Part[] partArray){
        boolean[] isMajor = new boolean[partArray.length];
        int baseKey = -1;
        for(int i = 0 ; i< partArray.length;i++){
            if(partArray[i]==null) continue;
            if(partArray[i].getKey()>11)isMajor[i]=false;
            else isMajor[i]=true;
        }
        boolean isAllMajor=true;
        boolean isAllMinor=true;
        for(int i = 0; i < isMajor.length;i++){
            if(partArray[i]==null)continue;
            if(!isMajor[i]){
                isAllMajor=false;
            }
            else{
                isAllMinor=false;
            }
        }

        if(isAllMajor||isAllMinor){
            //now based on the mode value of the key in each part
            int[] keyArray = new int[partArray.length];
            for(int i = 0; i < partArray.length;i++){
                if(partArray[i]==null)keyArray[i]=-1;
                else keyArray[i]=partArray[i].getKey();
            }
            baseKey = getMode(keyArray);
            for(int i = 0; i < partArray.length;i++){
                if(partArray[i]==null)continue;
                transpose(partArray[i],baseKey-partArray[i].getKey());
            }

        }
        else{
            float[] mergeAudio = new float[0];

            for(int i = 0; i < partArray.length;i++){
                float[] audioFloats = getAudioFloatFromRunningID(i);
                if(audioFloats==null)continue;
                else mergeAudio = concat(mergeAudio,audioFloats);
            }
            List<Integer> segmentOutput = Segment.segment(mergeAudio);
            try {
                Part mergePart = Pitch.pitchEstWithoutSegment(mergeAudio,-1);
                baseKey = mergePart.getKey();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for(int i = 0; i < partArray.length;i++){
                float[] audioFloats = getAudioFloatFromRunningID(i);
                segmentOutput = Segment.segment(audioFloats);
                if(audioFloats==null)continue;
                try {
                    partArray[i] = Pitch.pitchEstWithoutSegment(audioFloats,baseKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return baseKey;
    }
    public static int getMode(int[] values) {
        HashMap<Integer,Integer> freqs = new HashMap<Integer,Integer>();
        for (int val : values) {
            if(val<0)continue;
            Integer freq = freqs.get(val);
            freqs.put(val, (freq == null ? 1 : freq+1));
        }

        int mode = 0;
        int maxFreq = 0;

        for (Map.Entry<Integer,Integer> entry : freqs.entrySet()) {
            int freq = entry.getValue();
            if (freq > maxFreq) {
                maxFreq = freq;
                mode = entry.getKey();
            }
        }

        return mode;
    }
    public float[] concat(float[] a, float[] b) {
        int aLen = a.length;
        int bLen = b.length;
        float[] c= new float[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
    public void transpose(Part part,int trans){
        List<Note> noteList = part.getNoteList();

        for(int i = 0 ; i < noteList.size();i++){
            Note note = noteList.get(i);
            note.setPitch(note.getPitch()+trans);
            noteList.set(i,note);
        }
        part.setNoteList(noteList);
    }
    public float[] getAudioFloatFromRunningID(int ID){
        runningId = ID;
        File file = new File(Config.appFolder + "/test"+runningId+".pcm");
        int shortSizeInBytes = Short.SIZE/Byte.SIZE;

        int bufferSizeInBytes = (int)(file.length()/shortSizeInBytes);
        short[] audioData = new short[bufferSizeInBytes];

        boolean done = true;
        if(audioRecord==null){
            audioRecord = findAudioRecord();
        }
        int i = 0;
        try {
            bufferSize = AudioTrack.getMinBufferSize(audioRecord.getSampleRate(),AudioFormat.CHANNEL_OUT_MONO,audioRecord.getAudioFormat());
            InputStream inputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            dataInputStream = new DataInputStream(bufferedInputStream);


            while(dataInputStream.available() > 0){
                if(i==audioData.length){
                    done = false;
                    break;
                }
                audioData[i] = dataInputStream.readShort();
                i++;
            }
            dataInputStream.close();
        } catch (FileNotFoundException e) {
            Log.d(LOG_TAG,"error file "+ID+" file not found");
            return  null;

        } catch (IOException e) {
            Log.d(LOG_TAG,"error file "+ID+" IOException");
            return null;
        }
        Log.d(LOG_TAG,"audioData size: "+bufferSizeInBytes+" "+audioData.length+" "+i);
        return floatMe(audioData);
    }
    public  Part getPartFromRunningID(int ID){
        runningId = ID;
        float[] audioFloats = getAudioFloatFromRunningID(ID);

        Part partWithSegment=null;
        Part partWithoutSegment=null;
        try {
       //  List<Integer> segmentOutput = Segment.segment(audioFloats);
           partWithoutSegment= Pitch.pitchEstWithoutSegment(audioFloats,-1);
     //    partWithSegment =  Pitch.pitchEst(audioFloats,segmentOutput,-1);

//         Log.d(LOG_TAG,Arrays.toString(segmentOutput.toArray()));
//         Log.d(LOG_TAG,audioFloats.length+"");
//         Log.d(LOG_TAG,"Number of syllables:"+segmentOutput.size()/2);
            // Pitch.pitchEst(floatMe(audioData));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return partWithoutSegment;
    }
    public static short[] shortMe(byte[] bytes) {
        short[] out = new short[bytes.length / 2]; // will drop last byte if odd number
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        for (int i = 0; i < out.length; i++) {
            out[i] = bb.getShort();
        }
        return out;
    }
    public static float[] floatMe(short[] pcms) {
        float[] floaters = new float[pcms.length];
        for (int i = 0; i < pcms.length; i++) {
            floaters[i] = pcms[i];
        }
        return floaters;
    }
    private void callNextActivity() {
        Part[] parts = setParts();
        Intent nextIntent = new Intent(this, SectionSetting.class);
        nextIntent.putExtra("parts",parts);
        startActivity(nextIntent);
    }

    public Part[] setParts(){
        Part[] parts = new Part[4];
        for(int i=0;i<4;i++) {
            Part part = getPartFromRunningID(i);
            ChordGenerator cg = new ChordGenerator();
            NotesUtil.calculateOffset(part.getNoteList());
            boolean isMajor = part.getKey() <= 11 ? true : false;
            int key = isMajor ? part.getKey() : part.getKey() % 12;
            BarDetector bd = new BarDetector(part.getNoteList(), key, isMajor);
            double barOffset = bd.barDetect();

            part.getNoteList().add(0, new Note(-1, 4.0f - (float) barOffset));
            NotesUtil.calculateOffset(part.getNoteList());
            part.setPartType(section[i]);
            parts[i] = part;
        }
        return parts;
    }


    public AudioRecord findAudioRecord() {
        int[] mSampleRates = new int[]{11025, 22050, 44100, 48000};
        //int[] mSampleRates = new int[] { 48000, 44100, 22050, 11025, 8000 };
        for (int rate : mSampleRates) {
            for (short audioFormat : new short[]{AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT}) {
                for (short channelConfig : new short[]{AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO}) {
                    try {
                        Log.d(LOG_TAG, "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
                                + channelConfig);
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, rate, channelConfig, audioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                                Log.d(LOG_TAG, "success");
                                MainActivity.sampleRate = recorder.getSampleRate();
                                MainActivity.audioFormat = recorder.getAudioFormat();
                                MainActivity.channelConfig = recorder.getChannelConfiguration();
                                isFoundAudioRecord = true;
                                return recorder;
                            } else {
                                Log.d(LOG_TAG, "uninitial recorder");
                            }
                        }
                    } catch (Exception e) {
                        Log.e(LOG_TAG, rate + "Exception, keep trying.", e);
                    }
                }
            }
        }
        return null;
    }


    private boolean startRecord(String fileName) {

        File file = new File(fileName);
        try {
            file.createNewFile();

            OutputStream outputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
            if (audioRecord == null) {
                if(isFoundAudioRecord){
                    audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,channelConfig,audioFormat,AudioRecord.getMinBufferSize(sampleRate,channelConfig,audioFormat));
                }
                else {
                    audioRecord = findAudioRecord();
                }
                if(NoiseSuppressor.isAvailable()) {
                    Log.d(LOG_TAG,"Noise suppressor is available");
                    NoiseSuppressor.create(audioRecord.getAudioSessionId());
                }
                else{
                    Log.d(LOG_TAG,"Noise suppressor is not available");
                }
            }
            int minBufferSize = AudioRecord.getMinBufferSize(audioRecord.getSampleRate(), audioRecord.getChannelConfiguration(), audioRecord.getAudioFormat());
            short[] audioData = new short[minBufferSize];

            if (audioRecord == null) {
                return false;
            }
            else{
                if (audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED){
                    audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,channelConfig,audioFormat,AudioRecord.getMinBufferSize(sampleRate,channelConfig,audioFormat));
                }
            }

            try {
                audioRecord.startRecording();
                startTime = SystemClock.uptimeMillis();
                timerHandler.postDelayed(updateTimerMethod, 300);


            } catch (Exception e) {

                e.printStackTrace();
                return false;
            }
            while (recording) {
                int numberOfShort= audioRecord.read(audioData, 0, minBufferSize);
                for (int i = 0; i < numberOfShort; i++) {
                    dataOutputStream.writeShort(audioData[i]);
                }
            }
            audioRecord.stop();
            audioRecord.release();
            dataOutputStream.close();
            runningId = -1;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private Runnable updateTimerMethod = new Runnable() {

        public void run() {
            if(runningId>=0) {
                long timeInMillies = SystemClock.uptimeMillis() - startTime;
                long finalTime = timeInMillies;

                int seconds = (int) (finalTime / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                int milliseconds = (int) (finalTime % 1000);
                timer.setText("" + minutes + ":"
                        + String.format("%02d", seconds) + ":"
                        + String.format("%03d", milliseconds));
                timerHandler.postDelayed(this, 100);
            }
            else{
                timerHandler.removeCallbacks(this);
                enableAllButton();
            }
        }


    };

    private Runnable updatePlayTimerMethod = new Runnable() {
        @Override
        public void run() {
            if(runningId>=0){
                long timeInMillies = SystemClock.uptimeMillis() - startTime;
                long finalTime = timeInMillies;

                int seconds = (int) (finalTime / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                int milliseconds = (int) (finalTime % 1000);
                timer.setText("" + minutes + ":"
                        + String.format("%02d", seconds) + ":"
                        + String.format("%03d", milliseconds));
                timerHandler.postDelayed(this, 100);

            }
            else{
                timerHandler.removeCallbacks(this);
                enableAllButton();
            }
        }
    };


    private void playRecord(String fileName){
        File file = new File(fileName);
        int bufferSizeInBytes =(int)file.length();
        Log.d(LOG_TAG,"audioData size: "+bufferSizeInBytes);
        short[] audioData = new short[bufferSize];
        boolean done = true;
        if(!isFoundAudioRecord){
            audioRecord = findAudioRecord();
            if(audioRecord!=null)
                audioRecord.release();
            else
                return;
        }

        try {

            int channelOut;
            if(channelConfig == AudioFormat.CHANNEL_IN_STEREO){
                channelOut = AudioFormat.CHANNEL_OUT_STEREO;
            }
            else{
                channelOut = AudioFormat.CHANNEL_OUT_MONO;
            }
            bufferSize = AudioTrack.getMinBufferSize(sampleRate,channelOut,audioFormat);
            InputStream inputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            dataInputStream = new DataInputStream(bufferedInputStream);

            int i = 0;
            while(dataInputStream.available() > 0){
                if(i==audioData.length){
                    done = false;
                    break;
                }
                audioData[i] = dataInputStream.readShort();
                i++;
            }

            audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    channelOut,
                    audioFormat,
                    bufferSize,
                    AudioTrack.MODE_STREAM);

            audioTrack.play();
            playing = true;
            startTime = SystemClock.uptimeMillis();
            timerHandler.post(updatePlayTimerMethod);
            while(!done&&playing) {
                done = true;
                i = 0;
                while (dataInputStream.available() > 0) {
                    if (i==audioData.length) {
                        done = false;
                        break;
                    }
                    audioData[i] = dataInputStream.readShort();
                    i++;
                }
                audioTrack.write(audioData,0,i);
            }
            dataInputStream.close();
            audioTrack.stop();
            audioTrack.release();
            runningId = -1;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<Note> generateIntro(int room, int key){
        return null;
    }

    public static Context getAppContext() {
        return appContext;
    }
}