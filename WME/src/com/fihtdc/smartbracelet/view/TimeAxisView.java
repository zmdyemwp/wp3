package com.fihtdc.smartbracelet.view;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.text.TextPaint;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.util.LogApp;
import com.fihtdc.smartbracelet.util.Utility;

public class TimeAxisView extends View {

    private static final String TAG = "TimeAxisView";

    protected static final int NOT_MATCHED_INDEX = -1;
    
    protected static final int NO_COLUMN_INDEX = -1;
    protected static String COLUMN_NAME_TIME = "time";
    protected static String COLUMN_NAME_CYCLE = "cycle";
    protected static String COLUMN_NAME_HIT = "hit";
    
    protected static int FLAGS_TIME = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR;

    
    protected Context mContext;
    private GestureDetector mGestureDetector;

    OnMarchedContentChangedListener mContentChangedListener;
    OnContentTapListener mContentTapListener;
    OnScollFlingCompleteListener mScollFlingCompleteListener;

    Paint mXAxisPaint;
    Paint mXAxisBackGroundPaint;
    Paint mAxisPaint;
    TextPaint mTextPaint;
    Paint mObjectPaint;
    float mTextHeight;
   
    Paint mScoreZeroPaint;
    Paint mScoreOnePaint;
    Paint mScoreTwoPaint;
    Paint mScoreThreePaint;

    // The value to record scroll distance on X
    float mScrollX;

    int mDataNum;
    Cursor mCursor;
    int mTimeColumnIndex = NO_COLUMN_INDEX;
    int mCycleColumnIndex = NO_COLUMN_INDEX;
    int mHitColumnIndex = NO_COLUMN_INDEX;

    float mObjectGap;
    float mObjectWidth;
    float mRightMagin;
    float mObjectRadius;
    float mScrollGap;

    // The number of content visible on the view
    // This value may not accurate, may less 1
    int mShowNum;

    // The number of data that content visible on the view
    int mVisibleDataMaxNum;

    // The index of data that marched standard line
    int mOrginMatchedIndex;

    // The X coordinate of last visible content to draw
    float mRightStartX;
    // The X coordinate of standard line to draw
    float mStandardLineX;

    float mTimeDrawY;
    float mDateDrawY;
    float mTextLinesMargin = 5;;
    float mXAxisY;
    float mViewMargin = 15;
    float mXAxisTextMagin = 5;
    int mXAxisHeigt = 6;
    
    float mTextSize;
    String mDataString = null;
    
    boolean mNeedNotifyListener = false;
    boolean mAttached = false;
    
    boolean mScrollorFling = false;

    public TimeAxisView(Context context) {
        this(context, null);
    }

