package com.fihtdc.smartbracelet.fragment;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.activity.MeasureActivity;
import com.fihtdc.smartbracelet.activity.SummaryActivity;
import com.fihtdc.smartbracelet.service.BLEService;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.LogApp;
import com.fihtdc.smartbracelet.util.Utility;
import com.fihtdc.smartbracelet.view.HeartRateView;
import com.fihtdc.smartbracelet.view.PrepareLayout;
import com.fihtdc.smartbracelet.view.StatusView;
import com.yl.ekgrr.EkgRR;

public class MeasureFragment extends FlingFragment {
    private static final String TAG = "MeasureFragment";

    private static final int FISH_ANIM_TIME = 2500; // ms
    private static final int FISH_ANIM_COUNT_TIME = 5000; // ms
    private static final int FISH_SLIDE_POINT_NUM = 4;

    private static final int MESSAGE_UPDATEVIEW = 1;

    // breathing wave
    private static final int INHALE_TIME = 5; // sec
    private static final int EXHALE_TIME = 6; // sec
    
    private static final int WAVE_ANIM_TIME = INHALE_TIME * 1000; // ms
    
    PrepareLayout mPrepareLayout;
    RelativeLayout mPrapareWaveLayout;
    ImageView mPrepareDark;
    ImageView mPrepareLight;
    
    TextView mTimerValue;
    TextView mBPMValue;
    ImageView mWaveBottom;
    ImageView mWaveAbove;
    ImageView mFish;
    StatusView mStatusView;
    HeartRateView mHeartRateView;

    float mHeartRateViewBottom;
    
    Timer mTimer;
    TimerTask mTimerTask;

    int mOrangeColor;
    int mOrangeDisableColor;
    int mBPM;
    int mBPMTime;
    int mBPMShowValue;

    float mPrepareLightMarginTop;

    ObjectAnimator mPrepareLightAnim;
    ObjectAnimator mPrepareDarkAnim;
    ObjectAnimator mFishAnim;
    ObjectAnimator mWaveAnim;

    boolean mHasEnterSummary = false;
    boolean mFishAnimStart = false;

    boolean mIsWaveUp = false;
    long mPlayTime;
    float mWaveTranslateHeight; // Max translate height
    float mBreathPercentage;
    
    long mFishAnimPlayTime;
    long mFishAnimDuration = FISH_ANIM_TIME;
    float[] mFishXValues;
    float[] mFishYValues;
    float mFishXGap;
    int mFishStartX;
    int mFishStartY;
    int mFishAnimCountDown = FISH_ANIM_COUNT_TIME;

    int mCurrStatus = -1;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case MESSAGE_UPDATEVIEW:
                updateView();
                break;

