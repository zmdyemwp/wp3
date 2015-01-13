package com.fihtdc.smartbracelet.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.fragment.GuestProfileFragment;

public class GuestProfileActivity extends ServiceConnectActivity{
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_stack);
        mLeft.setImageResource(R.drawable.ic_menu_back);
        mRight.setImageResource(R.drawable.ic_menu_home);
        if (savedInstanceState == null) {
            Fragment newFragment = getFragmentManager().findFragmentByTag("GuestProfileFragment");
            if (newFragment == null) {
                newFragment = new GuestProfileFragment();
            }
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Intent intent = getIntent();
            if (intent != null) {
                newFragment.setArguments(intent.getExtras());
            }
            ft.replace(R.id.single_fragment, newFragment, "GuestProfileFragment");
            ft.commit();
        }
    }
}
