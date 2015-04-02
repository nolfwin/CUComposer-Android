package com.example.user.cucomposer_android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.user.cucomposer_android.entity.Note;
import com.example.user.cucomposer_android.utility.Key;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nuttapong on 1/21/2015.
 */
public class DrawNoteLine extends View {

    private static final int STATE_NORMAL = 0;
    private static final int STATE_FINGER_DRAG = 1;
    private static final int STATE_PLAYING = 2;

    public List<Note> getNotes() {
        List<Note> returnArray = new ArrayList<Note>();
        for(int i=0;i<notes.size();i++){
            Note aNote = notes.get(i);
            if(aNote.getPitch()<0){
                Note note = new Note(-1,notes.get(i).getDuration());
                note.setOffset(aNote.getOffset());
                returnArray.add(note);
            }
            else {
                int pitch = Key.mapBackToPitch(aNote.getPitch()+startPitch, keyPitch, keyMode)+((aNote.getPitch()+startPitch)/7*12);
                Note note = new Note(pitch, aNote.getDuration());
                note.setOffset(aNote.getOffset());
                returnArray.add(note);
            }
        }
        return returnArray;
    }

    public void setNotes(List<Note> notes,int keyPitch, boolean keyMode) {
        List<Note> noteArray = new ArrayList<Note>();
        int minPitch = 200;
        int maxPitch = -1;
        for(int i=0;i<notes.size();i++){
            Note aNote = notes.get(i);
            Note aNoteAdd = new Note();
            if(aNote.getPitch()<0){
                aNoteAdd.setPitch(aNote.getPitch());
                aNoteAdd.setOffset(aNote.getOffset());
                aNoteAdd.setDuration(aNote.getDuration());
            }
            else {
                int pitch = Key.mapToKey(aNote.getPitch(), keyPitch, keyMode)+(aNote.getPitch()/12*7);
                aNoteAdd.setPitch(pitch);
                aNoteAdd.setDuration(aNote.getDuration());
                aNoteAdd.setOffset(aNote.getOffset());
                if(pitch<minPitch){
                    minPitch = pitch;
                }
                if(pitch>maxPitch){
                   maxPitch = pitch;
                }
            }
            noteArray.add(aNoteAdd);
        }
        Log.d(LOG_TAG,"min pitch "+minPitch);
        Log.d(LOG_TAG,"max pitch "+maxPitch);
        startPitch = (maxPitch + minPitch)/2 - 10;
        if(startPitch<0){
            startPitch = 0;
        }
        for(int i=0;i<noteArray.size();i++){
            Note aNote = noteArray.get(i);
            if(aNote.getPitch()>=0){
                Log.d(LOG_TAG,"set note pitch "+(aNote.getPitch()-startPitch));
                aNote.setPitch(aNote.getPitch()-startPitch);
            }
        }
        this.notes = noteArray;
        this.keyPitch = keyPitch;
        this.keyMode = keyMode;
        calculateOffset();
    }

    private int startPitch;
    private int keyPitch;
    private boolean keyMode;

    private List<Note> notes = new ArrayList<Note>();



    private int roomLength = 3;
    private int lineLength = 11;

    private float heightStart = 0.2f;
    private float heightEnd = 0.95f;

    private float triangleDist = 10;
    private float triangleWidth = 40;
    private float triangleHeight = 20;

    private int touchNoteError = 25;
    private long longPressTime = 5;
    private int touchTriangleErrorX =20;
    private int touchTriangleErrorYUpper =100;
    private int touchTriangleErrorYLower = 10;

    private int transitionBackTime = 5;

    private int roomRectColor = Color.parseColor("#424242");
    private int needleBarColor = Color.parseColor("#bdbdbd");
    private int needleColor = Color.parseColor("#424242");
    private int roomTextColor = Color.WHITE;
    private float splitNeedle = 0.8f;
    private float needleWidth = 20;

    public float getPlayOffset() {
        return offset[selectedNotePlay]/offset[offset.length-1];
    }

    public void setPlaying(boolean isPlaying){
        if(isPlaying){
            state = STATE_PLAYING;
        }
        else{
            state = STATE_NORMAL;
        }
        postInvalidate();
    }

    // temp
    private float playOffset = 0;
    private int selectedNotePlay = 0;
    private float[] offset;
    private float[] noteLinesY;
    private float[] noteRegion;
    private float drawOffset = 0;
    private int selectedNote = -1;
    private int roomOffset = 0;
    private int maxRoomOffset = 0;
    private int startDrawNote = 0;
    private int startTouchNote = 0;
    private float roomSize = 0;
    private int state = STATE_NORMAL;
    private float radiusRect = 0;
    private long startTouchTime = 0;
    private float startTouchX = 0;
    private float startTouchY = 0;
    private final String LOG_TAG = "drawNote debug";
    private int transitionCount = 0;
    private float transitionBackOffset = 0;
    float upperBar;
    float roomBarY ;


