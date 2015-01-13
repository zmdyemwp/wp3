package com.fihtdc.smartbracelet.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.fihtdc.smartbracelet.R;
import com.yl.ekgrr.EkgRR;
/*
 * The view to draw heart rate dynamically
 */
public class HeartRateView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "HeartRateView";
    
    private SurfaceHolder mHolder;
    
    Paint mLinePaint = new Paint();
    Paint mPointPaint = new Paint();
    
    int mLineColor;
    int mQPointColor;
    int mRPointColor;
    int mSPointColor;
    int mNPointColor;
    int mBackgroundColor;
    
    boolean mStartDraw = true;

    public HeartRateView(Context context) {
        this(context, null);
    }
    
    public HeartRateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeartRateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HeartRateView);
        mLineColor = a.getColor(R.styleable.HeartRateView_lineColor, 0xFFFFFFFF);
        mQPointColor = a.getColor(R.styleable.HeartRateView_QPointColor, 0xFF0000FF);
        mRPointColor = a.getColor(R.styleable.HeartRateView_RPointColor, 0xFFFF0000);
        mSPointColor = a.getColor(R.styleable.HeartRateView_SPointColor, 0xFFFFFF00);
        mNPointColor = a.getColor(R.styleable.HeartRateView_SPointColor, 0xFFBEBEBE);
        mBackgroundColor = a.getColor(R.styleable.HeartRateView_backgroundColor, getResources().getColor(R.color.measure_heartview_bk_color)); 
        
        a.recycle();

        init(context);
    }
    
    private void init(Context context){
        mHolder = this.getHolder();
        mHolder.addCallback(this);
        
        //Set surface view background
        this.setZOrderOnTop(true);  
        mHolder.setFormat(PixelFormat.TRANSPARENT); 
        
        mLinePaint.setColor(mLineColor);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(4);
        
        mPointPaint.setColor(mQPointColor);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setStrokeWidth(8);
    }

    private void drawContent(Canvas canvas) {
        float frame_width = canvas.getWidth();
        float frame_height = canvas.getHeight();

        // draw 5 sec window
        int recs = (int) (EkgRR.BT_RESOLUTION * EkgRR.ECG_DRAW_WINDOW_SIZE);
        float x_units = (float) (1.0 / (float) recs) * frame_width;

        float scaleFactor = 1.0f;
        float startPt_x = 0.0f;
        float startPt_y = 0.0f;

        float QRA_avg = EkgRR.getQRAmplitudeAvg();
        if (QRA_avg < 15) {
            QRA_avg = 15;
        } else if (QRA_avg > 100) {
            QRA_avg = 100;
        }
        scaleFactor = ((frame_height) / QRA_avg) / 3;
        startPt_y = frame_height * 3.0f / 5.0f;

        float currPt_x = 0.0f;
        float currPt_y = 0.0f;
        float prevPt_x = 0.0f;
        float prevPt_y = 0.0f;

        //LogApp.Logd(TAG,"eiS:"+ EkgRR.eiS);
        //LogApp.Logd(TAG,"eiE:"+ EkgRR.eiE);

        for (int i = EkgRR.eiS, j = 0; i < EkgRR.eiE; i++, j++) {
            //LogApp.Logd(TAG,"EkgRR.ekgBuf[i]:"+ EkgRR.ekgBuf[i]);

            currPt_x = startPt_x + (float) j * x_units;
            currPt_y = startPt_y - ((float) EkgRR.ekgBuf[i] * scaleFactor);

            if (i != EkgRR.eiS) {
                float[] line = { prevPt_x, prevPt_y, currPt_x, currPt_y };
                canvas.drawLines(line, mLinePaint);
            } 
            
            if (EkgRR.rrBuf[i] != 0) {
                if (EkgRR.rrBuf[i] == 1) {
                    // Q
                    mPointPaint.setColor(mQPointColor);
                } else if (EkgRR.rrBuf[i] == 2) {
                    // S
                    mPointPaint.setColor(mSPointColor);
                } else if (EkgRR.rrBuf[i] == 3) {
                    // Near Peak
                    mPointPaint.setColor(mNPointColor);
                } else if (EkgRR.rrBuf[i] < 0) {
                    // R without normal RRI
                    mPointPaint.setColor(mRPointColor);
                } else {
                    // R with RRI
                    mPointPaint.setColor(mRPointColor);
                }

                canvas.drawCircle(currPt_x, currPt_y, 8, mPointPaint);
            }
            
            prevPt_x = currPt_x;
            prevPt_y = currPt_y;
        }
    }
    
    public void draw() {
        if (mHolder == null) {
            return;
        }
        
        Canvas canvas = mHolder.lockCanvas();
        if (canvas == null) {
            return;
        }
        
        canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
        
        if ((EkgRR.eiS <= 0) && (EkgRR.eiE <= 0)) {
            mHolder.unlockCanvasAndPost(canvas);
            return;
        }

        if (mStartDraw) {
            drawContent(canvas);
        }

        mHolder.unlockCanvasAndPost(canvas);
        
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        draw();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }
    
    public void setStartDraw(boolean startDraw){
        mStartDraw = startDraw;
    }
    
    public boolean getStartDraw(){
        return mStartDraw;
    }
}
