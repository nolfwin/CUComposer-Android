package com.example.user.cucomposer_android;

import android.app.*;
import android.graphics.*;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.example.user.cucomposer_android.entity.Part;

/**
 * Created by Wongse on 16/3/2558.
 */
public class SectionMerger extends Activity implements View.OnTouchListener {
    private int boxPart = 6;
    private boolean isDrag = false;
    private int sectionList[] = {6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6};
    private boolean hasIntro = false;
    private boolean hasBridge = false;
    private boolean hasSolo = false;
    private final int sectionId[] = {
            R.id.part01,
            R.id.part02,
            R.id.part03,
            R.id.part04,
            R.id.part05,
            R.id.part06,
            R.id.part07,
            R.id.part08,
            R.id.part09,
            R.id.part10,
            R.id.part11,
            R.id.part12
    };
    private final Part.PartType[] partTypes = {
            Part.PartType.INTRO,
            Part.PartType.VERSE,
            Part.PartType.PRECRORUS,
            Part.PartType.CHORUS,
            Part.PartType.BRIDGE,
            Part.PartType.SOLO,
            Part.PartType.BLANK
    };

    private final int id[] = {
            R.id.partIntro,
            R.id.partVerse,
            R.id.partPrechorus,
            R.id.partChorus,
            R.id.partBridge,
            R.id.partSolo
    };