    private Handler transitionBackHandler = new Handler();


    Paint paint = new Paint();
    //int[] noteColors = {Color.parseColor("#FF4444"),Color.parseColor("#FFBB33"),Color.parseColor("#FFEB3B"),Color.parseColor("#99CC00"),Color.parseColor("#259B24"),Color.parseColor("#33B5E5"),Color.parseColor("#AA66CC")};
    int[] noteColors = {Color.parseColor("#CC0000"),Color.parseColor("#FF8800"),Color.parseColor("#FFBB33"),Color.parseColor("#99CC00"),Color.parseColor("#669900"),Color.parseColor("#0099CC"),Color.parseColor("#9933CC")};

    public DrawNoteLine(Context context) {
        super(context);
        init();
    }

    public DrawNoteLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawNoteLine(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){

//        paint.setColor(Color.BLACK);
//        notes = new ArrayList<Note>();
//        notes.add(new Note(3,2.5f));
//        notes.add(new Note(4,2f));
//        notes.add(new Note(1, 1f));
//        notes.add(new Note(10, 0.25f));
//        for(int i=0;i<20;i++){
//            notes.add(new Note(i,0.25f));
//        }
//        notes.add(new Note(-1,2));
//        for(int i=0;i<20;i++){
//            notes.add(new Note(i,2));
//        }
//        calculateOffset();

    }

    @Override
    public void onDraw(Canvas canvas) {
        calculateLinesY(false,canvas.getHeight());
        float lineSize = noteLinesY[1]-noteLinesY[0];
        paint.setStrokeWidth(5);
        paint.setColor(roomRectColor);
        upperBar = noteLinesY[0]-lineSize*2;
        roomBarY = upperBar*splitNeedle;
        canvas.drawRect(0, 0, canvas.getWidth(), roomBarY, paint);
        paint.setColor(needleBarColor);
        canvas.drawRect(0,roomBarY,canvas.getWidth(),upperBar,paint);

        paint.setColor(Color.BLACK);
        for(int i=0;i<noteLinesY.length;i+=2) {
            canvas.drawLine(0, noteLinesY[i], canvas.getWidth(), noteLinesY[i], paint);
        }
        roomSize = canvas.getWidth()/(roomLength);
        int startDrawRoom = (roomOffset-2>=0)?-2:-roomOffset;
        int endDrawRoom = (roomLength+2+roomOffset<=maxRoomOffset)?roomLength+2:maxRoomOffset-roomOffset;
        paint.setTextSize(30);
        Rect textBound = new Rect();
        for(int i=startDrawRoom;i<endDrawRoom;i++){
            paint.setColor(Color.BLACK);
            canvas.drawLine(roomSize * (i + 1) - drawOffset, 0, roomSize * (i + 1) - drawOffset, noteLinesY[noteLinesY.length - 1], paint);
            paint.setColor(roomTextColor);
            String roomText = ""+(i+roomOffset+1);
            paint.getTextBounds(roomText,0,roomText.length(),textBound);
            canvas.drawText(roomText,roomSize*(i+0.5f)-textBound.centerX()-drawOffset,roomBarY/2-textBound.centerY(),paint);
        }

        float smallestNoteSize = roomSize/16.0f;
        radiusRect = Math.min(lineSize/2,smallestNoteSize/2);
        //paint.setStrokeWidth(0.5f);
        if(state != STATE_FINGER_DRAG){
            int consideredOffset = (roomOffset - 3)*4;
            int i;
            for(i=0;i<notes.size()-1;i++){
                if(offset[i+1]>consideredOffset){
                    break;
                }
            }
            startDrawNote = i;
            startTouchNote = -1;
        }
        for(int i=startDrawNote;i<notes.size();i++){
            Note aNote = notes.get(i);
            if(aNote.getPitch()>=0) {

                float startOffsetX = calStartOffsetX(i);
                float endOffsetX = startOffsetX + aNote.getDuration() * smallestNoteSize * 4;
                if(endOffsetX<=0){
                    continue;
                }
                if(startOffsetX>canvas.getWidth()){
                    break;
                }
                paint.setStyle(Paint.Style.FILL);
                if(i == selectedNote){
                    paint.setColor(Color.GRAY);
                    paint.setAlpha(50);
                    canvas.drawRect(startOffsetX,0,endOffsetX,canvas.getHeight(),paint);
                }
                paint.setColor(noteColors[aNote.getPitch()%7]);
                paint.setAlpha(255);
                float posY = noteLinesY[noteLinesY.length-1-aNote.getPitch()];
                if(state!=STATE_FINGER_DRAG&&startTouchNote<0){
                    if(endOffsetX>0){
                        startTouchNote = i;
                    }
                }

                RectF rect= new RectF(startOffsetX,posY-radiusRect,endOffsetX,posY+radiusRect);
                //Log.d("debugger",""+(smallestNoteSize*offset[i]*4+radiusRect)+","+posY+","+(smallestNoteSize*(offset[i]+aNote.getDuration())*4-radiusRect)+","+radiusRect);
                canvas.drawRoundRect(rect,radiusRect,radiusRect,paint);

                if(i==selectedNotePlay){
                    paint.setColor(needleColor);
                    Path path = new Path();
                    path.setFillType(Path.FillType.EVEN_ODD);
                    path.moveTo(startOffsetX-needleWidth,roomBarY);
                    path.lineTo(startOffsetX-needleWidth,upperBar);
                    path.lineTo(startOffsetX,upperBar+triangleHeight);
                    path.lineTo(startOffsetX+needleWidth,upperBar);
                    path.lineTo(startOffsetX+needleWidth,roomBarY);
                    path.lineTo(startOffsetX-needleWidth,roomBarY);
                    path.close();
                    canvas.drawPath(path,paint);
                    canvas.drawLine(startOffsetX,upperBar,startOffsetX,noteLinesY[noteLinesY.length-1],paint);
                }
                if(i == selectedNote&&state!=STATE_PLAYING){

                    paint.setColor(Color.DKGRAY);
                    for(int j=-1;j<2;j+=2) {
                        if(aNote.getPitch()==lineLength*2-2&&j==-1)
                            continue;
                        if(aNote.getPitch()==0&&j==1)
                            continue;
                        Path path = new Path();
                        path.setFillType(Path.FillType.EVEN_ODD);
                        float center = (endOffsetX + startOffsetX) / 2;
                        path.moveTo(center - triangleWidth / 2, posY + j*(radiusRect + triangleDist));
                        path.lineTo(center, posY + j*(radiusRect + triangleDist + triangleHeight));
                        path.lineTo(center + triangleWidth / 2, posY + j*(radiusRect + triangleDist));
                        path.lineTo(center, posY + j*(radiusRect + triangleDist + triangleHeight / 2));
                        path.lineTo(center - triangleWidth / 2, posY + j*(radiusRect + triangleDist));
                        path.close();
                        canvas.drawPath(path, paint);
                    }
                }
            }
        }
    }

