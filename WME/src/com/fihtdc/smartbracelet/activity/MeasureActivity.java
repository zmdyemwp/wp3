package com.fihtdc.smartbracelet.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.fragment.MeasureFragment;
import com.fihtdc.smartbracelet.service.BLEService;
import com.yl.ekgrr.EkgRR;

public class MeasureActivity extends FlingActivity {
    public static final int MEASURE_TIME = 180; // sec
    private BLEService mBLEService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_stack);

        mLeft.setImageResource(R.drawable.ic_menu_back);
        mRight.setImageResource(R.drawable.ic_menu_home);
        
        if (savedInstanceState == null) {
            //fihtdc 2013/10/14 fly.f.ren added for fixed the guest info fail to bundel issue begin
        	Bundle bundle =getIntent().getExtras();
            Fragment newFragment = getFragmentManager().findFragmentByTag("MeasureFragment");
            if (newFragment == null) {
                newFragment = new MeasureFragment();
            }
            if(bundle!=null){
            	newFragment.setArguments(bundle);
            }
            //fihtdc 2013/10/14 fly.f.ren added for fixed the guest info fail to bundel issue begin
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.single_fragment, newFragment, "UserChooserFragment").commit();
        }
    }

    @Override
    public void onServiceConnected() {
        EkgRR.an_ResetEkgBuf();
        EkgRR.an_ResetEkgResults();
        EkgRR.isMeasuring = true;
        EkgRR.isECGStarted = false;
        
        mBLEService = getBLEService();
        if (mBLEService != null) {
            mBLEService.NotifyECGStart(true, MEASURE_TIME);
        }
    }

    @Override
    public void onServiceDisconnected() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        mBLEService = getBLEService();
        if (mBLEService != null) {
            mBLEService.NotifyECGStart(false);
        }
        
        EkgRR.isECGStarted = false;
        EkgRR.isMeasuring = false;
    }
    
}