    private final int defaultPart[] = {0, 1, 2, 3, 1, 2, 3, 4, 3, 6, 6, 6};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_merger);

        TextView resetButton = (TextView) findViewById(R.id.resetButton);
        TextView defaultButton = (TextView) findViewById(R.id.defaultButton);
        TextView mergeButton = (TextView) findViewById(R.id.mergeButton);
        resetButton.setOnTouchListener(this);
        defaultButton.setOnTouchListener(this);
        mergeButton.setOnTouchListener(this);

        for (int i = 0; i < id.length; i++) {
            TextView part = (TextView) findViewById(id[i]);
            part.setText(partTypes[i].NAME());
            part.setOnTouchListener(this);
        }

        for (int i = 0; i < sectionId.length; i++) {
            TextView section = (TextView) findViewById(sectionId[i]);
            section.setOnTouchListener(this);
        }

        TextView box = (TextView) findViewById(R.id.box);
        box.setOnTouchListener(this);

        TextView nextButton = (TextView) findViewById(R.id.nextButton);
        nextButton.setOnTouchListener(this);

        setSectionBySectionList();
    }

    public boolean onTouch(View view, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        TextView box = (TextView) findViewById(R.id.box);
        TextView message = (TextView) findViewById(R.id.message);
        int targetIndex = getTargetIndex();
        int firstTailIndex = getFirstTailIndex();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (view.getId() == R.id.resetButton) {
                    for (int i = 0; i < 12; i++)
                        sectionList[i] = 6;
                    setSectionBySectionList();
                    break;
                }
                if (view.getId() == R.id.defaultButton) {
                    for (int i = 0; i < defaultPart.length; i++)
                        sectionList[i] = defaultPart[i];
                    setSectionBySectionList();
                    break;
                }
                if (view.getId() == R.id.mergeButton) {
                    message.setText("All sections of your song are being merged together.");
                    break;
                }
                for (int i = 0; i < 6; i++) {
                    // Show part description
                    if (view.getId() == id[i]) {
                        TextView partText = (TextView) findViewById(R.id.partText);
                        partText.setBackgroundColor(partTypes[i].COLOR());
                        partText.setText(partTypes[i].DESCRIPTION());
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (targetIndex == firstTailIndex) {
                    for (int i = targetIndex; i < sectionList.length; i++) {
                        // Add new part into the section order at white box (Case append)
                        TextView target = (TextView) findViewById(sectionId[i]);
                        addPart(target, X, Y, targetIndex);
                    }
                } else {
                    // Add new part into the section order at white box (Case insert)
                    TextView target = (TextView) findViewById(sectionId[targetIndex]);
                    addPart(target, X, Y, targetIndex);
                }
                isDrag = false;
                // Remove box


                setBox(box,6);
                Rect directionRect = new Rect();
                TextView direction = (TextView) findViewById(R.id.direction);
                direction.getGlobalVisibleRect(directionRect);
                box.setY(directionRect.top);
                boxPart = 6;
                break;
            case MotionEvent.ACTION_MOVE:
                box.setX(X - box.getWidth() / 2);
                box.setY(Y - box.getHeight() / 2);
                for (int i = 0; i < targetIndex; i++) {
                    if (view.getId() == sectionId[i]) {
                        TextView part = (TextView) view;
                        // Create box when swipe out of the section order
                        if (!isOnView(part, X, Y) && !isDrag) {
                            setBox(box,sectionList[i]);
                            boxPart = sectionList[i];
                            for (int j = i; j < sectionList.length; j++) {
                                if (j == sectionList.length - 1)
                                    sectionList[j] = 6;
                                else
                                    sectionList[j] = sectionList[j + 1];
                            }
                            setSectionBySectionList();
                            isDrag = true;
                            break;
                        }
                    }
                }
                for (int i = 0; i < 6; i++) {
                    if (view.getId() == id[i]) {
                        TextView part = (TextView) view;
                        // Create box when swipe out of the part name
                        if (!isOnView(part, X, Y)) {
                            if (i == 0 && hasIntro) break;
                            if (i == 4 && hasBridge) break;
                            if (i == 5 && hasSolo) break;
                            setBox(box,i);
                            boxPart = i;
                        }
                    }
                }
                if (targetIndex == sectionList.length) break;
                boolean check = true;
                for (int i = 0; i < firstTailIndex; i++) {
                    TextView section = (TextView) findViewById(sectionId[i]);
                    // Insert a white box to the section order
                    if (isOnViewForPart(section, X, Y) && boxPart != 6) {
                        removeWhiteBox();
                        Log.v("insert", String.valueOf(targetIndex));
                        addWhiteBox(i);
                        setSectionBySectionList();
                        check = false;
                        break;
                    }
                }
                // Remove the white box between two non-white box
                if (targetIndex != firstTailIndex && check) {
                    removeWhiteBox();
                    setSectionBySectionList();
                }
                break;
        }
        return true;
    }

    private void setSectionBySectionList() {
        hasIntro = false;
        hasBridge = false;
        hasSolo = false;
        int introIndex = sectionList.length;
        for (int i = 0; i < sectionList.length; i++) {
            TextView section = (TextView) findViewById(sectionId[i]);
            setBox(section,sectionList[i]);
            if (sectionList[i] == 0) {
                hasIntro = true;
                introIndex = i;
            } else if (sectionList[i] == 4) hasBridge = true;
            else if (sectionList[i] == 5) hasSolo = true;
        }
        if (hasIntro && introIndex != 0) {
            int newSectionList[] = new int[sectionList.length];
            newSectionList[0] = 0;
            int j = 1;
            for (int i = 0; i < sectionList.length; i++) {
                if (sectionList[i] != 0) {
                    newSectionList[j] = sectionList[i];
                    j++;
                }
            }
            for (int i = 0; i < sectionList.length; i++) {
                sectionList[i] = newSectionList[i];
            }
            setSectionBySectionList();
        }
    }

    private int getTargetIndex() {
        for (int i = 0; i < sectionList.length; i++)
            if (sectionList[i] == 6) return i;
        return sectionList.length;
    }

    private int getFirstTailIndex() {
        int firstTailIndex = sectionList.length;
        for (int i = sectionList.length - 1; i >= 0; i--)
            if (sectionList[i] == 6) firstTailIndex = i;
            else return firstTailIndex;
        return firstTailIndex;
    }

    private boolean isOnView(View view, int X, int Y) {
        Rect viewRect = new Rect();
        view.getGlobalVisibleRect(viewRect);
        return viewRect.left <= X && X <= viewRect.right && viewRect.top <= Y && Y <= viewRect.bottom;
    }

    private boolean isOnViewForPart(View view, int X, int Y) {
        Rect viewRect = new Rect();
        view.getGlobalVisibleRect(viewRect);
        return viewRect.left <= X && X <= viewRect.right && Y <= viewRect.bottom;
    }

    private void addPart(View target, int X, int Y, int targetIndex) {
        if (isOnViewForPart(target, X, Y)) {
            if (boxPart == 0 && hasIntro) ;
            else if (boxPart == 4 && hasBridge) ;
            else if (boxPart == 5 && hasSolo) ;
            else if (boxPart == 6) ;
            else {
                sectionList[targetIndex] = boxPart;
                setSectionBySectionList();
            }
        }
    }

    private void removeWhiteBox() {
        int targetIndex = getTargetIndex();
        int firstTailIndex = getFirstTailIndex();
        if (targetIndex == firstTailIndex) return;
        for (int i = targetIndex; i < firstTailIndex && i < sectionList.length - 1; i++)
            sectionList[i] = sectionList[i + 1];
    }

    private void addWhiteBox(int index) {
        for (int j = sectionList.length - 1; j > index; j--) {
            sectionList[j] = sectionList[j - 1];
        }
        sectionList[index] = 6;
    }

    private void setBox(TextView box, int partId){
        box.setBackgroundColor(partTypes[partId].COLOR());
        box.setText(partTypes[partId].NICKNAME());
    }
}
