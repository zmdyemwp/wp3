package com.fihtdc.smartbracelet.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fihtdc.smartbracelet.R;

public class HistoryChooserFragment extends CommonFragment implements View.OnClickListener{
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.history_chooser, null);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        view.findViewById(R.id.measure).setOnClickListener(this);
        view.findViewById(R.id.coaching).setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.measure:
            Fragment newFragment = getFragmentManager().findFragmentByTag("MeasureHistoryFragment");
            if (newFragment == null) {
                newFragment = new MeasureHistoryFragment();
            }

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.single_fragment, newFragment, "MeasureHistoryFragment");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
            ft.commit();
            break;
        case R.id.coaching:
            Fragment fragment = getFragmentManager().findFragmentByTag("CoachingHistoryFragment");
            if (fragment == null) {
                fragment = new CoachingHistoryFragment();
            }

            FragmentTransaction t = getFragmentManager().beginTransaction();
            t.replace(R.id.single_fragment, fragment, "CoachingHistoryFragment");
            t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            t.addToBackStack(null);
            t.commit();
            break;

        default:
            break;
        }
        
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.history_title);
    }
    
    

}
