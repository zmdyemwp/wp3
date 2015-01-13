package com.fihtdc.smartbracelet.view;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.util.Utility;
import com.yl.ekgrr.EkgRR;

public class HistogramView extends TimeAxisView {
    private static final String TAG = "HistogramView";

    protected static String COLUMN_NAME_AGILITY = "agility";
    protected static String COLUMN_NAME_EMOTION_STATUS = "status";
    
    Paint mAxisPaint;
    Paint mStandLinePaint;
    Path mStandLinePath = new Path();
    int mStandLineColor;
    
    Paint mPassivePaint;
    Paint mExcitablePaint;
    Paint mPessimisticPaint;
    Paint mAnxiousPaint;
    Paint mBalancePaint;
    
    float mRectTopY;
    float mRectBottomY;
    float mRectHeight;
    float mRectUnitHeight;
    
    boolean mShowAnimation = true;
    int mAnimationTimes = 0;
    int mAnimationTotalTimes;
    int mAnimationInterval;
    float mSpeed;
    
    Timer mTimer;
    TimerTask mTask ;
    
    int mAgilityColumnIndex = NO_COLUMN_INDEX;
    int mEmotionColumnIndex = NO_COLUMN_INDEX;
    
    public HistogramView(Context context) {
        this(context, null);
    }

