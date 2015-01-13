package com.fihtdc.smartbracelet.activity;

import com.fihtdc.smartbracelet.provider.BraceletInfo.Profile;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

public class MainActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent();
        if (hasProfile()) {
            intent.setClass(this, HomeActivity.class);
            
        } else {
            intent.setClass(this, WelcomeActivity.class);
        }
        startActivity(intent);
        finish();
    }
    
    private boolean hasProfile(){
        boolean result = false;
        Cursor cursor = getContentResolver().query(Profile.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                result = true;
            }
            
            cursor.close();
        } 
        
        return result;
    }

}
