package com.fihtdc.smartbracelet.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.activity.ServiceConnectActivity.OnServiceConnectedChangeListener;
import com.fihtdc.smartbracelet.entity.LevelParameter;
import com.fihtdc.smartbracelet.fragment.CoachingFragment;
import com.fihtdc.smartbracelet.service.BLEService;
import com.fihtdc.smartbracelet.util.Constants;
import com.yl.ekgrr.EkgRR;

public class CoachingActivity extends FlingActivity implements OnServiceConnectedChangeListener {
    private BLEService mBLEService;
    private int mCycleTotal;
    private int mInhaleTime; // sec
    private int mInhaleHoldTime; // sec
    private int mExhaleTime; // sec
    private int mExhaleHoldTime = 1; // sec (fixed)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            LevelParameter levelParams = intent.getParcelableExtra(Constants.KEY_LEVEL_PARAMETER);
            if (levelParams != null) {
                mInhaleTime = levelParams.getInhale();
                mInhaleHoldTime = levelParams.getHold();
                mExhaleTime = levelParams.getOuthale();
                mCycleTotal = levelParams.getTimes();
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_stack);

        mLeft.setImageResource(R.drawable.ic_menu_back);
        mRight.setImageResource(R.drawable.ic_menu_home);

        if (savedInstanceState == null) {
            Fragment newFragment = getFragmentManager().findFragmentByTag("CoachingFragment");
            if (newFragment == null) {
                newFragment = new CoachingFragment();
            }

            if (intent != null) {
                newFragment.setArguments(intent.getExtras());
            }
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.single_fragment, newFragment, "CoachingFragment").commit();
        }
    }

    @Override
    public void onServiceConnected() {
        EkgRR.an_ResetEkgBuf();
        EkgRR.an_ResetEkgResults();
        EkgRR.isCoaching = true;
        EkgRR.isECGStarted = false;

        mBLEService = getBLEService();
        if (mBLEService != null) {
            mBLEService.NotifyECGStart(true, getTotalSeconds());
        }
    }

    @Override
    public void onServiceDisconnected() {
        // TODO Auto-generated method stub

    }

    private int getTotalSeconds() {
        int total = mInhaleTime + mInhaleHoldTime + mExhaleTime + mExhaleHoldTime;
        return total * mCycleTotal;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        mBLEService = getBLEService();
        if (mBLEService != null) {
            mBLEService.NotifyECGStart(false);
        }
        
        EkgRR.isECGStarted = false;
        EkgRR.isCoaching = false;
    }
}
