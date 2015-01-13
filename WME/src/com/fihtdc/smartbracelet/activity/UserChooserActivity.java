package com.fihtdc.smartbracelet.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.fragment.UserChooserFragment;

public class UserChooserActivity extends ServiceConnectActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_stack);

        mLeft.setImageResource(R.drawable.ic_menu_back);
        mRight.setImageResource(R.drawable.ic_menu_home);
        
        if (savedInstanceState == null) {
            Fragment newFragment = getFragmentManager().findFragmentByTag("UserChooserFragment");
            if (newFragment == null) {
                newFragment = new UserChooserFragment();
            }
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.single_fragment, newFragment, "UserChooserFragment").commit();
        }
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
