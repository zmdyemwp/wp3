package com.fihtdc.smartbracelet.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.fragment.ConfigurationFragment;
import com.fihtdc.smartbracelet.service.BLEService;
import com.fihtdc.smartbracelet.slidingmenu.SlidingMenu;
import com.fihtdc.smartbracelet.slidingmenu.app.SlidingFragmentActivity;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.LogApp;
import com.fihtdc.smartbracelet.util.Utility;

public class HomeActivity extends SlidingFragmentActivity implements View.OnClickListener {
    private static final String REMOTE_CONTROL = "WME_REMOTE_CONTROL";
    private static final int REQUEST_ENABLE_BT = 123;
    public static final int REQUEST_CAMARA_CODE = 125;
    private View mActionBarCustomView;
    private TextView mTitle;
    private ImageView mLeft;
    private ImageView mRight;
    private BLEService mBluetoothLeService;
    
    private BluetoothAdapter mBluetoothAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        initViews();
        
        initBTAdapter();
        
        bindService();
    }
    
    private void initViews(){
        setContentView(R.layout.activity_main);
        // set the Behind View
        setBehindContentView(R.layout.menu_frame);
        FragmentTransaction t = this.getFragmentManager().beginTransaction();
        t.replace(R.id.menu_frame, new ConfigurationFragment());
        t.commit();
        
        // customize the SlidingMenu
        SlidingMenu sm = getSlidingMenu();
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.health_setting_projection);
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setBehindScrollScale(0);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        
        mActionBarCustomView = findViewById(R.id.actionbar);
        mLeft = (ImageView)mActionBarCustomView.findViewById(R.id.left);
        mTitle = (TextView)mActionBarCustomView.findViewById(R.id.middle);
        mRight = (ImageView)mActionBarCustomView.findViewById(R.id.right);
        mTitle.setText(getTitle());
        mLeft.setImageResource(R.drawable.ic_menu_menu);
        mLeft.setOnClickListener(this);
        
        //Fixed remove Camera function in market version
        mRight.setImageResource(R.drawable.ic_menu_camera);
        mRight.setOnClickListener(this);
        //mRight.setVisibility(View.INVISIBLE);
        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogApp.Logd("onActivityResult="+requestCode+"resultCode="+resultCode);
        if(requestCode == REQUEST_ENABLE_BT && resultCode==RESULT_OK){
            if (null != mBluetoothLeService && mBluetoothLeService.initialize()) {
                 if(!mBluetoothLeService.autoConnect()){
                     LogApp.Logd("AutoConnect failed!!!");
                 }
             }
        }else if (Activity.RESULT_OK == resultCode && PairActivity.REQUEST_PAIR_CODE == requestCode) {
//            Intent intentCamera = new Intent(Constants.COMMAND_CAMERA_ACTION);
//            intentCamera.putExtra(REMOTE_CONTROL, true);
//            startActivity(intentCamera);
        }else if (REQUEST_CAMARA_CODE == requestCode) {
            if(null != mBluetoothLeService ){
                mBluetoothLeService.reSetCameraState(true);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void initBTAdapter(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Utility.startBTEnable(HomeActivity.this,mBluetoothAdapter,REQUEST_ENABLE_BT);
    }
    
    private void bindService(){
        Intent gattServiceIntent = new Intent(this, BLEService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        
        Intent intentStart = new Intent(Constants.COMMAND_START_ACTION);
        this.startService(intentStart);
    }
    
    @Override
    protected void onResume() {
        if(null != mBluetoothLeService ){
            mBluetoothLeService.reSetCameraState(false);
        }
        super.onResume();
    }
    
    
    @Override
    protected void onDestroy() {
        if (null != mBluetoothLeService ) {
            mBluetoothLeService.UserLeaveAction();
        }
        this.unbindService(mServiceConnection);
        super.onDestroy();
    }
    
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
        case R.id.left:
            toggle();
            break;
        case R.id.right:
            if (mBluetoothLeService!=null && mBluetoothLeService.isBTConnected()) {
                Intent intentCamera = new Intent(Constants.COMMAND_CAMERA_ACTION);
                intentCamera.putExtra(REMOTE_CONTROL, true);
                startActivityForResult(intentCamera,REQUEST_CAMARA_CODE);
            }else{
                Intent intent = new Intent(this,PairActivity.class);
                startActivityForResult(intent, PairActivity.REQUEST_PAIR_CODE);
            }
            break;
            
        case R.id.measure:
            Intent measureIntent = new Intent();
            measureIntent.setClass(this, UserChooserActivity.class);
            startActivity(measureIntent);
            break;
        case R.id.coaching:
            Intent intent = new Intent(HomeActivity.this,LevelChooserActivity.class);
            startActivity(intent);
            break;
        case R.id.history:
            Intent history = new Intent();
            history.setClass(this, HistoryActivity.class);
            startActivity(history);
            break;
        default:
            break;
        }
       }
        
    
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BLEService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
               // finish();
            }else{
                if(mBluetoothAdapter.isEnabled()){
                    if(!mBluetoothLeService.autoConnect()){
                        LogApp.Logd("AutoConnect failed!!!");
                    }
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    
}
