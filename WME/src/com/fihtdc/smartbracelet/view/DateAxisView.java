package com.fihtdc.smartbracelet.view;

import java.util.Date;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.format.DateUtils;
import android.util.AttributeSet;

import com.fihtdc.smartbracelet.provider.BraceletInfo.DayCoaching;
import com.fihtdc.smartbracelet.util.Utility;

public class DateAxisView extends TimeAxisView {
    int[][] mDatas = new int[6][2];
    String[][] mColumns = {{DayCoaching.COLUMN_NAME_CYCLE1, DayCoaching.COLUMN_NAME_HIT1}, 
            {DayCoaching.COLUMN_NAME_CYCLE2, DayCoaching.COLUMN_NAME_HIT2}, 
            {DayCoaching.COLUMN_NAME_CYCLE3, DayCoaching.COLUMN_NAME_HIT3}, 
            {DayCoaching.COLUMN_NAME_CYCLE4, DayCoaching.COLUMN_NAME_HIT4}, 
            {DayCoaching.COLUMN_NAME_CYCLE5, DayCoaching.COLUMN_NAME_HIT5},
            {DayCoaching.COLUMN_NAME_CYCLEM, DayCoaching.COLUMN_NAME_HITM}};
    
    private static final String TAG = "DateAxisView";

    public DateAxisView(Context context) {
        this(context, null);
    }

    public DateAxisView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DateAxisView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void drawText(Canvas canvas, float x, int index){
        if (mCursor == null || mTimeColumnIndex == NO_COLUMN_INDEX) {
            return;
        }
        
        if (mCursor.moveToPosition(index)) {
            long time = mCursor.getLong(mTimeColumnIndex);
            
            String dateString;
            String pattern;
            Date date = new Date(time);
            Date current = new Date(System.currentTimeMillis());
            if (current.getYear() == date.getYear()) {
                pattern = "MM/dd";
            } else {
                pattern = "yy/MM/dd";
            }
            dateString = Utility.getDisplayDate( time, pattern);
            
            canvas.drawText(dateString, x, mTimeDrawY, mTextPaint);
        }
        
    }

    @Override
    protected int getLevel(int index) {
        int level = 0;
        int cycle = 0;
        int hit = 0;

        for (int i = 0; i < 6; i++) {
            cycle += mCursor.getInt(mCursor.getColumnIndex(mColumns[i][0]));
            hit += mCursor.getInt(mCursor.getColumnIndex(mColumns[i][1]));
        }

        try {
            level = Utility.getHitStarNum(hit, cycle);
        } catch (Exception e) {
            
        }

        return level;
    }
}