    public TimeAxisView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeAxisView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimeAxisView);
        Resources r = mContext.getResources();
        mObjectGap = a.getDimension(R.styleable.TimeAxisView_objectGap,
                r.getDimension(R.dimen.timeaxis_object_gap));
        mObjectWidth = a.getDimension(R.styleable.TimeAxisView_objectWidth,
                r.getDimension(R.dimen.timeaxis_object_width));
        mRightMagin = a.getDimension(R.styleable.TimeAxisView_rightMagin,
                r.getDimension(R.dimen.timeaxis_right_margin));
        mTextSize = a.getDimension(R.styleable.TimeAxisView_rightMagin, 
                r.getDimension(R.dimen.timeaxis_text_size));
        a.recycle();

        initPaints();
        initValues();
        mGestureDetector = new GestureDetector(context, mGestureListener);
    }

    private void initPaints() {
        mXAxisPaint = new Paint();
        mXAxisPaint.setColor(Color.WHITE);
        mXAxisPaint.setStyle(Paint.Style.FILL);
        mXAxisPaint.setStrokeWidth(mXAxisHeigt);
        
        mXAxisBackGroundPaint = new Paint();
        mXAxisBackGroundPaint.setColor(mContext.getResources().getColor(R.color.actionbar_color));
        mXAxisBackGroundPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Align.CENTER);
   
        FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.descent - fontMetrics.ascent;

        mObjectPaint = new Paint();
        mObjectPaint.setColor(Color.WHITE);
        mObjectPaint.setStyle(Paint.Style.FILL);
        mObjectPaint.setAntiAlias(true);

        mScoreZeroPaint = new Paint();
        mScoreZeroPaint.setColor(mContext.getResources().getColor(R.color.score_zero_color));
        mScoreZeroPaint.setStyle(Paint.Style.FILL);
        mScoreZeroPaint.setAntiAlias(true);
        
        mScoreOnePaint = new Paint();
        mScoreOnePaint.setColor(mContext.getResources().getColor(R.color.score_one_color));
        mScoreOnePaint.setStyle(Paint.Style.FILL);
        mScoreOnePaint.setAntiAlias(true);

        mScoreTwoPaint = new Paint();
        mScoreTwoPaint.setColor(mContext.getResources().getColor(R.color.score_two_color));
        mScoreTwoPaint.setStyle(Paint.Style.FILL);
        mScoreTwoPaint.setAntiAlias(true);

        mScoreThreePaint = new Paint();
        mScoreThreePaint.setColor(mContext.getResources().getColor(R.color.score_three_color));
        mScoreThreePaint.setStyle(Paint.Style.FILL);
        mScoreThreePaint.setAntiAlias(true);
    }

    private void initValues() {
        mScrollX = 0;
        mObjectRadius = mObjectWidth/2;
        initDataNum();
    }
    
    protected void initDataNum() {
        int count =  mCursor != null ? mCursor.getCount() : 0;
        if (mDataNum != count && count == 0) {
            mNeedNotifyListener = true;
        }
        
        mDataNum = count;
        mVisibleDataMaxNum = mDataNum;
        mOrginMatchedIndex = mVisibleDataMaxNum - 1;
        mScrollGap = mObjectGap - mObjectRadius;
    }
    
    public void setCursor(Cursor cursor){
        mCursor = cursor;
        initDataNum();
        initColumnIndex();
        invalidate();
        onMarchedContentChange();
        
        mScrollorFling = true;
        onScrollFlingComplete();
    }
    
    protected void initColumnIndex(){
        if (mCursor != null) {
            mTimeColumnIndex = mCursor.getColumnIndex(COLUMN_NAME_TIME);
            mCycleColumnIndex = mCursor.getColumnIndex(COLUMN_NAME_CYCLE);
            mHitColumnIndex = mCursor.getColumnIndex(COLUMN_NAME_HIT);
        } else {
            mTimeColumnIndex = NO_COLUMN_INDEX;
            mCycleColumnIndex = NO_COLUMN_INDEX;
            mHitColumnIndex = NO_COLUMN_INDEX;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mRightStartX = getWidth() - mRightMagin - mObjectWidth;
        mShowNum = (int) (mRightStartX / mObjectGap) + 1;
        mStandardLineX = mRightStartX + mObjectWidth / 2;

        mDateDrawY = getHeight() - mViewMargin;
        mTimeDrawY = mDateDrawY - mTextLinesMargin - mTextHeight;

        mXAxisY = mTimeDrawY - mTextHeight - mXAxisTextMagin - mObjectWidth / 2;

        mScrollorFling = true;
        onScrollFlingComplete();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawXAxis(canvas);
        drawContent(canvas);
    }

    protected void drawXAxis(Canvas canvas) {
        canvas.drawRect(0, mXAxisY,  getWidth(), getHeight(), mXAxisBackGroundPaint);
        canvas.drawLine(0, mXAxisY, getWidth(), mXAxisY, mXAxisPaint);
    }

    protected void drawContent(Canvas canvas) {
        // Actual show point number: data may more or less than SHOW_NUM
        int actualShowNum = Math.min(mShowNum, mVisibleDataMaxNum);
        float startX;
        int index;

        if (mCursor == null || mDataNum <= 0) {
            return;
        }
        
        mDataString = null;
        
        // Show offset content on left and right out of screen
        // To show more continuous
        if (mVisibleDataMaxNum > mShowNum) {
            // Left out of screen has data
            startX = -mScrollX + mRightStartX - mShowNum * mObjectGap;
            index = mVisibleDataMaxNum - mShowNum - 1;
            drawContent(canvas, startX, index);
        }
        
        // Show visible content
        for (int i = actualShowNum - 1; i >= 0 ; i--) {
            startX = -mScrollX + mRightStartX - i * mObjectGap;
            index = mVisibleDataMaxNum - 1 - i;
            drawContent(canvas, startX, index);
        }

        if (mVisibleDataMaxNum < mDataNum) {
            // Right out of screen has data
            startX = -mScrollX + mRightStartX + mObjectGap;
            drawContent(canvas, startX, mVisibleDataMaxNum);
        }

    }
    
    protected void drawContent(Canvas canvas, float startX, int index){
        drawText(canvas, startX + mObjectRadius, index);
        drawCircle(canvas, startX + mObjectRadius, mXAxisY, mObjectRadius,
                index);
    }
    
    protected void drawText(Canvas canvas, float x, int index){
        if (mCursor == null || mTimeColumnIndex == NO_COLUMN_INDEX) {
            return;
        }
        
        if (mCursor.moveToPosition(index)) {
            long time = mCursor.getLong(mTimeColumnIndex);
            
            String dateString = null;
            String pattern = "MM/dd";
            
            if(!DateUtils.isToday(time)){
                Date date = new Date(time);
                Date current =  new Date(System.currentTimeMillis());
                if (current.getYear() == date.getYear()) {
                    pattern = "MM/dd";
                }else {
                    pattern = "yy/MM/dd";
                }
                dateString = Utility.getDisplayDate( time, pattern);
            } else {
                if (mDataString != null) {
                    dateString = Utility.getDisplayDate( time, pattern);
                }
            }

            //Show date only when date different with previous date 
            if (dateString != null && !dateString.equals(mDataString)) {
                mDataString = dateString;
                canvas.drawText(dateString, x, mDateDrawY, mTextPaint);
            }
            
            canvas.drawText(Utility.getDisplayDate( time, "HH:mm"), x,
                    mTimeDrawY, mTextPaint);
        }
        
    }

    protected void drawCircle(Canvas canvas, float x, float y, float radius, int index) {
        canvas.drawCircle(x, y, radius, mObjectPaint);
        canvas.drawCircle(x, y, radius - 5, getPaint(getLevel(index)));
    }
    
    protected int getLevel(int index){
        int level = 0;
        int cycle;
        int hit;
        
        if (mCursor == null || mCycleColumnIndex == NO_COLUMN_INDEX
                || mHitColumnIndex == NO_COLUMN_INDEX) {
            return level;
        }
        
        if (mCursor.moveToPosition(index)) {
            cycle = mCursor.getInt(mCycleColumnIndex);
            hit = mCursor.getInt(mHitColumnIndex);
            
            try {
                level = Utility.getHitStarNum(hit, cycle);
                
            } catch (Exception e) {
                
            }
        }
        return level;
    }
    

    protected Paint getPaint(int index) {
        Paint paint = mScoreZeroPaint;

        if (index == 0) {
            paint = mScoreZeroPaint;
        } else if (index == 1) {
            paint = mScoreOnePaint;
        } else if (index == 2) {
            paint = mScoreTwoPaint;
        } else if (index == 3) {
            paint = mScoreThreePaint;
        }

        return paint;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_UP:
            onScrollFlingComplete();
            reset();
            break;
        case MotionEvent.ACTION_DOWN:
            mScrollX = 0;
            break;
        default:
            break;
        }

        return mGestureDetector.onTouchEvent(event);
    }

    protected void reset() {
        if (mScrollX != 0) {
            mScrollX = 0;
            invalidate();

            onMarchedContentChange();
        }
    }
    
    protected void onMarchedContentChange(){
        if (mContentChangedListener != null) {
            mOrginMatchedIndex = mVisibleDataMaxNum - 1;
            if (mDataNum > 0 && mOrginMatchedIndex >= 0 || mNeedNotifyListener) {
                mContentChangedListener.onMarchedContentChanged(this, mOrginMatchedIndex);
                mNeedNotifyListener = false;
            }
        }
    }
    
    protected void onScrollFlingComplete(){
        
        if (mDataNum > 0 && mScrollorFling && mScollFlingCompleteListener != null ) {
            int startIndex = mVisibleDataMaxNum - Math.min(mShowNum, mVisibleDataMaxNum);
            int endIndex = mVisibleDataMaxNum - 1;
            
            mScrollorFling = false;
            if (startIndex > endIndex) {
                return;
            }

            if (startIndex >= 1) {
                int num = endIndex - startIndex + 1;
                float leftObjectEndX = mRightStartX + mObjectWidth - num * mObjectGap;
                if (leftObjectEndX > 0) {
                    startIndex -= 1;
                }
            }

            mScollFlingCompleteListener.OnScollFlingComplete(this, startIndex, endIndex);

        }
    }

    private final GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            mScrollX = 0;
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mScrollorFling = true;
            mScrollX += distanceX;
            
            // Move distance more than one GAP
            if (Math.abs(mScrollX) >= mScrollGap) {
                if (mScrollX < 0) {
                    // Move right
                    if (mVisibleDataMaxNum != 1) {
                        mVisibleDataMaxNum -= 1;

                        // If mVisibleDataMaxIndex change, reset mScrollX
                        mScrollX = mObjectRadius;
                    }
                } else {
                    // Move left
                    if (mVisibleDataMaxNum != mDataNum) {
                        mVisibleDataMaxNum += 1;

                        // If mVisibleDataMaxIndex change, reset mScrollX
                        mScrollX = -mObjectRadius;
                    }
                }

                if (mVisibleDataMaxNum < 1) {
                    mVisibleDataMaxNum = 1;
                } else if (mVisibleDataMaxNum > mDataNum) {
                    mVisibleDataMaxNum = mDataNum;
                }
            }
            
            checkMatchedStandLineChange();
            
            invalidate();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mContentTapListener != null && mDataNum > 0) {
                int index = getTapContentIndex(e);
                if (index >= 0) {
                    mContentTapListener.onContentSigleTap(TimeAxisView.this, index);
                }
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mContentTapListener != null && mDataNum > 0) {
                int index = getTapContentIndex(e);
                if (index >= 0) {
                    mContentTapListener.onContentDoubleTap(TimeAxisView.this, index);
                }
            }
            return true;
        }
    };

    protected boolean isMatched(float startX, int index) {
        if (startX <= mStandardLineX && (startX + mObjectWidth) >= mStandardLineX) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return void
     * @Description: Check stand line in which object range
     */
    protected void checkMatchedStandLineChange() {
        int matchedIndex = NOT_MATCHED_INDEX;
        int actualShowNum = Math.min(mShowNum, mVisibleDataMaxNum);
        int index;
        int offsetIndex;
        float startX;
        
        if (Math.abs(mScrollX) <= mObjectRadius) {
            offsetIndex = 0;
        } else {
            offsetIndex = 1;
        }

        startX = -mScrollX + mRightStartX - offsetIndex * mObjectGap;
        index = mVisibleDataMaxNum - 1 - offsetIndex;
        if (isMatched(startX, index)) {
            matchedIndex = index;
        }

        if (mVisibleDataMaxNum < mDataNum) {
            startX = -mScrollX + mRightStartX + mObjectGap;
            if (isMatched(startX, mVisibleDataMaxNum - 1)) {
                matchedIndex = mVisibleDataMaxNum - 1;
            }
        }

        if (mVisibleDataMaxNum > mShowNum) {
            startX = -mScrollX + mRightStartX + mObjectGap - mShowNum * mObjectGap;
            index = mVisibleDataMaxNum - 1 - actualShowNum;
            if (isMatched(startX, index)) {
                matchedIndex = mVisibleDataMaxNum - 1 - actualShowNum;
            }
        }

        if (mOrginMatchedIndex != matchedIndex && mContentChangedListener != null && mDataNum > 0) {
            if (matchedIndex >= 0) {
                mContentChangedListener.onMarchedContentChanged(this, matchedIndex);
            } else if (matchedIndex == NOT_MATCHED_INDEX){
                mContentChangedListener.onMarchedNothing(this);
            }
            mOrginMatchedIndex = matchedIndex;
        }
    }

    /**
     * @return int
     * @param MotionEvent
     * @Description: Get index of content which be tap
     */
    protected int getTapContentIndex(MotionEvent e) {
        float distance = mObjectGap + mRightStartX - e.getX();
        int offsetIndex = (int) (distance / mObjectGap);

        float rangXMin = mRightStartX - offsetIndex * mObjectGap;

        // In X range
        if (e.getX() > rangXMin && e.getX() < (rangXMin + mObjectWidth)) {
            int index = mVisibleDataMaxNum - offsetIndex - 1;

            // In Y range
            if (e.getY() >  mViewMargin && e.getY() < mDateDrawY) {
                return index;
            }
        }

        return NOT_MATCHED_INDEX;
    }

    public interface OnMarchedContentChangedListener {
        public void onMarchedContentChanged(View view, int index);

        public void onMarchedNothing(View view);
    }

    public interface OnContentTapListener {
        public void onContentSigleTap(View view, int index);

        public void onContentDoubleTap(View view, int index);
    }
    
    public interface OnScollFlingCompleteListener {
        public void OnScollFlingComplete(View view, int startIndex, int endIndex);
    }

    public void setOnMarchedContnetChangedListener(OnMarchedContentChangedListener listener) {
        mContentChangedListener = listener;
        onMarchedContentChange();
    }

    public void setOnContentTapListener(OnContentTapListener listener) {
        mContentTapListener = listener;
    }
    
    public void setOnScollFlingCompleteListener(OnScollFlingCompleteListener listener) {
        mScollFlingCompleteListener = listener;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!mAttached) {
            mAttached = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            getContext().registerReceiver(mIntentReceiver, filter);
        }

    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            getContext().unregisterReceiver(mIntentReceiver);
            mAttached = false;
        }
    }
    
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           invalidate();
        }
    };
    
    public float getContentWidth(){
        return mObjectWidth;
    }
    
    /**
     * @return the X coordinate of index context in whole screen; 
     * @param index: This index is cursor index, but not the visible index
     */
    public float getContentStartX(int index){
        int offsetIndex = mVisibleDataMaxNum - index - 1;
        float startX = getLeft() + mRightStartX - offsetIndex * mObjectGap;
        return startX;
    }
    
    /**
     * @return the Y coordinate of index context in whole screen; 
     * @param index: the index is not use now, Y coordinate value is same
     */
    public float getContentStartY(int index){
        return getTop() + mXAxisY - mXAxisHeigt;
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        LogApp.Logd("widthMeasureSpec="+widthMeasureSpec+"heightMeasureSpec="+heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
