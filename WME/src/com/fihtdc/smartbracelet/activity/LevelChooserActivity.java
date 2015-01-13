package com.fihtdc.smartbracelet.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.fragment.LevelChooserFragment;

public class LevelChooserActivity extends CustomActionBarActivity {
    public static final String TAG = "CoachingActivity";
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_stack);
        mLeft.setImageResource(R.drawable.ic_menu_back);
        mRight.setImageResource(R.drawable.ic_menu_home);
        if (savedInstanceState == null) {
            Fragment newFragment = getFragmentManager().findFragmentByTag("LevelChooserFragment");
            if (newFragment == null) {
                newFragment = new LevelChooserFragment();
            }
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            
            ft.replace(R.id.single_fragment, newFragment, "LevelChooserFragment");
            ft.commit();
        }
    }
}
