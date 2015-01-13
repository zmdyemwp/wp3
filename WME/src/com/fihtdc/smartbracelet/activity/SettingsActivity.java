package com.fihtdc.smartbracelet.activity;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.fragment.SettingsFragment;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.Utility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View.OnClickListener;

public class SettingsActivity extends CustomActionBarActivity implements OnClickListener{

    private OnCancelResetListener mListener = null;
    private Context mContext = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mContext = this;
        Fragment frag = Fragment.instantiate(this, SettingsFragment.class.getName());
        getFragmentManager().beginTransaction().add(android.R.id.content, frag).commit();
        initView();
    }
    
    public interface OnCancelResetListener{
        public void notifyCancelAction();
    }
    
    public void setOnCancelResetListener(OnCancelResetListener o){
        this.mListener = o;
    }
    
    private void initView(){
        mLeft.setImageResource(R.drawable.ic_menu_back);
        mRight.setImageResource(R.drawable.ic_menu_home);
    }
    
    public Dialog onCreateDialog(int id) {
        switch(id){
            case SettingsFragment.SHOW_RESET_ALL_SETTINGS_DIALOG:
                return new AlertDialog.Builder(this)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setMessage(R.string.reset_dialog_txt)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //do follow things:
                        Utility.clearSharedPreferenceValue(mContext);
                        //fly.f.ren add for fixed reset fb issue begin
                        if(com.fihtdc.smartbracelet.facebook.Utility.mFacebook!=null){
                        	com.fihtdc.smartbracelet.facebook.Utility.mFacebook.setTokenFromCache(null, 0, 0);
                        }
                        //fly.f.ren add for fixed reset fb issue end
                        //Notice:Here have a issue:deleted database,then insert profile date,Will FC
                        //because BraceletSQLiteHelper is single instance,it would not run onCreate() callback
                        //Now clear all tables data and keep the tables structure.
                        Utility.clearTablesData(mContext);
                        Utility.delAllFile(Constants.APP_SDCARD_PATH);
                        startSeriviceToDisconnectBT();
                        startWelcomeActivity();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        doCancelAction();
                    }
                }).setCancelable(true).setOnCancelListener(new OnCancelListener() {
                    
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        doCancelAction();
                    }
                })
                .create();
            default:
                break;
            }
        return null;
        }
    
    private void doCancelAction(){
        /* User clicked Cancel so do some stuff */
        if(null != mListener){
            mListener.notifyCancelAction();
        }
    }
    private void startWelcomeActivity(){
        finish();
        Intent intent = new Intent();
        intent.setClass(SettingsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        
    }
    
    private void startSeriviceToDisconnectBT(){
        Intent aIntent = new Intent(Constants.COMMAND_RESET);
        startService(aIntent);
    }
    
    @Override
    public void onClickRight() {
        setResult(RESULT_OK);
        super.onClickRight();
    }
}
