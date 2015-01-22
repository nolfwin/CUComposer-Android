package com.example.user.cucomposer_android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
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
    private int lineLength = 10;
    private float[] offset;
    private float[] noteLinesY;

    Paint paint = new Paint();

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
        paint.setColor(Color.BLACK);
        notes = new ArrayList<Note>();
        notes.add(new Note(3,2.5f));
        notes.add(new Note(4,2f));
        notes.add(new Note(1, 1f));
        notes.add(new Note(10, 0.25f));
        calculateOffset();
    }

    @Override
    public void onDraw(Canvas canvas) {
        calculateLinesY(false,canvas.getHeight());

        paint.setColor(Color.BLACK);
        for(int i=0;i<noteLinesY.length;i+=2) {
            canvas.drawLine(0, noteLinesY[i], canvas.getWidth(), noteLinesY[i], paint);
        }
        int roomSize = canvas.getWidth()/(roomLength);
        for(int i=0;i<roomLength-1;i++){
            canvas.drawLine(roomSize*(i+1),noteLinesY[0],roomSize*(i+1),noteLinesY[noteLinesY.length-1],paint);
        }
        float lineSize = noteLinesY[1]-noteLinesY[0];
        float smallestNoteSize = roomSize/16.0f;
        float radiusRect = Math.min(lineSize/2,smallestNoteSize/2);
        for(int i=0;i<notes.size();i++){
            Note aNote = notes.get(i);
            if(aNote.getPitch()>=0) {
                float posY = noteLinesY[noteLinesY.length-1-aNote.getPitch()];
                RectF rect= new RectF(smallestNoteSize*offset[i]*4,posY-radiusRect,smallestNoteSize*(offset[i]+aNote.getDuration())*4,posY+radiusRect);
                //Log.d("debugger",""+(smallestNoteSize*offset[i]*4+radiusRect)+","+posY+","+(smallestNoteSize*(offset[i]+aNote.getDuration())*4-radiusRect)+","+radiusRect);
                canvas.drawRoundRect(rect,radiusRect,radiusRect,paint);
            }
        }
    }

    private void calculateOffset(){
        offset = new float[notes.size()];
        float lastOffset = 0;
        for(int i=0;i<notes.size();i++){
            offset[i] = lastOffset;
            lastOffset += notes.get(i).getDuration();
        }
    }

    private void calculateLinesY(boolean force,int h){
        if(noteLinesY==null||force==true) {
            int startHeight = (int) (0.1 * h);
            int endHeight = (int) (0.6 * h);
            int lineSize = (endHeight-startHeight)/(lineLength);
            noteLinesY = new float[lineLength*2-1];
            for (int i = 0; i < noteLinesY.length; i += 1) {
                noteLinesY[i] = startHeight + lineSize * (i/2.0f+0.5f);
            }
        }
    }
}
