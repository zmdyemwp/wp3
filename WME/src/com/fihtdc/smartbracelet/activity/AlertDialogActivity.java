package com.fihtdc.smartbracelet.activity;

import com.fihtdc.smartbracelet.fragment.AlertDialogFragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

public class AlertDialogActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = null;
        Intent intent = getIntent();
        if (intent != null) {
            bundle = intent.getExtras();
        }
        
        if (bundle == null) {
            bundle = new Bundle();
        }
       
        if (savedInstanceState == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            
            DialogFragment newFragment = new AlertDialogFragment();
            int type = bundle.getInt("type");
            newFragment.setArguments(bundle);
            newFragment.show(ft, String.valueOf(type));
        }
    }
}
