package com.fihtdc.smartbracelet.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class SameHeightHistogram extends HistogramView{
    public SameHeightHistogram(Context context) {
        this(context, null);
    }

    public SameHeightHistogram(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public SameHeightHistogram(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mShowAnimation = false;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        drawXAxis(canvas);
        drawContent(canvas);
    }
    
    protected void drawRect(Canvas canvas, float x, int index) {
        canvas.drawRect(x, mRectTopY, x + mObjectWidth, mRectBottomY, getPaint(getLevel(index)));
    }

}
