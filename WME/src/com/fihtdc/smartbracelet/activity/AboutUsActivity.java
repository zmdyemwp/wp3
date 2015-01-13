package com.fihtdc.smartbracelet.activity;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.service.BLEService;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.LogApp;
import com.fihtdc.smartbracelet.util.Utility;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.TextView;

public class AboutUsActivity extends CustomActionBarActivity{
    
    private TextView mVersionName;
    private TextView mWmeVersionName;
    private BLEService mBluetoothLeService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us_activity);
        mLeft.setImageResource(R.drawable.ic_menu_back);
        mRight.setImageResource(R.drawable.ic_menu_home);
        mVersionName = (TextView)findViewById(R.id.version_name);
        mWmeVersionName = (TextView)findViewById(R.id.wme_version_name);
        Intent gattServiceIntent = new Intent(this, BLEService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }
    
    @Override
    protected void onResume() {
        getVersionInfo();
        setWMEversion();
        super.onResume();
    }
    
    private void getVersionInfo() {
        PackageManager pm = getPackageManager(); 
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(this.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
        } catch (NameNotFoundException e) {
            
        }
        String versionName = packageInfo.versionName;
        
        mVersionName.setText(getString(R.string.aboutus_version_title, versionName));

    }

    private void setWMEversion(){
        String version = Utility.getSharedPreferenceValue(this, Constants.KEY_VERSION, "00");
        mWmeVersionName.setText(getString(R.string.aboutus_wme_version_title, version));
    }
    
    @Override
    protected void onDestroy() {
        this.unbindService(mServiceConnection);
        super.onDestroy();
    }

    @Override
    public void onClickRight() {
        setResult(RESULT_OK);
        super.onClickRight();
    }
    
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            LogApp.Logd("msg="+msg.what);
            
            if(AboutUsActivity.this.isFinishing()||AboutUsActivity.this.isDestroyed()){
                return;
            }
            
            switch (msg.what) {
            case BLEService.MSG_VERSION_CHANGE_RESULT:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bundle bd = (Bundle) msg.getData();
                        String version = bd.getString(BLEService.BLE_DEVICE_VERSION, "");
                        LogApp.Logd("version="+version);
                        mWmeVersionName.setText(getString(R.string.aboutus_wme_version_title, version));
                    }
                });
                break;
            default:
                super.handleMessage(msg);
            }
        }
    };
    
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BLEService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
               // finish();
            }
            mBluetoothLeService.setCallbackHandler(mHandler);
            mBluetoothLeService.NotifySBGetVersion();
            
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            mHandler = null;
        }
    };

}
