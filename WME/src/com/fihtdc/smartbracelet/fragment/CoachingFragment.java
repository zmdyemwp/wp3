package com.fihtdc.smartbracelet.fragment;

import java.util.Timer;
import java.util.TimerTask;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.activity.CoachingActivity;
import com.fihtdc.smartbracelet.activity.SummaryActivity;
import com.fihtdc.smartbracelet.entity.LevelParameter;
import com.fihtdc.smartbracelet.service.BLEService;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.LogApp;
import com.fihtdc.smartbracelet.util.Utility;
import com.fihtdc.smartbracelet.view.AnimatedImageView;
import com.fihtdc.smartbracelet.view.HeartRateView;
import com.fihtdc.smartbracelet.view.PrepareLayout;
import com.fihtdc.smartbracelet.view.StatusView;
import com.yl.ekgrr.EkgRR;

public class CoachingFragment extends FlingFragment {
    private static final String TAG = "CoachingFragment";

    private static final int MESSAGE_HIDE_BUBBLE = 101;
    private static final int MESSAGE_UPDATEVIEW = 102;

    private LevelParameter mLevelParams = null;
    private int mOrangeColor;
    private int mOrangeDisableColor;
    
    private PrepareLayout mPrepareLayout;
    private TextView mCycleView;
    private TextView mHitView;
    private TextView mBPMView;
    private ImageView mPrepareDarkWave;
    private HeartRateView mHeartRateView;
    private AnimatedImageView mWhaleView;
    private AnimatedImageView mBubbleView;
    private StatusView mStatusView;

    private String mLevel;
    private int mCycleTotal;
    private int mCycleSurplus;
    private int mHitNum;
    private int mInhaleTime;     //sec
    private int mInhaleHoldTime; //sec
    private int mExhaleTime;     //sec
    private int mExhaleHoldTime = 1; //sec (fixed) 

    ObjectAnimator mPrepareDarkAnim;
    ObjectAnimator mWhaleAnim;
    
    float mWhaleTranslateHeight; //Max height
    float mWhaleTranslateY; //Really translate height
    float mWhaleAnimTime;
    
    float mHeartRateViewBottom;

    boolean mHasEnterSummary = false;
    int mBPMTime;
    int mBPMShowValue;

    Timer mTimer;
    TimerTask mTimerTask;

    int mCurrStatus = -1;
    
    //YL 130913
    long lastUpdateMeasure;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLevelParams = getArguments().getParcelable(Constants.KEY_LEVEL_PARAMETER);

        if (mLevelParams != null) {
            mInhaleTime = mLevelParams.getInhale();
            mInhaleHoldTime = mLevelParams.getHold();
            mExhaleTime = mLevelParams.getOuthale();
            mCycleTotal = mLevelParams.getTimes();
            mLevel = mLevelParams.getLevel();
        }

