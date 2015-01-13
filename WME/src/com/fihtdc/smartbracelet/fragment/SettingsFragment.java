package com.fihtdc.smartbracelet.fragment;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.activity.SettingsActivity;
import com.fihtdc.smartbracelet.activity.SettingsActivity.OnCancelResetListener;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.Utility;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.fihtdc.smartbracelet.activity.FaceBookSettingActivity;
import com.fihtdc.smartbracelet.facebook.SessionEvents;
import com.fihtdc.smartbracelet.facebook.SessionStore;
import com.fihtdc.smartbracelet.facebook.SessionEvents.AuthListener;
import com.fihtdc.smartbracelet.facebook.SessionEvents.LogoutListener;

public class SettingsFragment extends CommonFragment implements OnClickListener,OnCancelResetListener{
    
    public static final String KEY_EMAIL_ALERT_BRACELET = "key_email_alert_bracelet";
    public static final String KEY_ALARM_ALERT_BRACELET = "key_alarm_alert_bracelet";
    public static final String KEY_CALL_ALERT_BRACELET = "key_call_alert_bracelet";
    public static final String KEY_FINDME_ALERT_BRACELET = "key_findme_alert_bracelet";
    public static final String KEY_TOTAL_UNREAD_GMAIL_COUNT = "key_total_unread_gmail_count";

    public static final int SHOW_RESET_ALL_SETTINGS_DIALOG = 101;
    
    private static final int REQUEST_FACEBOOK_CODE = 1;
    
    private Context mContext = null;
    private LinearLayout mBTPairLayout = null;
    private TextView mBTSummeryView = null;
    private TextView mFBSummeryView = null;
    
    private LinearLayout mEmailLayout = null;
    private LinearLayout mAlarmLayout = null;
    private TextView mAlarmSummeryView = null;
    
    private LinearLayout mFaceBookLayout = null;
    private LinearLayout mCallLayout = null;
    private LinearLayout mPhoneLayout = null;
    private LinearLayout mResetLayout = null;
    
    private Switch mEmailSwitch = null;
    private Switch mAlarmSwitch = null;
    private Switch mCallSwitch = null;
    private Switch mPhoneSwitch = null;
    private Switch mResetSwitch = null;
	private SessionStatusListener mSessionStatusListener = new SessionStatusListener();
	private class SessionStatusListener implements AuthListener, LogoutListener {

		@Override
		public void onAuthSucceed() {
			   //to do nothing
			
		}

		@Override
		public void onAuthFail(String error) {
			   //to do nothing
		}

		@Override
		public void onLogoutBegin() {
		      //to do nothing
		}

