package com.fihtdc.smartbracelet.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.fragment.AlertDialogFragment;
import com.fihtdc.smartbracelet.fragment.ProifleBriefFragment;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.Utility;

public class ProfileActivity extends CustomActionBarActivity{

    boolean mFromWelcome;
    
    ProifleBriefFragment mProfileBriefFrag;
    Context mContext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        mLeft.setImageResource(R.drawable.ic_menu_back);
        mContext = this;
        
        Intent intent = getIntent();
        if (intent != null) {
            mFromWelcome = intent.getBooleanExtra(Constants.FROM_WELCOM_EXTRA, false);
            if (!mFromWelcome) {
                mRight.setImageResource(R.drawable.ic_menu_home);
            } else {
                mRight.setImageResource(R.drawable.ic_menu_next);
            }
        }
        
        mProfileBriefFrag = (ProifleBriefFragment)getFragmentManager().findFragmentById(R.id.profile);
        if (savedInstanceState == null) {
            initContentView();
        }
    }

    private void initContentView() {
       if (mProfileBriefFrag == null) {
           mProfileBriefFrag = new ProifleBriefFragment();
       }
       
       Bundle bundle = new Bundle();
       bundle.putBoolean(Constants.FROM_WELCOM_EXTRA, mFromWelcome);
       mProfileBriefFrag.setArguments(bundle);
       FragmentTransaction ft = getFragmentManager().beginTransaction();
       ft.add(R.id.profile, mProfileBriefFrag);
       ft.commit();
    }

    @Override
    public void onClickRight() {
        if (!mFromWelcome) {
            if (mProfileBriefFrag != null) {
                if (mProfileBriefFrag.checkProfileComplete(true)) {
                    mProfileBriefFrag.saveProfileInfo();
                }
            }
            setResult(RESULT_OK);
            finish();
        } else {
            if (mProfileBriefFrag != null) {
                if (!mProfileBriefFrag.checkProfileComplete(false)) {
                    Utility.showAlertDialog(this, AlertDialogFragment.DIALOG_TYPE_NOT_COMPLETE,
                            null);
                    return;
                } else {
                    // Save data in DB
                    mProfileBriefFrag.saveProfileInfo();
                }
            }
            
            Intent intent = new Intent();
            intent.setClass(this, PairActivity.class);
            intent.putExtra(Constants.FROM_WELCOM_EXTRA, mFromWelcome);
            startActivityForResult(intent, 0);
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

    @Override
    public void onClickLeft() {
        onBackPressed();
    }
    
    @Override
    public void onBackPressed() {
        if (mProfileBriefFrag != null) {
            if (mProfileBriefFrag.checkProfileComplete(true)) {
                mProfileBriefFrag.saveProfileInfo();
            }
        }
        
        super.onBackPressed();
    }
    
    public void startViewsAnimation(){
        if (mProfileBriefFrag != null) {
            mProfileBriefFrag.startViewsAnimation();
        }
    }
}
