package com.fihtdc.smartbracelet.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.service.BLEService;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.Utility;

public class NewPairActivity extends CustomActionBarActivity {

    // Add by Galvin.q.wang 2013/11/14 14.46 begin
    private static final int REQUEST_OPEN_BLUETOOTH_CODE = 102;
    // Add by Galvin.q.wang 2013/11/14 14.46 end
	
	private BLEService mBluetoothLeService;
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mIsFromWelcome;
	
	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		this.setContentView(R.layout.new_pair);
		
		Intent intent = getIntent();
		if (intent != null) {
            mIsFromWelcome = intent.getBooleanExtra(Constants.FROM_WELCOM_EXTRA, false);
        }
        
        if (!mIsFromWelcome) {
            mRight.setImageResource(R.drawable.ic_menu_home);
            mRight.setVisibility(View.VISIBLE);
        } else {
            mRight.setVisibility(View.INVISIBLE);
        }

        initViews();
        initBTAdapter();
	}
	
	private void initBTAdapter(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Utility.startBTEnable(NewPairActivity.this, mBluetoothAdapter,
                REQUEST_OPEN_BLUETOOTH_CODE);
    }
	
	private void initViews(){
        mLeft.setImageResource(R.drawable.ic_menu_back);
    }
	
	
}
