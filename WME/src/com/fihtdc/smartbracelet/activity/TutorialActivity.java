package com.fihtdc.smartbracelet.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.fragment.TutorialFragment;


public class TutorialActivity extends CustomActionBarActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (savedInstanceState == null) {
            Fragment newFragmeent = getFragmentManager().findFragmentById(android.R.id.content);
            if (newFragmeent == null) {
                newFragmeent = new TutorialFragment();
            }
            newFragmeent.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(android.R.id.content, newFragmeent)
                    .commit();
        }
        
        mLeft.setImageResource(R.drawable.ic_menu_back);
        mRight.setVisibility(View.INVISIBLE);
    }

}