    public HistogramView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public HistogramView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimeAxisView);
        mAnimationInterval = a.getInt(R.styleable.TimeAxisView_animationInterval, 50);
        mAnimationTotalTimes = a.getInt(R.styleable.TimeAxisView_animationTimes, 25);
        mStandLineColor = a.getColor(R.styleable.TimeAxisView_standLineColor, 0xff077783);
        a.recycle();

        initPaints();
        
        initTimer();
        mSpeed =  100f / mAnimationTotalTimes;;
    }
    
    private void initPaints() {
        mAxisPaint = new Paint();
        mAxisPaint.setColor(Color.GRAY);
        mAxisPaint.setStyle(Paint.Style.STROKE);
        PathEffect effects = new DashPathEffect(new float[]{10,10},1);
        mAxisPaint.setPathEffect(effects);
        
        FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.descent - fontMetrics.ascent;
        
        mStandLinePaint = new Paint();
        mStandLinePaint.setColor(mStandLineColor);
        mStandLinePaint.setStyle(Paint.Style.STROKE);
        mStandLinePaint.setStrokeWidth(6);
        mStandLinePaint.setPathEffect(effects);
        PathEffect standLineEffects = new DashPathEffect(new float[]{10,10},1);
        mStandLinePaint.setPathEffect(standLineEffects);
        
        mPassivePaint = new Paint();
        mPassivePaint.setColor(mContext.getResources().getColor(R.color.passive_color));
        mPassivePaint.setStyle(Paint.Style.FILL);

        mExcitablePaint = new Paint();
        mExcitablePaint.setColor(mContext.getResources().getColor(R.color.excitable_color));
        mExcitablePaint.setStyle(Paint.Style.FILL);
        
        mPessimisticPaint = new Paint();
        mPessimisticPaint.setColor(mContext.getResources().getColor(R.color.pessimistic_color));
        mPessimisticPaint.setStyle(Paint.Style.FILL);
        
        mAnxiousPaint = new Paint();
        mAnxiousPaint.setColor(mContext.getResources().getColor(R.color.anxious_color));
        mAnxiousPaint.setStyle(Paint.Style.FILL);
        
        mBalancePaint = new Paint();
        mBalancePaint.setColor(mContext.getResources().getColor(R.color.balance_color));
        mBalancePaint.setStyle(Paint.Style.FILL);

    }

    private void initTimer(){
        mTimer = new Timer();
        mTask = new TimerTask() {
            
            @Override
            public void run() {
                mAnimationTimes ++;
                handler.sendEmptyMessage(0);
            }
        };
        
        if (mShowAnimation) {
            mTimer.schedule(mTask, 200, mAnimationInterval);
        }
    }
    
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mAnimationTimes > mAnimationTotalTimes) {
                mShowAnimation = false;
                if (mTask != null) {
                    mTask.cancel();
                }
                if (mTimer != null) {
                    mTimer.cancel();
                }
            }
            invalidate();
        }
        
    };
    
    protected void initColumnIndex() {
        super.initColumnIndex();
        if (mCursor != null) {
            mAgilityColumnIndex = mCursor.getColumnIndex(COLUMN_NAME_AGILITY);
            mEmotionColumnIndex = mCursor.getColumnIndex(COLUMN_NAME_EMOTION_STATUS);
        } else {
            mAgilityColumnIndex = NO_COLUMN_INDEX;
            mEmotionColumnIndex = NO_COLUMN_INDEX;
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mXAxisY = mTimeDrawY - mTextHeight - mXAxisTextMagin;
        
        mRectBottomY = mXAxisY - mXAxisHeigt/2;
        mRectTopY = mViewMargin;
        mRectHeight = mRectBottomY - mRectTopY;
        mRectUnitHeight = mRectHeight/100; 
        
        mStandLinePath.reset();
        mStandLinePath.moveTo(mStandardLineX, 0);
        mStandLinePath.lineTo(mStandardLineX, mRectBottomY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawAxes(canvas);
        drawXAxis(canvas);
        drawStandLine(canvas);
        drawContent(canvas);
    }
    
    protected void drawXAxis(Canvas canvas){
        canvas.drawRect(0, mXAxisY,  getWidth(), getHeight(), mXAxisBackGroundPaint);
        canvas.drawLine(0, mXAxisY, getWidth(), mXAxisY, mXAxisPaint);
    }
    
    protected void drawAxes(Canvas canvas) {
        float distanceY = mRectHeight/5;
        
        Path path = new Path();
        for (int i = 1; i < 6; i++) {
            path.reset();
            path.moveTo(0, mRectBottomY - distanceY * i);
            path.lineTo(getWidth(), mRectBottomY - distanceY * i);     
            canvas.drawPath(path, mAxisPaint);
        }
    }
    
    protected void drawStandLine(Canvas canvas) {
        canvas.drawPath(mStandLinePath, mStandLinePaint);
    }

    protected void drawContent(Canvas canvas, float startX, int index){
        drawText(canvas, startX + mObjectWidth/2, index);
        drawRect(canvas, startX, index);
    }
    
    

    @Override
    protected int getLevel(int index) {
        int level = 0;
        if (mCursor == null
                || mEmotionColumnIndex == NO_COLUMN_INDEX) {
            return level;
        }
        
        if (mCursor.moveToPosition(index)) {
            level = mCursor.getInt(mEmotionColumnIndex);
        }
        
        return level;
    }

    @Override
    protected Paint getPaint(int index) {
        if (EkgRR.EMOSTATUS_PASSIVE == index) {
            return mPassivePaint; 
        } else if (EkgRR.EMOSTATUS_EXCITABLE  == index) {
            return mExcitablePaint; 
        } else if (EkgRR.EMOSTATUS_PESSIMISTIC  == index) {
            return mPessimisticPaint; 
        } else if (EkgRR.EMOSTATUS_ANXIOUS == index) {
            return mAnxiousPaint; 
        } else {
            return mBalancePaint; 
        } 
    }
    
    protected void drawRect(Canvas canvas, float x, int index){
        float increaseHeight = mAnimationTimes * mSpeed;
        
        if (mCursor == null || mAgilityColumnIndex == NO_COLUMN_INDEX
                || mEmotionColumnIndex == NO_COLUMN_INDEX) {
            return;
        }
        
        if (mCursor.moveToPosition(index)) {
            int agility = Utility.transformAgility(mCursor.getInt(mAgilityColumnIndex));
            if (!mShowAnimation) {
                canvas.drawRect(x, mRectBottomY - agility * mRectUnitHeight, x
                        + mObjectWidth, mRectBottomY, getPaint(getLevel(index)));
            } else {
                canvas.drawRect(x, mRectBottomY - Math.min(increaseHeight, agility) * mRectUnitHeight,
                        x + mObjectWidth, mRectBottomY, getPaint(getLevel(index)));
            }
        }
    }
}
