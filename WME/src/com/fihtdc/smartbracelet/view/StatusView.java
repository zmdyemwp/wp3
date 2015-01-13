package com.fihtdc.smartbracelet.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.util.Constants;
import com.yl.ekgrr.EkgRR;

public class StatusView extends LinearLayout {
    Context mContext;

    ImageView mStopIcon;
    ImageView mResumeIcon;
    TextView mStatusText;

    int mStatus;
    int mType = Constants.TYPE_MEASURE;

    public StatusView(Context context) {
        this(context, null);
    }

    public StatusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mStopIcon = (ImageView) findViewById(R.id.status_stop_icon);
        mResumeIcon = (ImageView) findViewById(R.id.status_resume_icon);
        mStatusText = (TextView) findViewById(R.id.status_label);
    }
    
    public void setType(int type){
        mType = type;
    }

    public void updateView(int status) {
        if (mStatus != status) {
            mStatus = status;
            switch (mStatus) {
            case EkgRR.STATUS_NONE:
                break;
            case EkgRR.STATUS_SIGNAL_UNSTABLE:
                mStatusText.setText(R.string.signal_unstable);
                break;
            case EkgRR.STATUS_CLIBRATING:
                mStatusText.setText(R.string.hold_retrieve);
                break;
            case EkgRR.STATUS_COUNTING_DOWN:
                mStatusText.setText(mContext.getString(R.string.measure_continue,
                        EkgRR.getCalCountDownSec()));
                break;
            case EkgRR.STATUS_IN_PROGRESS:
                if (Constants.TYPE_MEASURE == mType) {
                    mStatusText.setText(R.string.measuring_label);
                } else {
                    mStatusText.setText(R.string.coaching_label);
                }
                break;
            }
            
            updateIcon(status);
        } else if (mStatus == EkgRR.STATUS_COUNTING_DOWN) {
            mStatusText.setText(mContext.getString(R.string.measure_continue,
                    EkgRR.getCalCountDownSec()));
        }
    }
    
    private void updateIcon(int status){
        if (isStatusLive(status)) {
            mStopIcon.setImageResource(R.drawable.health_measure_schedule_ic_white);
            mResumeIcon.setImageResource(R.drawable.health_measure_schedule_ic_green);
        } else {
            mStopIcon.setImageResource(R.drawable.health_measure_schedule_ic_red);
            mResumeIcon.setImageResource(R.drawable.health_measure_schedule_ic_white);
        }
    }
    
    private boolean isStatusLive(int status) {
        if (status == EkgRR.STATUS_IN_PROGRESS || status == EkgRR.STATUS_SIGNAL_UNSTABLE) {
            return true;
        } else {
            return false;
        }
    }

}
