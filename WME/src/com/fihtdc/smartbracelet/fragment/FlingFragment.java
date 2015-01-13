package com.fihtdc.smartbracelet.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.fihtdc.smartbracelet.activity.FlingActivity;
import com.fihtdc.smartbracelet.activity.FlingActivity.OnSlideLeftRightListener;
import com.fihtdc.smartbracelet.util.Utility;

public class FlingFragment extends CommonFragment implements OnSlideLeftRightListener{
    private static final int TIMEOUT_TIME = 80 * 1000; 
    public static final int RESULT_SHOW_TIMEOUT_DIALOG = 200;
    public static final int RESULT_SHOW_BLUETOOTH_DIALOG = 201;
    
    Context mContext;
    int mScreenWidth;
    int mScreenHeight;
    Activity mActivity;

    CountDownTimer mCountDownTimer;
    
    boolean mIsFirstCalibrationDone = false;
    boolean mHasEnterBack = false;
    
    @Override
    public void onAttach(Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        ((FlingActivity) mContext).setOnSlideLeftRightListener(this);
        initScreenSize();

    }
    @Override
    public void doSlideLeftAction() {
        getActivity().onBackPressed();
    }

    @Override
    public void doSlideRightAction() {

    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHasEnterBack = false;
        startCountDownTimer();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        ((FlingActivity)mContext).setOnSlideLeftRightListener(null);
    }
    
    private void initScreenSize(){
        Point size = Utility.getCurrentWindowSize(mContext);
        mScreenWidth = size.x;
        int contentTop = mActivity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        mScreenHeight = size.y - contentTop;
    }
    
    void startCountDownTimer() {
        if (mCountDownTimer == null) {
            mCountDownTimer = new CountDownTimer(TIMEOUT_TIME , TIMEOUT_TIME) {
                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    if (!mIsFirstCalibrationDone && !mHasEnterBack) {
                        mHasEnterBack = true;
                        mActivity.setResult(RESULT_SHOW_TIMEOUT_DIALOG);
                        mActivity.finish();
                    }
                }
            };
        } else {
            mCountDownTimer.cancel();
        }

        mCountDownTimer.start();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

}
