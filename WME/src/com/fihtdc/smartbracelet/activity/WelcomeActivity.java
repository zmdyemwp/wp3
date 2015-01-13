package com.fihtdc.smartbracelet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.util.Constants;

public class WelcomeActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
    }
    
    
    

   public void onClick(View v) {
        switch (v.getId()) {
        case R.id.begin_setup:
            Intent intent = new Intent();
            intent.setClass(this, ProfileActivity.class);
            intent.putExtra(Constants.FROM_WELCOM_EXTRA, true);
            startActivityForResult(intent, 0);
            break;

        default:
            break;
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
