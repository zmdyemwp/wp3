package com.fihtdc.smartbracelet.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.service.BLEService;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.LogApp;
import com.fihtdc.smartbracelet.util.Utility;

public class PairActivity extends CustomActionBarActivity implements OnEditorActionListener {
    public static final int REQUEST_PAIR_CODE = 101;

    private static final int MESSAGE_DELAY_TIME = 250;
    private static final int NAME_MESSAGE = 98;
    private static final int RECONNECTTED = 99;
    private static final int REQ_CODE = 100;

    // Add by Galvin.q.wang 2013/11/14 14.46 begin
    private static final int REQUEST_OPEN_BLUETOOTH_CODE = 102;
    // Add by Galvin.q.wang 2013/11/14 14.46 end

    private static final String NAME_TITLE = "WithMe-";
    private BLEService mBluetoothLeService;
    
    //View 
    private TextView mTop;
    private EditText mPaireName;
    private TextView mStatus;
    private Button mUseMe;
    private Button mScan;
    private Button mForget;
    private Toast mToast;
    
    private boolean mIsFromWelcome;
    private boolean mIsPairOK;
    private boolean mNeedReconnected = false;
    
    private String mShowName="";
    private BluetoothAdapter mBluetoothAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pair);
        
        initViews();
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
        
        initBTAdapter();
        
        Intent gattServiceIntent = new Intent(this, BLEService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        
    }
    
    private void initBTAdapter(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Utility.startBTEnable(PairActivity.this, mBluetoothAdapter,
                REQUEST_OPEN_BLUETOOTH_CODE);
    }
    
    private void initViews(){
        mLeft.setImageResource(R.drawable.ic_menu_back);
        mStatus = (TextView)findViewById(R.id.status);
        mPaireName = (EditText)findViewById(R.id.name);
        mUseMe = (Button)findViewById(R.id.useMEButton);
        mScan = (Button)findViewById(R.id.scanButton);
        mForget = (Button)findViewById(R.id.forgetButton);
        mPaireName.setOnEditorActionListener(this);
        mTop = (TextView)findViewById(R.id.top);
    }
    
    
    @Override
    protected void onResume() {
        super.onResume();
        if(null != mBluetoothLeService){
            mBluetoothLeService.setCallbackHandler(mHandler);
            if(!mBluetoothLeService.isBTConnected() && mBluetoothAdapter.isEnabled()){
                mHandler.sendEmptyMessageDelayed(RECONNECTTED, 1000);
            }
        }
        
        if(TextUtils.isEmpty(Utility.getSharedPreferenceValue(this, Utility.BLE_NAME, ""))){
            mIsPairOK = false;
        }else{
            mIsPairOK = true;
        }
        refresh();

    }
    
    private boolean haveConnected(){
        return (mBluetoothLeService != null && null != mBluetoothLeService.getConnectedBLEdevices())?true:false;
    }
    
    @Override
    protected void onPause() {
        if(mPaireName.isShown()){
            changeDevicesName(mPaireName);
        }
        if(null != mToast){
            mToast.cancel();
        }
        super.onPause();
    }
    
    @Override
    protected void onDestroy() {
        this.unbindService(mServiceConnection);
        mHandler = null;
        
        super.onDestroy();
    }
    
    private void refresh(){
        if (mIsPairOK) {
            
            //Paired & connected
            if(haveConnected()){
                mStatus.setText(R.string.device_connect);
                mStatus.setTextColor(Color.WHITE);
                mPaireName.setEnabled(true);
                mNeedReconnected = false;
            }else{//Paired but NOT connected
                mStatus.setText(R.string.device_not_connect);
                mStatus.setTextColor(0xFF696969);
                mPaireName.setEnabled(false);
                mNeedReconnected = true;
            }
            
            mPaireName.setVisibility(View.VISIBLE);
            if(TextUtils.isEmpty(getPaireName())){
                String name = Utility.getSharedPreferenceValue(this, Utility.BLE_NAME, "");
                //setUIname(name);
                mHandler.removeMessages(NAME_MESSAGE);
                mHandler.sendMessageDelayed(
                        Message.obtain(mHandler, NAME_MESSAGE, name), MESSAGE_DELAY_TIME);
            }
            
            mForget.setVisibility(View.VISIBLE);
            mScan.setVisibility(View.GONE);
            
            mTop.setText(R.string.device_name_hit);
        } else {
            mNeedReconnected = false;
            mStatus.setText(R.string.device_not_connect);
            mStatus.setTextColor(0xFF696969);
            
            mScan.setVisibility(View.VISIBLE);
            mForget.setVisibility(View.GONE);
            mTop.setText(R.string.no_pair_device_hit);
            mPaireName.setVisibility(View.INVISIBLE);
        }
        
        if (mIsFromWelcome && haveConnected()) {
            mUseMe.setVisibility(View.VISIBLE);
        } else {
            mUseMe.setVisibility(View.GONE);
        }
    }
    
    
    private String getPaireName(){
        String s = "";
        try{
            s = mPaireName.getText().toString();
        }catch(Exception e){
            
        }
        return s;
    }
    
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.scanButton:

            // modify by Galvin.q.wang 2013/11/14 14.46 begin
            if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            startSearchDialog();
            } else {
                Toast.makeText(PairActivity.this,
                        getString(R.string.open_bluetooth_toast),
                        Toast.LENGTH_SHORT).show();
            }
            // startSearchDialog();
            // modify by Galvin.q.wang 2013/11/14 14.46 end

            break;
        case R.id.forgetButton:

            // modify by Galvin.q.wang 2013/11/14 14.46 begin
            if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            disconnect();
            mIsPairOK = false;
            refresh();
            } else {
                Toast.makeText(PairActivity.this,
                        getString(R.string.open_bluetooth_toast),
                        Toast.LENGTH_SHORT).show();
            }
            // disconnect();
            // mIsPairOK = false;
            // refresh();
            // modify by Galvin.q.wang 2013/11/14 14.46 end

            break;
        case R.id.useMEButton:
            Intent intent = new Intent();
            intent.setClass(this, HomeActivity.class);
            startActivity(intent);
            setResult(RESULT_OK);
            finish();
            break;
        default:
            break;
        }
        super.onClick(v);
    }
    
    @Override
    public void onClickRight() {
        setResult(RESULT_OK);
        finish();
    }
    
    private void startSearchDialog() {
        Intent intent = new Intent(this,PairListActivity.class);
        this.startActivityForResult(intent, REQ_CODE);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQ_CODE == requestCode && RESULT_OK == resultCode) {
            mIsPairOK = true;
            //save the remote devices and connect all times
            BluetoothDevice device = data.getParcelableExtra(BLEService.BLE_DEVICE);
            setConnectBLEName(device);
            showToast();
            mIsPairOK = true;
            refresh();

        }
        // add by Galvin.q.wang 2013/11/14 14.46 begin
        else if (REQUEST_OPEN_BLUETOOTH_CODE == requestCode
                && RESULT_CANCELED == resultCode) {
            PairActivity.this.finish();
        }
        // add by Galvin.q.wang 2013/11/14 14.46 end
        else {
            mIsPairOK = false;
        }
    }
    
    private void showToast(){
        mToast = new Toast(this);
        View v = View.inflate(this, R.layout.toastview, null);
        mToast.setView(v);
        mToast.setGravity(Gravity.TOP, 0, (int) this.getResources().getDimension(R.dimen.scaned_toast_offset_y));
        mToast.show();
    }
    
    
    
    /**
     * @author F3060326 at 2013/8/6
     * @return void
     * @param @param device
     * @Description: set connect BLE name
     */
    private void setConnectBLEName(BluetoothDevice device){
        if(null != device){
            //setUIname(device.getName());
            mHandler.removeMessages(NAME_MESSAGE);
            mHandler.sendMessageDelayed(
                    Message.obtain(mHandler, NAME_MESSAGE, device.getName()), MESSAGE_DELAY_TIME);
        }
    }
    
    private void setUIname(String name){
        if(null != name){
            String devicesName = name.trim();
            LogApp.Logd("devicesName="+devicesName+"length="+devicesName.length());
            if(null != devicesName && devicesName.length() > NAME_TITLE.length()){
                if(devicesName.contains(NAME_TITLE)){
                    mPaireName.setText(
                            devicesName.substring(NAME_TITLE.length(), devicesName.length()));
                    LogApp.Logd("mPaireNameText="+devicesName.substring(NAME_TITLE.length(), devicesName.length()));
                    LogApp.Logd("setSelection="+(devicesName.length()-NAME_TITLE.length()));
                    mPaireName.setSelection(devicesName.length()-NAME_TITLE.length());
                    mShowName = mPaireName.getText().toString();
                }
            }
        }
    }
    
    /**
     * @author F3060326 at 2013/8/1
     * @return void
     * @param 
     * @Description: Disconnect WME devices
     */
    private void disconnect(){
        
        Utility.setSharedPreferenceValue(this, Utility.BLE_MAC, "");
        Utility.setSharedPreferenceValue(this, Utility.BLE_NAME, "");
        
        if(null != mBluetoothLeService){
            mBluetoothLeService.disconnect();
        }
    }
    
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BLEService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
               // finish();
            }
            mBluetoothLeService.setCallbackHandler(mHandler);
            setRefreshUI(mBluetoothLeService.getConnectedBLEdevices());
            
            if(mBluetoothAdapter.isEnabled()){
                mHandler.sendEmptyMessageDelayed(RECONNECTTED, 1000);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            mHandler = null;
        }
    };
    
    private void setRefreshUI(BluetoothDevice bd){
        LogApp.Logd("setRefreshUI"+bd);
        if(null != bd ){
            mIsPairOK = true;
            //Set BLE name
            String name = Utility.getSharedPreferenceValue(this, Utility.BLE_NAME, "");
            if(TextUtils.isEmpty(name)){
                setConnectBLEName(bd);
            }else{
                //setUIname(name);
                mHandler.removeMessages(NAME_MESSAGE);
                mHandler.sendMessageDelayed(
                        Message.obtain(mHandler, NAME_MESSAGE, name), MESSAGE_DELAY_TIME);
            }
        }
        refresh();
    }

    @Override
    public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
        LogApp.Logd("onEditorAction arg1="+arg1);
        if(EditorInfo.IME_ACTION_DONE == arg1 ){
            LogApp.Logd("EditorInfo.IME_ACTION_DONE");
            changeDevicesName(arg0);
            //fixed APPG2-4953
            hideIME();
        }
        return true;
    }
    
    /**
     * @author F3060326 at 2013/9/11
     * @return void
     * @param 
     * @Description: hide IME after action done
     * @throws 
     */
    public void hideIME(){
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, InputMethodManager.HIDE_NOT_ALWAYS);
    }
    
    /**
     * @author F3060326 at 2013/8/10
     * @return void
     * @param @param v
     * @Description: change Devices Name 
     */
    private void changeDevicesName(TextView v){
        
        if(null != mBluetoothLeService){
            String name  = v.getText().toString();
            if(!TextUtils.isEmpty(name)&& !name.equalsIgnoreCase(mShowName)){
                LogApp.Logd("Set frindly name!! name="+name);
                Utility.setSharedPreferenceValue(this,Utility.BLE_NAME,NAME_TITLE+name);
                mBluetoothLeService.NotifySBupdateFriendlyName(v.getText());
            }
        }
    }
    
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            LogApp.Logd("msg="+msg.what);
            
            if(PairActivity.this.isFinishing()||PairActivity.this.isDestroyed()){
                return;
            }
            
            switch (msg.what) {
            case BLEService.MSG_CONNECT_RESULT:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(null != mBluetoothLeService){
                            setRefreshUI(mBluetoothLeService.getConnectedBLEdevices());
                        }
                        
                    }
                });
                break;
            case BLEService.MSG_DISCONNECT_RESULT:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       //do nothing
                        if(null != mBluetoothLeService){
                            setRefreshUI(mBluetoothLeService.getConnectedBLEdevices());
                        }
                    }
                });
                break;
            case BLEService.MSG_DEVICENAME_CHANGE_RESULT:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bundle bd = (Bundle) msg.getData();
                        //setUIname(bd.getString(BLEService.BLE_DEVICE_NAME, ""));
                        mHandler.removeMessages(NAME_MESSAGE);
                        mHandler.sendMessageDelayed(
                                Message.obtain(mHandler, NAME_MESSAGE, 
                                        bd.getString(BLEService.BLE_DEVICE_NAME, "")), 250);
                    }
                });
                break;
            case RECONNECTTED:
                if(null != mBluetoothLeService){
                    this.removeMessages(RECONNECTTED);
                    if(!mBluetoothLeService.isBTConnected()){
                        if(mBluetoothAdapter.isEnabled()){
                            mBluetoothLeService.autoConnect();
                        }
                    }
                }
                break;
            case NAME_MESSAGE:
                setUIname((String)msg.obj);
                break;
            default:
                super.handleMessage(msg);
            }
        }
    };
}
