package com.fihtdc.smartbracelet.view;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.util.Constants;
import com.yl.ekgrr.EkgRR;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PrepareLayout extends RelativeLayout{
    private static final int CALM_DELAY = 2 * 1000;    //ms
    
    private static final int CALM_TIMEOUT = 48 * 1000;
    LinearLayout mPrepareLayout;
    RelativeLayout mPromptLayout;
    RelativeLayout mCalmLayout;
    RelativeLayout mMeasureStartLayout;
    TextView mPromptText;
    TextView mSecondText;
    
    //ECG
    int mCurrStatus;
    int mTimeBeforeCalmFace;
    boolean mIsFirstPrompt;
    
    private long CalmTime;
    
    public static interface onCalmTimeOut {
        void onTimeOut();
    }
    private onCalmTimeOut mCalmTimeout;
    
    public PrepareLayout(Context context) {
        this(context, null);
    }
    
    public PrepareLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrepareLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mIsFirstPrompt = true;
        mTimeBeforeCalmFace = CALM_DELAY;
        mCurrStatus = -1;
        CalmTime = System.currentTimeMillis();
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        
        mPromptLayout = (RelativeLayout) findViewById(R.id.prompt_layout);
        mPromptText = (TextView)findViewById(R.id.prompt_text);
        mCalmLayout = (RelativeLayout) findViewById(R.id.calm_layout);
        mMeasureStartLayout = (RelativeLayout) findViewById(R.id.measure_start_layout);
        mSecondText = (TextView) findViewById(R.id.second_value);
        updateView(mCurrStatus);
    }
    
    public void setTimeoutListener(onCalmTimeOut calmTimeout){
        mCalmTimeout = calmTimeout;
    }
    
    public void updateView(int status){
        long currentTime = System.currentTimeMillis();
        if (!EkgRR.isECGStarted) {
            if (mTimeBeforeCalmFace <= 0) {
                showCalmLayout();
                checkCalm(currentTime);
            } else {
                CalmTime = currentTime;
                showPrompLayout();
                mTimeBeforeCalmFace -= Constants.ECG_TIME_CALLBACK;
            }
            
        } else {
            // status change
            if (mCurrStatus != EkgRR.getECGstatus()) {
                mCurrStatus = EkgRR.getECGstatus();
                
                switch (mCurrStatus) {
                case EkgRR.STATUS_NONE:
                    if (mTimeBeforeCalmFace <= 0) {
                        mIsFirstPrompt = false;
                        showCalmLayout();
                        checkCalm(currentTime);
                    } else {
                        CalmTime = currentTime;
                        showPrompLayout();
                        mTimeBeforeCalmFace -= Constants.ECG_TIME_CALLBACK;
                    }
                    break;
                case EkgRR.STATUS_SIGNAL_UNSTABLE:
                    CalmTime = currentTime;
                    showPrompLayout();
                    break;
                case EkgRR.STATUS_CLIBRATING:
                    CalmTime = currentTime;
                    showPrompLayout();
                    break;
                case EkgRR.STATUS_COUNTING_DOWN:
                    CalmTime = currentTime;
                    showMeasureLayout();
                    break;
                case EkgRR.STATUS_IN_PROGRESS:
                    CalmTime = currentTime;
                    break;
                }
            } else if (mCurrStatus == EkgRR.STATUS_COUNTING_DOWN) {
                CalmTime = currentTime;
                mSecondText.setText(String.valueOf(EkgRR.getCalCountDownSec()));
            }
        }
    }
    
    
    private void checkCalm(long currentTime){
        if(currentTime - CalmTime > CALM_TIMEOUT){
            //notify to stop WME
            if(null != mCalmTimeout){
                mCalmTimeout.onTimeOut();
            }
        }
    }
    
    private void showPrompLayout(){
        mPromptLayout.setVisibility(View.VISIBLE);
        mCalmLayout.setVisibility(View.GONE);
        if (!mIsFirstPrompt) {
            mPromptText.setText(R.string.prompt_label_better);
        } else {
            mPromptText.setText(R.string.prompt_label);
        }
        mMeasureStartLayout.setVisibility(View.GONE);
    }
    
    private void showCalmLayout(){
        mPromptLayout.setVisibility(View.GONE);
        mCalmLayout.setVisibility(View.VISIBLE);
        mMeasureStartLayout.setVisibility(View.GONE);
    }
    
    private void showMeasureLayout(){
        mPromptLayout.setVisibility(View.GONE);
        mCalmLayout.setVisibility(View.GONE);
        mSecondText.setText(String.valueOf(EkgRR.getCalCountDownSec()));
        mMeasureStartLayout.setVisibility(View.VISIBLE);
    }


}