            default:
                break;
            }
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOrangeColor = mContext.getResources().getColor(R.color.orange_color);
        mOrangeDisableColor = mContext.getResources().getColor(R.color.orange_disable_color);
        mWaveTranslateHeight = mContext.getResources().getDimension(R.dimen.wave_translate_height);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.measure, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ((MeasureActivity) mActivity).initValues();
        
        mHasEnterSummary = false;
        mBPMTime = Constants.BPM_UPDATE_TIME;
        mIsFirstCalibrationDone = false;
        mCurrStatus = -1;
        
        mPrepareLayout = (PrepareLayout)view.findViewById(R.id.prepare);
        mPrapareWaveLayout = (RelativeLayout) view.findViewById(R.id.prepare_wave_layout);
        mPrepareLight = (ImageView) view.findViewById(R.id.wave_prepare_light);
        mPrepareDark = (ImageView) view.findViewById(R.id.wave_prepare_dark);
        
        mWaveBottom = (ImageView) view.findViewById(R.id.wave_bottom);
        mWaveAbove = (ImageView) view.findViewById(R.id.wave_above);
        mFish = (ImageView) view.findViewById(R.id.fish);
        mHeartRateView = (HeartRateView) view.findViewById(R.id.heartRateVieww);
        mHeartRateView.setStartDraw(false);
        mTimerValue = (TextView) view.findViewById(R.id.time_label);
        mBPMValue = (TextView) view.findViewById(R.id.bpm_label);
        mBPMShowValue = 0;
        updateBPMView(mBPMShowValue);
        
        mStatusView = (StatusView) view.findViewById(R.id.measure_status);
        mStatusView.setType(Constants.TYPE_MEASURE);
        
        mPrepareLayout.setTimeoutListener(new PrepareLayout.onCalmTimeOut() {
            @Override
            public void onTimeOut() {
                if (!mHasEnterBack) {
                    mHasEnterBack = true;

                    BLEService mBLEService =  ((MeasureActivity)mActivity).getBLEService();
                    if (mBLEService != null) {
                        mBLEService.reSetWMEState(true);
                    }

                    mActivity.setResult(RESULT_SHOW_TIMEOUT_DIALOG);
                    mActivity.finish();
                }
            }
        });
        addOnGlobalLayoutListener(mHeartRateView);
        
        //To start count down timer
        super.onViewCreated(view, savedInstanceState);
    }
    
    @Override
    public void onActivityGlobalLayout() {
        mPrepareLightMarginTop = mWaveBottom.getTop();
        mHeartRateViewBottom = mHeartRateView.getBottom();
        initFishAnimation();
        startTimer();
        super.onActivityGlobalLayout();
    }

    private void doLightWaveAnimation() {
        if (!Utility.isFragmentLive(this)) {
            return;
        }

        PropertyValuesHolder pvhY1 = PropertyValuesHolder.ofFloat("y", mPrepareLightMarginTop
                - getResources().getDimension(R.dimen.measure_wave_above_margin_top));

        mPrepareLightAnim = ObjectAnimator.ofPropertyValuesHolder(mPrapareWaveLayout, pvhY1);
        mPrepareLightAnim.setDuration(350);
        mPrepareLightAnim.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (Utility.isFragmentLive(MeasureFragment.this)) {
                    mPrepareLight.setVisibility(View.GONE);
                    doDarkWaveAnimation();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }
        });

        mPrepareLightAnim.start();
    }

    private void doDarkWaveAnimation() {
        if (!Utility.isFragmentLive(this)) {
            return;
        }

        Log.e(TAG, "mWaveAbove.getHeight() "+mWaveAbove.getHeight());
        Log.e(TAG, "mScreenHeight "+mScreenHeight);
        Log.e(TAG, "mScreenHeight - mWaveAbove.getTop() "+(mScreenHeight - mWaveAbove.getTop()));
        Log.e(TAG, "mHeartRateViewBottom "+mHeartRateViewBottom);
        
        PropertyValuesHolder pvhY1 = PropertyValuesHolder.ofFloat("translationY",
                mScreenHeight - mPrepareDark.getTop()/* mWaveAbove.getHeight()*/);
        mPrepareDarkAnim = ObjectAnimator.ofPropertyValuesHolder(mPrepareDark, pvhY1).setDuration(
                650);

        mPrepareDarkAnim.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (Utility.isFragmentLive(MeasureFragment.this)) {
                    mPrepareDark.setVisibility(View.GONE);
                }
                
                if (!mHeartRateView.getStartDraw()) {
                    mHeartRateView.setStartDraw(true);
                }
                
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        
        mPrepareDarkAnim.addUpdateListener(new AnimatorUpdateListener() {
            
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.e(TAG, "mPrepareDark.getY() "+mPrepareDark.getY());

                if (mPrepareDark.getY() >= mHeartRateViewBottom) {
                    if (!mHeartRateView.getStartDraw()) {
                        mHeartRateView.setStartDraw(true);
                    }
                }
            }
        });

        mPrepareDarkAnim.start();
    }

    private void doWaveAnimation() {
        if (!Utility.isFragmentLive(this)) {
            return;
        }

        float moveY;
        if (mIsWaveUp) {
            moveY =  -mWaveTranslateHeight;
        } else {
            moveY = 0;
        }
        
        PropertyValuesHolder pvhY2 = PropertyValuesHolder.ofFloat("translationY", moveY);
        mWaveAnim = ObjectAnimator.ofPropertyValuesHolder(mWaveBottom, pvhY2).setDuration(
               mPlayTime);
        mWaveAnim.start();
    }

    private void initFishAnimation() {
        float centerX = mScreenWidth / 2;
        mFishStartX = mScreenWidth * 3 / 4 ;
        mFish.setX(mFishStartX);
        
        mFishXGap = (mFishStartX - centerX) / FISH_SLIDE_POINT_NUM;
        mFishStartY = mFish.getTop();

        int pointNum = getFishAnimPointNum();
        mFishXValues = new float[pointNum];
        mFishYValues = new float[pointNum];

        float offsetY = getResources().getDimension(R.dimen.fish_jump_height);
        
        //y = rate * Maht.pow(x - centerX, 2) + mFishStartY - offset
        float rate = (float) (offsetY / Math.pow(mFishStartX - centerX, 2));
        for (int i = 1; i < pointNum; i++) {
            mFishXValues[i] = mFishStartX - mFishXGap * i;
            mFishYValues[i] = (float) (rate * Math.pow(mFishXValues[i] - centerX, 2) +mFishStartY
                    -  offsetY) ;
        }
    }

    private int getFishAnimPointNum() {
        return 2 * FISH_SLIDE_POINT_NUM + 1;
    }

    private void doFishAnimation() {
        if (!Utility.isFragmentLive(this)) {
            return;
        }

        if (mFishXValues == null || mFishYValues == null) {
            return;
        }

        int remindIndex = (int) (mFishStartX - mFish.getX()) / (int) mFishXGap + 1;

        int pointNum = getFishAnimPointNum() - remindIndex;
        if (pointNum <= 0) {
            return;
        }

        float[] fishXValues = new float[pointNum];
        float[] fishYValues = new float[pointNum];

        fishXValues[0] = mFish.getX();
        fishYValues[0] = mFish.getY();
        for (int i = 1; i < pointNum; i++) {
            fishXValues[i] = mFishXValues[remindIndex + i];
            fishYValues[i] = mFishYValues[remindIndex + i];
        }

        PropertyValuesHolder pvhX1 = PropertyValuesHolder.ofFloat("x", fishXValues);
        PropertyValuesHolder pvhY1 = PropertyValuesHolder.ofFloat("y", fishYValues);
        mFishAnim = ObjectAnimator.ofPropertyValuesHolder(mFish, pvhY1, pvhX1).setDuration(
                mFishAnimDuration);
        mFishAnim.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mFishAnimDuration -= mFishAnimPlayTime;
                if (mFishAnimDuration <= 0) {
                    mFishAnimDuration = FISH_ANIM_TIME;
                    mFishAnimStart = false;
                    mFish.setX(mFishStartX);
                    mFish.setY(mFishStartY);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }
        });

        mFishAnim.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mFishAnimPlayTime = animation.getCurrentPlayTime();
            }
        });

        mFishAnimStart = true;
        mFishAnim.start();
    }

    void startTimer() {
        stopTimer();
        if (Utility.isFragmentLive(this)) {
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (Utility.isFragmentLive(MeasureFragment.this)) {
                        mHeartRateView.draw();
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(MESSAGE_UPDATEVIEW);
                        }
                    } else {
                        stopTimer();
                    }
                }
            };

            mTimer.scheduleAtFixedRate(mTimerTask, 0, Constants.ECG_TIME_CALLBACK);
        }
    }
    
    private void stopTimer(){
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    @Override
    public void doSlideRightAction() {
        if (mHasEnterSummary || !mIsFirstCalibrationDone) {
            return;
        }
        mHasEnterSummary = true;

        Intent intent = new Intent(mContext, SummaryActivity.class);
        Bundle bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putInt(Constants.KEY_MEASURE_TIME, MeasureActivity.MEASURE_TIME);
        intent.putExtras(bundle);
        
        if (mContext != null) {
            mContext.startActivity(intent);
        }

        if (mActivity != null) {
            mActivity.finish();
        }
    }

    public void updateView() {
        if (mContext != null && this.isAdded() && !this.isDetached()) {
            LogApp.Logd(TAG, "EkgRR.getECGstatus() "+EkgRR.getECGstatus());

            if (mCurrStatus != EkgRR.getECGstatus()) {
                mCurrStatus = EkgRR.getECGstatus();
                switch (mCurrStatus) {
                case EkgRR.STATUS_NONE:
                    break;
                case EkgRR.STATUS_SIGNAL_UNSTABLE:
                    break;
                case EkgRR.STATUS_CLIBRATING:
                    break;
                case EkgRR.STATUS_COUNTING_DOWN:
                    break;
                case EkgRR.STATUS_IN_PROGRESS:
                    if (!mIsFirstCalibrationDone) {
                        doLightWaveAnimation();
                        mIsFirstCalibrationDone = true;
                    } else {
                        onAnimResume();
                    }
                    break;
                }
            }

            if (!mIsFirstCalibrationDone) {
                //Show prepare layout
                mPrepareLayout.setVisibility(View.VISIBLE);
                mPrepareLayout.updateView(mCurrStatus);
            } else {
                //Show measure layout
                mPrepareLayout.setVisibility(View.GONE);
                updateMeasureView();
            }
        }
    }
    
    private void updateMeasureView(){
        // update Timer
        long secs = 3 * 60 - EkgRR.getECGDataCount() / EkgRR.BT_RESOLUTION;
        long minute = secs / 60;
        long sec = secs % 60;

        updateTimeView(minute, sec);

        // update BPM
        mBPMTime -=  Constants.ECG_TIME_CALLBACK;
        if (mBPMTime <= 0) {
            mBPMShowValue = EkgRR.hf_GetInstantHeartBeat();
            mBPMTime = Constants.BPM_UPDATE_TIME;
        }
        updateBPMView(mBPMShowValue);

        // update status
        mStatusView.updateView(mCurrStatus);

        // Set wave Height
        if (mCurrStatus == EkgRR.STATUS_IN_PROGRESS) {
            float callback = Constants.ECG_TIME_CALLBACK / 1000.0f;
            float breathPercentage = Math.abs(EkgRR.edr_getBreathWithCallbackTime(callback,
                    INHALE_TIME, EXHALE_TIME));
            
            if (mBreathPercentage != breathPercentage) {
                LogApp.Logd(TAG, "breathPercentage "+breathPercentage);
                float offset = breathPercentage - mBreathPercentage;
                boolean mTempWaveUp;
                if (offset > 0) {
                    //Move up
                    mTempWaveUp = true;
                } else {
                    //Move down
                    mTempWaveUp = false;
                }

                mBreathPercentage = breathPercentage;
                //Wave move direction changed
                if (mIsWaveUp != mTempWaveUp) {
                    mIsWaveUp = mTempWaveUp;
                    if(mWaveAnim != null && mWaveAnim.isRunning()){
                        mWaveAnim.cancel();
                    }
                
                    if (mTempWaveUp) {
                        mPlayTime = (long) ((1 - mBreathPercentage) * WAVE_ANIM_TIME);
                } else {
                        mPlayTime = (long) (mBreathPercentage * WAVE_ANIM_TIME);
                    }
                    
                    doWaveAnimation();
                }
            }

            if (mBreathPercentage != 0) {
                mFishAnimCountDown = FISH_ANIM_COUNT_TIME;
            } else {
                // Do fish animation
                if (mFishAnimCountDown <= 0) {
                    mFishAnimCountDown = FISH_ANIM_COUNT_TIME;
                    doFishAnimation();
                } else {
                    mFishAnimCountDown -= Constants.ECG_TIME_CALLBACK;
                }
            }
        }else {
            if(mWaveAnim != null && mWaveAnim.isRunning()){
                mWaveAnim.cancel();
            }
        }

        if (secs <= 0) {
            doSlideRightAction();
        }
    }

    private void updateTimeView(long minute, long second) {
        // 00m00s
        String allNumPattern = "[0-9]";
        boolean hasNotZeroNum = false;
        int color = isStatusLive(mCurrStatus) ? mOrangeColor : mOrangeDisableColor;

        String minuteString = String.valueOf(minute);

        if (minute < 0) {
            minute = 3;
        }

        if (second < 0) {
            second = 0;
        }

        if (minute < 10) {
            minuteString = "0" + minuteString;
        }

        String secString = String.valueOf(second);
        if (second < 10) {
            secString = "0" + secString;
        }

        String string = mContext.getString(R.string.time_format, minuteString, secString);
        SpannableString span = new SpannableString(string);

        Pattern change = Pattern.compile(allNumPattern);
        Matcher m = change.matcher(span.toString());
        while (m.find()) {
            char character = string.charAt(m.start());
            if (!"0".equals(String.valueOf(character))) {
                hasNotZeroNum = true;
            }
            if (hasNotZeroNum) {
                span.setSpan(new ForegroundColorSpan(color), m.start(), m.end(),
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }

        mTimerValue.setText(span);
    }

    private void updateBPMView(int value) {
        String string = String.valueOf(value);
        if (value < 10) {
            string = "00" + string;
        } else if (value < 100) {
            string = "0" + string;
        }

        if (isStatusLive(mCurrStatus)) {
            mBPMValue.setText(Utility.formatStringColor(string, mOrangeColor));
        } else {
            mBPMValue.setText(Utility.formatStringColor(string, mOrangeDisableColor));
        }
    }

    private boolean isStatusLive(int status) {
        if (status == EkgRR.STATUS_IN_PROGRESS || status == EkgRR.STATUS_SIGNAL_UNSTABLE) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        stopTimer();

        if (mHandler != null) {
            mHandler.removeMessages(MESSAGE_UPDATEVIEW);
            mHandler = null;
        }

        if (mPrepareLightAnim != null) {
            mPrepareLightAnim.cancel();
        }

        if (mPrepareDarkAnim != null) {
            mPrepareDarkAnim.cancel();
        }

        if (mFishAnim != null) {
            mFishAnim.cancel();
        }

        if (mWaveAnim != null) {
            mWaveAnim.cancel();
        }
    }

    private void onAnimResume() {
        if (Utility.isFragmentLive(this)) {
            // Resume fish animation
            if (mFishAnimStart && mFishAnim != null && !mFishAnim.isRunning()) {
                mFishAnimCountDown = FISH_ANIM_COUNT_TIME;
                doFishAnimation();
            }

            // Resume wave animation
            if (mBreathPercentage != 0) {
                mIsWaveUp = false;
                mPlayTime = (long)(mBreathPercentage * WAVE_ANIM_TIME);
                mBreathPercentage = 0;
                doWaveAnimation();
            }
        }
    }
}