        mCycleSurplus = mCycleTotal;
        mOrangeColor = mContext.getResources().getColor(R.color.orange_color);
        mOrangeDisableColor = mContext.getResources().getColor(R.color.orange_disable_color);
        mWhaleTranslateHeight = mContext.getResources()
                .getDimension(R.dimen.whale_translate_height);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle onSavedInstanceState) {
        return inflater.inflate(R.layout.coaching, null);
    }

    @Override
    public void onViewCreated(View view, Bundle onSavedInstanceState) {
        ((CoachingActivity) mActivity).initValues();
        
        mHasEnterSummary = false;
        mBPMTime = Constants.BPM_UPDATE_TIME;;
        
        initView(view);

        //To start count down timer
        super.onViewCreated(view, onSavedInstanceState);
    }

    private void initView(View view) {
        mPrepareLayout = (PrepareLayout)view.findViewById(R.id.prepare);
        mPrepareDarkWave = (ImageView) view.findViewById(R.id.wave_above_dark);
        mHeartRateView = (HeartRateView) view.findViewById(R.id.heartRateVieww);
        mHeartRateView.setStartDraw(false);
        mCycleView = (TextView) view.findViewById(R.id.cycle_view);
        mHitView = (TextView) view.findViewById(R.id.hit_view);
        mBPMView = (TextView) view.findViewById(R.id.bpm_view);
        mBPMShowValue = 0;
        updateViewValue(mBPMView, mBPMShowValue);
        mWhaleView = (AnimatedImageView) view.findViewById(R.id.whale_view);
        mBubbleView = (AnimatedImageView) view.findViewById(R.id.bubble_view);
        mStatusView = (StatusView) view.findViewById(R.id.measure_status);
        mStatusView.setType(Constants.TYPE_COACHING);
        
        mPrepareLayout.setTimeoutListener(new PrepareLayout.onCalmTimeOut() {
            @Override
            public void onTimeOut() {
                if (!mHasEnterBack) {
                    mHasEnterBack = true;
                    BLEService mBLEService = ((CoachingActivity)mActivity).getBLEService();
                    if (mBLEService != null) {
                        mBLEService.reSetWMEState(true);
                    }
                    
                    mActivity.setResult(RESULT_SHOW_TIMEOUT_DIALOG);
                    mActivity.finish();
                }
            }
        });
        addOnGlobalLayoutListener(mPrepareDarkWave);
    }
    
    private int getTotalSeconds() {
        int total = mInhaleTime + mInhaleHoldTime + mExhaleTime + mExhaleHoldTime;
        return total * mCycleTotal;
    }

    @Override
    public void onActivityGlobalLayout() {
        super.onActivityGlobalLayout();
        mHeartRateViewBottom = mHeartRateView.getBottom();
        EkgRR.initBreathParam(mInhaleTime, mInhaleHoldTime, mExhaleTime, mExhaleHoldTime);
        startTimer();
    }

    private void doDarkWaveAnimation() {
        if (!Utility.isFragmentLive(this)) {
            return;
        }

        PropertyValuesHolder pvhY1 = PropertyValuesHolder.ofFloat("y", mPrepareDarkWave.getTop());
        PropertyValuesHolder pvhY2 = PropertyValuesHolder.ofFloat("y", mScreenHeight);
        mPrepareDarkAnim = ObjectAnimator.ofPropertyValuesHolder(mPrepareDarkWave, pvhY1, pvhY2)
                .setDuration(1000);

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
                if (Utility.isFragmentLive(CoachingFragment.this)) {
                    mPrepareDarkWave.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        
        mPrepareDarkAnim.addUpdateListener(new AnimatorUpdateListener() {
            
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mPrepareDarkWave.getY() >= mHeartRateViewBottom) {
                    if (mHeartRateView.getStartDraw() == false) {
                        mHeartRateView.setStartDraw(true);
                    }
                }
            }
        });

        mPrepareDarkAnim.start();
    }

    private void doWhaleAnimation() {
        if (!Utility.isFragmentLive(this)) {
            return;
        }

        PropertyValuesHolder pvhY1 = PropertyValuesHolder.ofFloat("translationY",
                mWhaleTranslateY);
        mWhaleAnim = ObjectAnimator.ofPropertyValuesHolder(mWhaleView, pvhY1).setDuration(
                (int)mWhaleAnimTime);

        mWhaleAnim.start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!Utility.isFragmentLive(CoachingFragment.this)) {
                return;
            }
            
            switch (msg.what) {
            case MESSAGE_HIDE_BUBBLE:
                mBubbleView.setVisibility(View.GONE);
                break;
            case MESSAGE_UPDATEVIEW:
                updateView();
                break;
            default:
                break;
            }
        }

    };

    private void showBubble() {
        if (!Utility.isFragmentLive(this)) {
            return;
        }

        mBubbleView.setVisibility(View.VISIBLE);
        mBubbleView.setImageResource(R.drawable.icon_bubble);
    }

    @Override
    public void doSlideRightAction() {
        if (mHasEnterSummary  || !mIsFirstCalibrationDone) {
            return;
        }
        //YL 130913
        EkgRR.isCoaching = false;

        mHasEnterSummary = true;
        enterSummaryActivity();
    }

    void startTimer() {
        stopTimer();

        if (Utility.isFragmentLive(this)) {
            mTimer = new Timer();

            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (Utility.isFragmentLive(CoachingFragment.this)) {
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
                        doDarkWaveAnimation();
                        mIsFirstCalibrationDone = true;
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

    private void enterSummaryActivity() {
        Intent intent = new Intent(mContext, SummaryActivity.class);
        intent.putExtra(Constants.TYPE_EXTRA, Constants.TYPE_COACHING);
        intent.putExtra(Constants.KEY_LEVEL_CLASS, mLevel);
        intent.putExtra(Constants.KEY_CYCLE_TIME, mCycleTotal);
        intent.putExtra(Constants.KEY_MEASURE_TIME, getTotalSeconds());
        intent.putExtra(Constants.KEY_HIT_TIMES, mHitNum);
        
        if (mContext != null) {
            mContext.startActivity(intent);
            ((Activity) mContext).finish();
        }

    }
    
    
    private void updateMeasureView() {
    	//YL 130913  MeasureView should be updated when the status is "in progress" or "signal unstable".
        if (mCurrStatus == EkgRR.STATUS_IN_PROGRESS || mCurrStatus == EkgRR.STATUS_SIGNAL_UNSTABLE) {
            /*
             * YL 130913 The function callback time is not precisely 0.1 sec, so
             * get callback time by System Time.
             */
            float callBackTime = 0;
            if (lastUpdateMeasure < 0) {
                callBackTime = Constants.ECG_TIME_CALLBACK / 1000.0f;
            } else {
                callBackTime = (System.currentTimeMillis() - lastUpdateMeasure) / 1000.0f;
            }
            lastUpdateMeasure = System.currentTimeMillis();
            // out of bound
            if (callBackTime >= 0.2f) {
                callBackTime = Constants.ECG_TIME_CALLBACK / 1000.0f;
            }

            EkgRR.doBreathGuiding(callBackTime);

            float bGuideTime = EkgRR.getBreathStatusTime();
            switch (EkgRR.getBreathStatus()) 
            {
            case EkgRR.BREATH_STATUS_NONE:
                if (EkgRR.isBreathCycleFinished) {
                    mCycleSurplus--;

                    // Reset translateY
                    mWhaleTranslateY = 0;
                    mWhaleView.setTranslationY(mWhaleTranslateY);

                    // Show bubble
                    showBubble();
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE_BUBBLE, 1000);
                    }

                    // check if hits, add
                    if (EkgRR.isBreathHit) {
                        mHitNum++;
                        // reset flag
                        EkgRR.isBreathHit = false;
                    }

                    // reset flag
                    EkgRR.isBreathCycleFinished = false;
                }
                break;
            case EkgRR.BREATH_STATUS_INHALE:
                //If whale animation not running, do it
                if (mWhaleAnim == null || !mWhaleAnim.isRunning()) {
                    mWhaleAnimTime = 1000 * (mInhaleTime - bGuideTime);
                    mWhaleTranslateY = -mWhaleTranslateHeight;
                    doWhaleAnimation();
                }
                break;
            case EkgRR.BREATH_STATUS_INHALEHOLD:
                //Set whale image
                mWhaleTranslateY = -mWhaleTranslateHeight;
                mWhaleView.setTranslationY(mWhaleTranslateY);
                
                // if inhale pass, show whale water animation
                if (EkgRR.isCoachingInhalePass) {
                    mWhaleView.setImageLevel(1);
                    // reset flag
                    EkgRR.isCoachingInhalePass = false;
                }
                break;
            case EkgRR.BREATH_STATUS_EXHALE:
                mWhaleView.setImageLevel(0);
                
                //If whale animation not running, do it
                if (mWhaleAnim == null || !mWhaleAnim.isRunning()) {
                    mWhaleTranslateY = 0;
                    mWhaleAnimTime = 1000 * ( mExhaleTime - bGuideTime);
                    doWhaleAnimation();
                }
                break;
            }
        } else {
            if (mWhaleAnim != null && mWhaleAnim.isRunning()) {
                mWhaleAnim.cancel();
            }
        }

        // Update cycle
        updateViewValue(mCycleView, mCycleSurplus);

        // Update hit
        updateViewValue(mHitView, mHitNum);

        // Update BPM
        mBPMTime -= Constants.ECG_TIME_CALLBACK;
        if (mBPMTime <= 0) {
            mBPMShowValue = EkgRR.hf_GetInstantHeartBeat();
            mBPMTime = Constants.BPM_UPDATE_TIME;
        }
        updateViewValue(mBPMView, mBPMShowValue);

        // update status
        mStatusView.updateView(mCurrStatus);

        if (mCycleSurplus <= 0) {
        	LogApp.Logd(TAG,"EkgRR.eRR_ei : "+EkgRR.eRR_ei);
            doSlideRightAction();
        }

    }

    private void updateViewValue(TextView textView, int value) {
        String string = String.valueOf(value);
        if (value < 10) {
            string = "00" + string;
        } else if (value < 100) {
            string = "0" + string;
        }

        if (isStatusLive(mCurrStatus)) {
            textView.setText(Utility.formatStringColor(string, mOrangeColor));
        } else {
            textView.setText(Utility.formatStringColor(string, mOrangeDisableColor));
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
            mHandler.removeMessages(MESSAGE_HIDE_BUBBLE);
            mHandler.removeMessages(MESSAGE_UPDATEVIEW);
            mHandler = null;
        }

        if (mPrepareDarkAnim != null) {
            mPrepareDarkAnim.cancel();
        }

        if (mWhaleAnim != null) {
            mWhaleAnim.cancel();
        }
    }
}