		@Override
		public void onLogoutFinish() {
			   Log.i("Fly", "onLogoutFinish()");
			   if(getActivity()!=null && !isDetached()){
				   doCheckAction(mContext,Utility.FB_ACCOUNT_NAME,mFBSummeryView);
			   }
		}
	}
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        ((SettingsActivity) mContext).setOnCancelResetListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBTPairLayout(view);
        initFaceBookLayout(view);
        initEmailLayout(view);
        initAlarmLayout(view);
        initCallLayout(view);
        initFindMeLayout(view);
        initResetLayout(view);
    }
    
    private void initCallLayout(View view){
        mCallLayout = (LinearLayout)view.findViewById(R.id.incoming_call_layout);
        mCallLayout.setBackgroundResource(R.drawable.bracelet_item_settings_middle);
        mCallLayout.setOnClickListener(this);
        ImageView imgView = (ImageView)mCallLayout.findViewById(R.id.item_icon);
        imgView.setImageResource(R.drawable.health_setting_ic_call);
        TextView tview = (TextView)mCallLayout.findViewById(R.id.titleTxt);
        tview.setText(R.string.incoming_call);
        ImageView myview = (ImageView)mCallLayout.findViewById(R.id.item_forword);
        myview.setVisibility(View.GONE);
        mCallSwitch = (Switch)mCallLayout.findViewById(R.id.switched);
        initPreferenceValue(mCallSwitch,KEY_CALL_ALERT_BRACELET,true);
    }
    
    private void initFindMeLayout(View view){
        mPhoneLayout = (LinearLayout)view.findViewById(R.id.find_phone_layout);
        mPhoneLayout.setBackgroundResource(R.drawable.bracelet_item_settings_middle);
        mPhoneLayout.setOnClickListener(this);
        ImageView imgView = (ImageView)mPhoneLayout.findViewById(R.id.item_icon);
        imgView.setImageResource(R.drawable.health_setting_ic_search);
        TextView tview = (TextView)mPhoneLayout.findViewById(R.id.titleTxt);
        tview.setText(R.string.find_my_phone);
        ImageView myview = (ImageView)mPhoneLayout.findViewById(R.id.item_forword);
        myview.setVisibility(View.GONE);
        mPhoneSwitch = (Switch)mPhoneLayout.findViewById(R.id.switched);
        initPreferenceValue(mPhoneSwitch,KEY_FINDME_ALERT_BRACELET,true);
    }
    
    private void initResetLayout(View view){
        mResetLayout = (LinearLayout)view.findViewById(R.id.reset_all_settings_layout);
        mResetLayout.setBackgroundResource(R.drawable.bracelet_item_settings_down);
        mResetLayout.setOnClickListener(this);
        ImageView imgView = (ImageView)mResetLayout.findViewById(R.id.item_icon);
        imgView.setImageResource(R.drawable.health_setting_ic_reset);
        TextView tview = (TextView)mResetLayout.findViewById(R.id.titleTxt);
        tview.setText(R.string.reset);
        ImageView myview = (ImageView)mResetLayout.findViewById(R.id.item_forword);
        myview.setVisibility(View.GONE);
        mResetSwitch = (Switch)mResetLayout.findViewById(R.id.switched);
    }
    
    private void initFaceBookLayout(View view){
        mFaceBookLayout = (LinearLayout)view.findViewById(R.id.facebook_layout);
        mFaceBookLayout.setBackgroundResource(R.drawable.bracelet_item_settings_down);
        mFaceBookLayout.setOnClickListener(this);
        ImageView imgView = (ImageView)mFaceBookLayout.findViewById(R.id.item_icon);
        imgView.setImageResource(R.drawable.health_setting_ic_facebook);
        TextView tview = (TextView)mFaceBookLayout.findViewById(R.id.titleTxt);
        tview.setText(R.string.facebook);
        mFBSummeryView = (TextView)mFaceBookLayout.findViewById(R.id.summaryTxt);
        Switch switchView = (Switch)mFaceBookLayout.findViewById(R.id.switched);
        switchView.setVisibility(View.GONE);
    }
    
    private void initAlarmLayout(View view){
        mAlarmLayout = (LinearLayout)view.findViewById(R.id.alarm_layout);
        mAlarmLayout.setBackgroundResource(R.drawable.bracelet_item_settings_middle);
        mAlarmLayout.setOnClickListener(this);
        ImageView imgView = (ImageView)mAlarmLayout.findViewById(R.id.item_icon);
        imgView.setImageResource(R.drawable.health_setting_ic_alarm);
        TextView tview = (TextView)mAlarmLayout.findViewById(R.id.titleTxt);
        tview.setText(R.string.alarm);
        mAlarmSummeryView = (TextView)mAlarmLayout.findViewById(R.id.summaryTxt);
        //mAlarmSummeryView.setText(R.string.no_active_alarm);
        mAlarmSummeryView.setVisibility(View.GONE);
        ImageView myView = (ImageView)mAlarmLayout.findViewById(R.id.item_forword);
        myView.setVisibility(View.GONE);
        mAlarmSwitch = (Switch)mAlarmLayout.findViewById(R.id.switched);
        initPreferenceValue(mAlarmSwitch,KEY_ALARM_ALERT_BRACELET,true);
    }
    
    private void initBTPairLayout(View view){
        mBTPairLayout = (LinearLayout)view.findViewById(R.id.bt_pair_layout);
        mBTPairLayout.setBackgroundResource(R.drawable.bracelet_item_settings_up);
        mBTPairLayout.setOnClickListener(this);
        ImageView imgView = (ImageView)mBTPairLayout.findViewById(R.id.item_icon);
        imgView.setImageResource(R.drawable.health_setting_ic_bule);
        TextView tview = (TextView)mBTPairLayout.findViewById(R.id.titleTxt);
        tview.setText(R.string.pair_wme);
        mBTSummeryView = (TextView)mBTPairLayout.findViewById(R.id.summaryTxt);
        Switch switchView = (Switch)mBTPairLayout.findViewById(R.id.switched);
        switchView.setVisibility(View.GONE);
    }
    
    private void initEmailLayout(View view){
        mEmailLayout = (LinearLayout)view.findViewById(R.id.email_layout);
        mEmailLayout.setBackgroundResource(R.drawable.bracelet_item_settings_up);
        mEmailLayout.setOnClickListener(this);
        ImageView imgView = (ImageView)mEmailLayout.findViewById(R.id.item_icon);
        imgView.setImageResource(R.drawable.health_setting_ic_email);
        TextView tview = (TextView)mEmailLayout.findViewById(R.id.titleTxt);
        tview.setText(R.string.gmail);
        ImageView myview = (ImageView)mEmailLayout.findViewById(R.id.item_forword);
        myview.setVisibility(View.GONE);
        mEmailSwitch = (Switch)mEmailLayout.findViewById(R.id.switched);
        initPreferenceValue(mEmailSwitch,KEY_EMAIL_ALERT_BRACELET,true);
    }
    
    private void initPreferenceValue(Switch switchBox,String key,boolean initValue){
        boolean flag = Utility.isSharedPreferenceContainKey(mContext,key);
        //flag is true,indicate that the preference value has existed.
        if(flag){
            flag = Utility.getSharedPreferenceValue(mContext, key, true);
            switchBox.setChecked(flag);
        }else{
            Utility.setSharedPreferenceValue(mContext, key, initValue);
            switchBox.setChecked(initValue);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
    	SessionEvents.addLogoutListener(mSessionStatusListener);
        doCheckAction(mContext,Utility.BLE_NAME,mBTSummeryView);
        doCheckAction(mContext,Utility.FB_ACCOUNT_NAME,mFBSummeryView);
    }

    private void doCheckAction(Context context,String key,TextView view){
        String string = Utility.getSharedPreferenceValue(context,key,null);
        if(TextUtils.isEmpty(string)){
            view.setVisibility(View.GONE);
        }else{
            view.setText(string);
            view.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    public void onClick(View v) {
        switch(v.getId()){
        case R.id.bt_pair_layout:
            launchPairWMEMode();
            break;
        case R.id.email_layout:
            updateValue(mEmailSwitch,KEY_EMAIL_ALERT_BRACELET);
            break;
        case R.id.alarm_layout:
            updateValue(mAlarmSwitch,KEY_ALARM_ALERT_BRACELET);
            break;
        case R.id.incoming_call_layout:
            updateValue(mCallSwitch,KEY_CALL_ALERT_BRACELET);
            break;
        case R.id.facebook_layout:
        	launchFaceBookMode();
            break;
        case R.id.find_phone_layout:
            updateValue(mPhoneSwitch,KEY_FINDME_ALERT_BRACELET);
            break;
        case R.id.reset_all_settings_layout:
            //show resrt all dialog
            mResetSwitch.setChecked(true);
            getActivity().showDialog(SHOW_RESET_ALL_SETTINGS_DIALOG);
            break;
        default:
            break;
        }
    }
    
    /**
     * @author F3060326 at 2013/8/1
     * @return void
     * @param 
     * @Description: launch to Paire WME in settings
     */
    private void launchPairWMEMode() {
        Utility.startPairedForResult(mContext, this);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
            case REQUEST_FACEBOOK_CODE:
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
                break;
            default:
                break;
            }
        }
    }

    private void launchFaceBookMode() {
	  Intent intent = new Intent(mContext,FaceBookSettingActivity.class);
	  startActivityForResult(intent, REQUEST_FACEBOOK_CODE);
	}

	private void updateValue(Switch switchBox,String KEY){
        boolean flag = switchBox.isChecked();
        switchBox.setChecked(!flag);
        Utility.setSharedPreferenceValue(mContext,KEY, !flag);
    }

    @Override
    public void notifyCancelAction() {
        mResetSwitch.setChecked(false);
        
    }

}
