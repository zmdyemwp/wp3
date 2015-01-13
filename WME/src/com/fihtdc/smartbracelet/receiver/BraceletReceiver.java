package com.fihtdc.smartbracelet.receiver;

import java.io.IOException;

import com.fihtdc.smartbracelet.fragment.SettingsFragment;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.LogApp;
import com.fihtdc.smartbracelet.util.Utility;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class BraceletReceiver extends BroadcastReceiver {

    private static final String ACTION_PHONE_STATE ="android.intent.action.PHONE_STATE";
    private static final String ACTION_ALARM_ALERT = "com.android.deskclock.ALARM_ALERT";
    private static final String ACTION_NEW_EMAIL = "com.fihtdc.action.NEW_MAIL_LED_ON";
    private static final String ACTION_NAME_CHANGED = "android.accounts.LOGIN_ACCOUNTS_CHANGED";
    private static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    private static final int MSG_NEW_EMAIL = 101;
    private static final int MSG_CONNECT_BRACELET = 102;
    private static final int MSG_NEW_EMAIL_DELAY = 103;
    
    private int mEmailCount = 0;
    private Context mContext = null;
    

    private static final String TAG = "BraceletReceiver";
 
    @Override
    public void onReceive(Context context, Intent intent) {
        if(null == intent){
            return;
        }
        mContext = context;
        String action = intent.getAction();
        LogApp.Logd(TAG, "Action=="+action);
        if(ACTION_PHONE_STATE.equals(action)){
            TelephonyManager tm = 
                    (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
            switch (tm.getCallState()) {
            case TelephonyManager.CALL_STATE_RINGING:
                //receive Incoming call
                //String incoming_number = intent.getStringExtra("incoming_number");
                //LogApp.Logi(TAG, "RINGING :"+ incoming_number);
                boolean isOn = Utility.getSharedPreferenceValue(context, SettingsFragment.KEY_CALL_ALERT_BRACELET, true);
                if(isOn){
                    Intent aIntent = new Intent(Constants.COMMAND_ACTION);
                    aIntent.putExtra(Constants.COMMAND_EXTRA, Constants.COMMAND_EXTRA_INCOMING_CALL);
                    context.startService(aIntent);
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //LogApp.Logi(TAG, "incoming ACCEPT :");
                clearCallAlert(context);
                break;
        
            case TelephonyManager.CALL_STATE_IDLE:   
                //LogApp.Logi(TAG, "incoming IDLE");
                clearCallAlert(context);
                break;
            default:
                break;
            } 
           
        }else if(ACTION_ALARM_ALERT.equals(action)){
            boolean isOn = Utility.getSharedPreferenceValue(context, SettingsFragment.KEY_ALARM_ALERT_BRACELET, true);
            if(isOn){
                Intent aIntent = new Intent(Constants.COMMAND_ACTION);
                aIntent.putExtra(Constants.COMMAND_EXTRA, Constants.COMMAND_EXTRA_ALARM);
                context.startService(aIntent);
            }
           
        }else if(ACTION_NEW_EMAIL.equals(action)){
            //mEmailCount++;// need to record the new email count and pass it to service.
            //mHandler.sendEmptyMessageDelayed(MSG_NEW_EMAIL, 1000);
        }else if(Intent.ACTION_PROVIDER_CHANGED.equals(action)
                ||ACTION_NAME_CHANGED.equals(action)
                ||Constants.CHECK_GMAIL_ACTION.equals(action)){//Gmail provider changed
            mHandler.removeMessages(MSG_NEW_EMAIL_DELAY);
            mHandler.sendEmptyMessageDelayed(MSG_NEW_EMAIL_DELAY, 1000);
        }else if(ACTION_BOOT_COMPLETED.equals(action)){
            String address = Utility.getSharedPreferenceValue(context,
                    Utility.BLE_MAC, "");
            if (!TextUtils.isEmpty(address)) {
                mHandler.sendEmptyMessageDelayed(MSG_CONNECT_BRACELET, 2000);
            }
        }else {
            LogApp.Loge(TAG, "Unkown action");
        }
    }
    
    
    private void clearCallAlert(Context context){
        boolean isOn;
        isOn = Utility.getSharedPreferenceValue(context, SettingsFragment.KEY_CALL_ALERT_BRACELET, true);
        if(isOn){
            Intent aIntent = new Intent(Constants.COMMAND_ACTION);
            aIntent.putExtra(Constants.COMMAND_EXTRA, Constants.COMMAND_EXTRA_STOP_INCOMING_CALL);
            context.startService(aIntent);
        } 
    }
    
    private void doGmailAction(){
        AccountManager.get(mContext).getAccountsByTypeAndFeatures(Constants.ACCOUNT_TYPE_GOOGLE, Constants.FEATURES_MAIL,
                new AccountManagerCallback<Account[]>() {
                    @Override
                    public void run(AccountManagerFuture<Account[]> future) {
                        Account[] accounts = null;
                        try {
                            accounts = future.getResult();
                        } catch (OperationCanceledException oce) {
                            LogApp.Loge(TAG, "Got OperationCanceledException:"+oce);
                        } catch (IOException ioe) {
                            LogApp.Loge(TAG, "Got IOException:"+ioe);
                        } catch (AuthenticatorException ae) {
                            LogApp.Loge(TAG, "Got AuthenticatorException:"+ ae);
                        }
                        mEmailCount = Utility.onAccountResults(mContext,accounts);
                        int oldCount = Utility.getSharedPreferenceValue(mContext, SettingsFragment.KEY_TOTAL_UNREAD_GMAIL_COUNT, 0);
                        LogApp.Logd(TAG, "mEmailCount=="+mEmailCount+";oldCount=="+oldCount);
                        if(mEmailCount > oldCount){
                            Message msg = mHandler.obtainMessage();
                            msg.arg1 = mEmailCount;
                            msg.what = MSG_NEW_EMAIL;
                            mHandler.sendMessage(msg);
                        }else{
                            //Maybe user delete some email,we should update sharedPreference
                            if(mEmailCount == 0){
                                //Fixed to clear notification after send message
                                Message msg = mHandler.obtainMessage();
                                msg.arg1 = 0;
                                msg.what = MSG_NEW_EMAIL;
                                mHandler.sendMessage(msg);
                            }
                        }
                        Utility.setSharedPreferenceValue(mContext, SettingsFragment.KEY_TOTAL_UNREAD_GMAIL_COUNT, mEmailCount);
                        mEmailCount = 0;
                    }
                }, null /* handler */);
    }
    
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
            case MSG_NEW_EMAIL_DELAY:
                doGmailAction();
                break;
                
            case MSG_NEW_EMAIL:
                boolean isOn = Utility.getSharedPreferenceValue(mContext, SettingsFragment.KEY_ALARM_ALERT_BRACELET, true);
                if(isOn){
                    LogApp.Logd(TAG,"start service-->"+msg.arg1);
                    Intent aIntent = new Intent(Constants.COMMAND_ACTION);
                    aIntent.putExtra(Constants.COMMAND_EXTRA, Constants.COMMAND_EXTRA_EMAIL);
                    aIntent.putExtra(Constants.COMMAND_EXTRA_EMAIL_COUNT , msg.arg1);
                    mContext.startService(aIntent);
                }
                break;
                
            case MSG_CONNECT_BRACELET:
                if(Utility.isBTConnect()){
                    LogApp.Logd(TAG,"start service-->connet bracelet");
                    Intent intent = new Intent(Constants.COMMAND_REBOOT_ACTION);
                    mContext.startService(intent);
                }
                break;
            default:
                break;
            }
        }
        
    };

}
