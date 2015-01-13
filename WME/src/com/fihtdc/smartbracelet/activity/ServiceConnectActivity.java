package com.fihtdc.smartbracelet.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.service.BLEService;
import com.fihtdc.smartbracelet.util.Utility;

public class ServiceConnectActivity extends CustomActionBarActivity {
    Context mContext;

    private static final int REQUEST_ENABLE_BT = 123;
    private BLEService mBluetoothLeService;
    private BluetoothAdapter mBluetoothAdapter;
    private OnServiceConnectedChangeListener mConnectedChangeListener;
    
    Handler mHandler;
    
    
    public interface OnServiceConnectedChangeListener {
        public void onServiceConnected();
        public void onServiceDisconnected();
    }
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        initBTAdapter();
        Intent gettServiceIntent = new Intent(this, BLEService.class);
        bindService(gettServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void initBTAdapter() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Utility.startBTEnable(ServiceConnectActivity.this, mBluetoothAdapter, REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            //
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        if (null != mBluetoothLeService) {
            mBluetoothLeService.setCallbackHandler(mHandler);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        this.unbindService(mServiceConnection);
        mHandler = null;
        super.onDestroy();
    }
    
    public void setOnServiceConnectedChangeListener(OnServiceConnectedChangeListener listener){
        mConnectedChangeListener = listener;
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BLEService.LocalBinder) service).getService();
            if (mHandler != null) {
                mBluetoothLeService.setCallbackHandler(mHandler);
            }
            if (mConnectedChangeListener != null) {
                mConnectedChangeListener.onServiceConnected();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mHandler = null;
            mBluetoothLeService = null;
            if (mConnectedChangeListener != null) {
                mConnectedChangeListener.onServiceDisconnected();
            }
        }
    };

    public boolean isBTConnected() {
        if (mBluetoothLeService != null && mBluetoothLeService.isBTConnected()) {
            return true;
        }

        return false;
    }
    
    public void setCallbackHandler(Handler handler){
        mHandler = handler;
    }


    public BLEService getBLEService() {
        return mBluetoothLeService;
    }

    @Override
    public void onClickLeft() {
        onBackPressed();
    }
    
    @Override
    public void onClickRight() {
        setResult(RESULT_OK);
        super.onClickRight();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        } else {
            getFragmentManager().popBackStack();
            removeCurrentFragment();
        }
    }

    public void removeCurrentFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment currentFrag = getFragmentManager().findFragmentById(R.id.single_fragment);

        if (currentFrag != null) {
            transaction.remove(currentFrag);
            // LogApp.Logd(TAG,
            // "fragName "+currentFrag.getClass().getSimpleName());
        }

        transaction.commit();
    }
}
