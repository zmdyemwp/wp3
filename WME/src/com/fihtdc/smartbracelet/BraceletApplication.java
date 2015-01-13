package com.fihtdc.smartbracelet;

import java.io.IOException;

import com.android.camera.Util;
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
import android.app.Application;
import android.content.Context;

public class BraceletApplication extends Application {

    private static final String TAG = "BraceletReceiver";
    private Context mContext = null;
    @Override
    public void onCreate() {
        mContext = this;
        recordAllUnreadGmailCount();
        Util.initialize(this);
        super.onCreate();
    }

    private void recordAllUnreadGmailCount(){
        AccountManager.get(this).getAccountsByTypeAndFeatures(Constants.ACCOUNT_TYPE_GOOGLE, Constants.FEATURES_MAIL,
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
                        int count = Utility.onAccountResults(mContext,accounts);
                        Utility.setSharedPreferenceValue(mContext, SettingsFragment.KEY_TOTAL_UNREAD_GMAIL_COUNT, count);
                    }
                }, null /* handler */);
    }
}