    private void calculateOffset(){
        offset = new float[notes.size()+1];
        float lastOffset = 0;
        int i;
        for(i=0;i<notes.size();i++){
            Note aNote = notes.get(i);
            offset[i] = aNote.getOffset();
            lastOffset += aNote.getDuration();
            Log.d(LOG_TAG,"offset note "+aNote.getOffset());
            Log.d(LOG_TAG,"offset "+offset[i]);
        }
        offset[i] = lastOffset;
        Log.d(LOG_TAG,"offset "+offset[i]);
        maxRoomOffset = (int)(Math.ceil(lastOffset)/4) ;
    }

    private void calculateLinesY(boolean force,int h){
        if(noteLinesY==null||force==true) {
            int startHeight = (int) (heightStart * h);
            int endHeight = (int) (heightEnd * h);
            int lineSize = (endHeight-startHeight)/(lineLength);
            noteLinesY = new float[lineLength*2-1];
            for (int i = 0; i < noteLinesY.length; i += 1) {
                noteLinesY[i] = startHeight + lineSize * (i/2.0f+0.5f);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(state==STATE_PLAYING)
            return true;
        //selectNoteClick(event.getX(),event.getY());
        Log.d(LOG_TAG,"event action: "+event.getAction()+" pos: "+event.getX()+","+event.getY()+" time: "+(SystemClock.currentThreadTimeMillis()-startTouchTime));
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startTouchTime = SystemClock.currentThreadTimeMillis();
                //startTouchX = event.getX();
                //startTouchY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(SystemClock.currentThreadTimeMillis()-startTouchTime>longPressTime){
                    if(state!=STATE_FINGER_DRAG) {
                        state = STATE_FINGER_DRAG;
                        startTouchX = event.getX();
                        transitionBackHandler.post(updateFingerDragTransition);
                    }
                    else {
                        drawOffset = - 1.4f*(event.getX() - startTouchX);
                        //postInvalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(SystemClock.currentThreadTimeMillis()-startTouchTime<=longPressTime){
                    selectNoteClick(event.getX(),event.getY());
                }
                else {
                    float newOffset = changeRoomDrag( - 1.4f*(event.getX() - startTouchX));

                    drawOffset = drawOffset - newOffset;
                    transitionBackOffset = (drawOffset)/transitionBackTime;
                    transitionBackHandler.postDelayed(updateScreenBack,50);
                }
                //drawOffset = 0;
                state = STATE_NORMAL;
                //postInvalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                drawOffset = 0;
                state = STATE_NORMAL;
                postInvalidate();
        }
        return true;

    }

    private void selectNoteClick(float evX, float evY){
        if(selectedNote!= -1){
            float buttonCenter = (calStartOffsetX(selectedNote)+calStartOffsetX(selectedNote+1))/2;
            if(evX<buttonCenter+triangleWidth/2+touchTriangleErrorX&&evX>buttonCenter-triangleWidth/2-touchTriangleErrorX){
                Note aNote = notes.get(selectedNote);
                int pitch = aNote.getPitch();
                float posY = noteLinesY[noteLinesY.length -1 - pitch];
                if(evY>posY-triangleDist-triangleHeight-touchTriangleErrorYUpper && evY<posY-triangleDist+touchTriangleErrorYLower){
                    if(pitch<lineLength*2-2){
                        aNote.setPitch(pitch+1);
                        postInvalidate();
                    }
                    return;

                }
                if(evY>posY+triangleDist-touchTriangleErrorYLower && evY<posY+triangleDist+triangleHeight+touchTriangleErrorYUpper){
                    if(pitch>0){
                        aNote.setPitch(pitch-1);
                        postInvalidate();
                    }
                    return;
                }

            }
        }
        selectedNote = -1;
        for(int i=startTouchNote;i<notes.size();i++){
            if(calStartOffsetX(i)<=evX&&calStartOffsetX(i+1)>=evX){
                int pitch = notes.get(i).getPitch();
                if(pitch>=0) {
                    float posY = noteLinesY[noteLinesY.length - 1 - pitch];
                    if (evY <= posY + radiusRect + touchNoteError && evY >= posY - touchNoteError - radiusRect) {
                        selectedNote = i;
                    } else {
                        if(evY<=upperBar+triangleHeight){
                            selectedNotePlay = i;
                        }
                        else
                            selectedNote = -1;
                    }
                }
                else{
                    selectedNote = -1;
                }
                break;
            }
        }
        postInvalidate();
    }

    private float changeRoomDrag(float distX){
        int roomChangeAmt = (int)Math.abs(distX / roomSize);
        int lastRoom = roomOffset;
        if(Math.abs(distX/roomSize) - roomChangeAmt>0.66){
            roomChangeAmt+=1;
        }
        if(distX<0){
            roomChangeAmt = -roomChangeAmt;
        }
        incRoomOffset(roomChangeAmt);
        roomChangeAmt = roomOffset - lastRoom;
        return roomChangeAmt*roomSize;
    }





    public void incRoomOffset(int add){
        int lastRoom = maxRoomOffset - roomLength +2;
        roomOffset += add;
        roomOffset = (roomOffset>lastRoom)? lastRoom:roomOffset;
        roomOffset = (roomOffset<-1)?-1:roomOffset;
        postInvalidate();

    }


    private final float calStartOffsetX(int i){
        return roomSize/16.0f*offset[i]*4-drawOffset-roomOffset*roomSize;
    }

    private Runnable updateScreenBack = new Runnable() {
        @Override
        public void run() {
            drawOffset -= transitionBackOffset;

            transitionCount += 1;
            if(transitionCount==transitionBackTime){
                drawOffset = 0;
                transitionCount = 0;
                transitionBackHandler.removeCallbacks(updateScreenBack);
            }
            else{
                transitionBackHandler.postDelayed(updateScreenBack,30);
            }
            postInvalidate();

        }
    };

    public void updatePlayingNote(float playingPercent){
        if(1.0*offset[selectedNote+1]/offset[offset.length-1]<playingPercent){
            selectedNote+=1;
            if(!((roomOffset+roomLength-1)>=(int)(offset[selectedNote]/4)&&(roomOffset)<=(int)(offset[selectedNote]/4))){
                roomOffset = (int)(offset[selectedNote]/4);
            }
            postInvalidate();
        }
    }

    public void setSelectedNoteFromSelectedNotePlay(){
        selectedNote = selectedNotePlay;
        if((roomOffset+roomLength-1)>=(int)(offset[selectedNote]/4)&&(roomOffset)<=(int)(offset[selectedNote]/4)){
            postInvalidate();
            return;
        }
        roomOffset = (int)(offset[selectedNote]/4);
        postInvalidate();
    }

    private Runnable updateFingerDragTransition = new Runnable() {
        @Override
        public void run() {
            if(state == STATE_FINGER_DRAG) {
                postInvalidate();
                transitionBackHandler.postDelayed(this,50);
            }
            else{
                transitionBackHandler.removeCallbacks(this);
            }
        }
    };


}
