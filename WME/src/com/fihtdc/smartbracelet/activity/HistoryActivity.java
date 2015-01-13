package com.fihtdc.smartbracelet.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.fragment.HistoryChooserFragment;

public class HistoryActivity extends CustomActionBarActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_stack);
        mLeft.setImageResource(R.drawable.ic_menu_back);
        mRight.setImageResource(R.drawable.ic_menu_home);
        
        if (savedInstanceState == null) {
            Fragment newFragment = getFragmentManager().findFragmentByTag("HistoryChooserFragment");
            if (newFragment == null) {
                newFragment = new HistoryChooserFragment();
            }
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.single_fragment, newFragment, "HistoryChooserFragment").commit();
        }
    }
    
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        } else {
            getFragmentManager().popBackStack();
            removeCurrentFragment();
        }
    }
    
    public void removeCurrentFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment currentFrag = getFragmentManager().findFragmentById(R.id.single_fragment);

        if (currentFrag != null){
            transaction.remove(currentFrag);
            //LogApp.Logd(TAG, "fragName "+currentFrag.getClass().getSimpleName());
        }

        transaction.commit();
    }

    @Override
    public void onClickLeft() {
        onBackPressed();
    }


}
