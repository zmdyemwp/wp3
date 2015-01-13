package com.fihtdc.smartbracelet.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Toast;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.activity.ServiceConnectActivity.OnServiceConnectedChangeListener;
import com.fihtdc.smartbracelet.fragment.FlingFragment;
import com.fihtdc.smartbracelet.service.BLEService;
import com.fihtdc.smartbracelet.util.LogApp;
import com.fihtdc.smartbracelet.util.Utility;
import com.yl.ekgrr.EkgRR;

public class FlingActivity extends ServiceConnectActivity implements
        OnServiceConnectedChangeListener {
    private float mXGetture;
    private OnSlideLeftRightListener mListener;
    
    private boolean mHasShowDialog = false;
    
    int mScreenWidth;
    int mScreenHeight;
    
    boolean mHasRemoveOnGlobal = false;
    View mGlobalView = null;
    
    private WifiManager mWifiSwitch;
    private boolean isWifi = false;
    
    
    /**
     * @author F3060326 at 2013/9/2
     * @return void
     * @param 
     * @Description: fragment need Override this function
     */
    public void onActivityGlobalLayout(){
        
    }
    
    protected void addOnGlobalLayoutListener(View v){
        mGlobalView = v;
        mGlobalView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }
    
    OnGlobalLayoutListener mOnGlobalLayoutListener = new OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            mHasRemoveOnGlobal = true;
            onActivityGlobalLayout();
            mGlobalView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Call these functions before call super onCreate
        setCallbackHandler(mHandler);
        setOnServiceConnectedChangeListener(this);
        
        super.onCreate(savedInstanceState);
        mLeft.setImageResource(R.drawable.ic_menu_back);
        mRight.setImageResource(R.drawable.ic_menu_home);        
        mXGetture = getResources().getDimension(R.dimen.gesture_detector);
        mContext = this;
        
        // fixed wifi issue
        if (Utility.wifiCondition(getWifiManager())) {
            // close wifi
            getWifiManager().setWifiEnabled(false);
            Toast toast = Toast.makeText(this,
                    getString(R.string.turnoff_wifi_notification),
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 200);
            toast.show();
            isWifi = true;
        }
    }

    
    public interface OnSlideLeftRightListener {
        public void doSlideLeftAction();
        public void doSlideRightAction();
    }

    private GestureDetector mGestureDetector = new GestureDetector(mContext,
            new SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                        float velocityY) {
                    //Fixed by JimmyChen for slide left/right issue
                    float gapX = 0f;
                    try{
                        gapX = e2.getX() - e1.getX();
                    }catch(Exception e){
                        gapX = 0f;
                    }
                    //LogApp.Logd("gapX"+gapX+"mXGetture="+mXGetture);
                    if (mListener != null) {
                        if (gapX > mXGetture) {
                            mListener.doSlideLeftAction();
                        } else if(gapX < -mXGetture){
                            mListener.doSlideRightAction();
                        }
                    }
                    return true;
                }

            });
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //JimmyChen marked for slide test end @20130909
        /*if (mListener != null ) {
            mGestureDetector.onTouchEvent(ev);
        }*/
        //JimmyChen marked for slide test end @20130909
        return super.dispatchTouchEvent(ev);
    }
    
    public void setOnSlideLeftRightListener(OnSlideLeftRightListener listener){
        mListener = listener;
    }
    
    @Override
    protected void onDestroy() {
        mHandler = null;

        if (!mHasRemoveOnGlobal && null != mGlobalView) {
            mGlobalView.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
        }
        
        if(isWifi){
            //open wifi
            mWifiSwitch.setWifiEnabled(true);
        }

        super.onDestroy();

    }
    
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            if (!Utility.isActivityLive(FlingActivity.this)) {
                return;
            }

            switch (msg.what) {
            case BLEService.MSG_CONNECT_RESULT:
                
                break;
            case BLEService.MSG_DISCONNECT_RESULT:
                LogApp.Logv("FlingActivity<--MSG_DISCONNECT_RESULT");
                if (EkgRR.isECGStarted && !mHasShowDialog) {
                    mHasShowDialog = true;
                    
                    setResult(FlingFragment.RESULT_SHOW_BLUETOOTH_DIALOG);
                    finish();
                }
                break;
            case BLEService.MSG_IT_SHUTDOWN:
                LogApp.Logv("FlingActivity<--MSG_IT_SHUTDOWN");
                finish();
                break;
            default:
                super.handleMessage(msg);
            }
        }
    };

    @Override
    public void onClickLeft() {
        onBackPressed();
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

        if (currentFrag != null){
            transaction.remove(currentFrag);
            //LogApp.Logd(TAG, "fragName "+currentFrag.getClass().getSimpleName());
        }

        transaction.commit();
    }
    
    public void initValues(){
        mHasShowDialog = false;
    }
    
    public void onClickRight(){
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onServiceConnected() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onServiceDisconnected() {
        // TODO Auto-generated method stub
        
    }
    
    private WifiManager getWifiManager(){
        if(null == mWifiSwitch){
            mWifiSwitch = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        }
        return mWifiSwitch;
    }
}
