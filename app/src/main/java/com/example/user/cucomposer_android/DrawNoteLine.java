package com.example.user.cucomposer_android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.user.cucomposer_android.entity.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nuttapong on 1/21/2015.
 */
public class DrawNoteLine extends View {

    private List<Note> notes;


    private int roomLength = 3;
    private int lineLength = 11;

    private float heightStart = 0.1f;
    private float heightEnd = 0.9f;

    private float triangleDist = 10;
    private float triangleWidth = 40;
    private float triangleHeight = 20;

    private int touchNoteError = 25;
    private long longPressTime = 5;
    private int touchTriangleErrorX =20;
    private int touchTriangleErrorYUpper =100;
    private int touchTriangleErrorYLower = 10;

    private int transitionBackTime = 5;

    // temp
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
    private boolean isTransiting = false;
    private float radiusRect = 0;
    private long startTouchTime = 0;
    private float startTouchX = 0;
    private float startTouchY = 0;
    private final String LOG_TAG = "drawNote debug";
    private int transitionCount = 0;
    private float transitionBackOffset = 0;

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

        this.setBackgroundColor(Color.parseColor("#f5f5f5"));
        paint.setColor(Color.BLACK);
        notes = new ArrayList<Note>();
        notes.add(new Note(3,2.5f));
        notes.add(new Note(4,2f));
        notes.add(new Note(1, 1f));
        notes.add(new Note(10, 0.25f));
        for(int i=0;i<20;i++){
            notes.add(new Note(i,0.25f));
        }
        notes.add(new Note(-1,2));
        for(int i=0;i<20;i++){
            notes.add(new Note(i,2));
        }
        calculateOffset();

    }

    @Override
    public void onDraw(Canvas canvas) {
        calculateLinesY(false,canvas.getHeight());
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLACK);
        for(int i=0;i<noteLinesY.length;i+=2) {
            canvas.drawLine(0, noteLinesY[i], canvas.getWidth(), noteLinesY[i], paint);
        }
        roomSize = canvas.getWidth()/(roomLength);
        int startDrawRoom = (roomOffset-2>=0)?-2:-roomOffset;
        int endDrawRoom = (roomLength+2+roomOffset<=maxRoomOffset)?roomLength+2:maxRoomOffset-roomOffset;
        for(int i=startDrawRoom;i<endDrawRoom;i++){
            canvas.drawLine(roomSize*(i+1)-drawOffset,noteLinesY[0],roomSize*(i+1)-drawOffset,noteLinesY[noteLinesY.length-1],paint);
        }
        float lineSize = noteLinesY[1]-noteLinesY[0];
        float smallestNoteSize = roomSize/16.0f;
        radiusRect = Math.min(lineSize/2,smallestNoteSize/2);
        //paint.setStrokeWidth(0.5f);
        if(!isTransiting){
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
                if(!isTransiting&&startTouchNote<0){
                    if(endOffsetX>0){
                        startTouchNote = i;
                    }
                }

                RectF rect= new RectF(startOffsetX,posY-radiusRect,endOffsetX,posY+radiusRect);
                //Log.d("debugger",""+(smallestNoteSize*offset[i]*4+radiusRect)+","+posY+","+(smallestNoteSize*(offset[i]+aNote.getDuration())*4-radiusRect)+","+radiusRect);
                canvas.drawRoundRect(rect,radiusRect,radiusRect,paint);

                if(i == selectedNote){

                    paint.setColor(Color.DKGRAY);
                    for(int j=-1;j<2;j+=2) {
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
            offset[i] = lastOffset;
            lastOffset += notes.get(i).getDuration();
        }
        offset[i] = lastOffset;
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
                    if(!isTransiting) {
                        isTransiting = true;
                        startTouchX = event.getX();
                    }
                    else {
                        drawOffset = event.getX() - startTouchX;
                        postInvalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(SystemClock.currentThreadTimeMillis()-startTouchTime<=longPressTime){
                    selectNoteClick(event.getX(),event.getY());
                }
                else {
                    float newOffset = changeRoomDrag(event.getX() - startTouchX);

                    drawOffset = drawOffset - newOffset;
                    transitionBackOffset = (drawOffset)/transitionBackTime;
                    transitionBackHandler.postDelayed(updateScreenBack,50);
                }
                //drawOffset = 0;
                isTransiting = false;
                //postInvalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                drawOffset = 0;
                isTransiting = false;
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

}
