package com.fihtdc.smartbracelet.facebook;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.facebook.CheckIn.CheckInCallBack;
import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.LogApp;

public class FaceBookService extends Service implements CheckInCallBack {
	private static final String NOTIFICATION_REQUEST_URL = "me/notifications";
	private static final String FRIENDREQ_REQUEST_URL = "me/friendrequests";
	private static final String INBOX_REQUEST_URL = "me/inbox";
	private static final String UNREAD_JSON_KEY = "unread_count";
	private static final String UNSEEN_JSON_KEY = "unseen_count";
	private static final String SUMMARY_JSON_KEY = "summary";
	private static final String TAG = "NotificationCheckService";
	PendingIntent mPendingIntent;
	private AlarmManager mAlarmManager;
	private static final int MIMUTE = 60;
	private static final int SECONDS = 1000;

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate  ");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand  ");
		if (mPendingIntent == null) {
			Intent serviceIntent = new Intent(this, FaceBookService.class);
			serviceIntent.putExtra(Constants.FACEBOOK_NOTIFICATION_EXTRA,
					Constants.FACEBOOK_NOTIFICATION_REQUEST);
			serviceIntent.setAction(Constants.FACEBOOK_NOTIFICATION_ACTION);
			mPendingIntent = PendingIntent
					.getService(this, 0, serviceIntent, 0);
			mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		}
		com.fihtdc.smartbracelet.facebook.Utility.initReqEnviroment(this);
		com.fihtdc.smartbracelet.facebook.Utility.mFacebook.setTokenFromCache(SessionStore.getCacheAccessToken(this), 
        		SessionStore.getCacheExpires(this), SessionStore.getCacheLastUpdate(this));
		handlerCommand(intent);
		return START_STICKY;
	}

	private void handlerCommand(Intent intent) {
		if (intent != null && intent.getAction() != null) {
			String action = intent.getAction();
			String extra = intent
					.getStringExtra(Constants.FACEBOOK_NOTIFICATION_EXTRA);
			Log.d(TAG, "action====" + action);
			Log.d(TAG, "extra======" + extra);
			if (action.equals(Constants.FACEBOOK_CHECKIN_ACTION)) {
				boolean isOpenCheck = com.fihtdc.smartbracelet.util.Utility
						.getSharedPreferenceValue(getApplicationContext(),
								Constants.KEY_CHECKIN, false);
				if (isOpenCheck) {
					if (!Utility.isSessionValid( this)) {
//						showToast(getString(R.string.com_facebook_loginview_log_in_prompt_message));
						sendFailIntent();
					} else {
						onRequestCheckIn();
					}
				} else {
//					showToast(getString(R.string.facebook_checkin_control_prompt));
					sendFailIntent();
				}

			} else if (action.equals(Constants.FACEBOOK_NOTIFICATION_ACTION)) {
				boolean isOpenNotification = com.fihtdc.smartbracelet.util.Utility
						.getSharedPreferenceValue(getApplicationContext(),
								Constants.KEY_NOTIFICATION, false);
				Log.i("Fly", "isOpenNotification======"+isOpenNotification+"Utility.isSessionValid( this)==="+Utility.isSessionValid( this));
				Log.i("Fly", "extra======"+extra);
				if (null != extra
						&& extra.equals(Constants.FACEBOOK_NOTIFICATION_REQUEST)) {
					if (isOpenNotification
							&& Utility.isSessionValid( this)) {
						onRequestNotification();
					}

				} else if (null != extra
						&& extra.equals(Constants.FACEBOOK_SERVICE_STOP)) {
					onCancelAlarm();
					onStopSelf();
				} else {
					// start notification alarm notice notification to user by
					// timer
					if (Utility.isSessionValid( this)
							&& isOpenNotification) {
						onStartNotificationAlarm();
					}
				}
			} else if (action
					.equals(Constants.FACEBOOK_NOTIFICATION_CLOSE_ACTION)) {
				onCancelAlarm();
			}
		}
	}

	private void onStartNotificationAlarm() {
		Log.i("Fly", "onStartNotificationAlarm()");
		//fihtdc fly.f.ren 2013/10/08 update for fixed the APPG2-5277 issue
        //com.fihtdc.smartbracelet.util.Utility.setSharedPreferenceValue(FaceBookService.this, com.fihtdc.smartbracelet.util.Utility.FB_NOTIFICATION_COUNT, 0);
		// We want the alarm to go off 15 minutes from now.
		long firstTime = SystemClock.elapsedRealtime();
		// Schedule the alarm!
		mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				firstTime, 15*MIMUTE* SECONDS, mPendingIntent);
	}

	private void onCancelAlarm() {
		Log.i("Fly", "onCancelAlarm()");
		mAlarmManager.cancel(mPendingIntent);
	}

	private void onRequestCheckIn() {
		if(com.fihtdc.smartbracelet.util.Utility.isInternetWorkValid(this)){
			CheckIn check = CheckIn.getInstance(this);
			check.setmCallBack(this);
			String checkInMessage =com.fihtdc.smartbracelet.util.Utility
			.getSharedPreferenceValue(getApplicationContext(),
					Constants.KEY_CHECKIN_TEXT, "");
			if(null==checkInMessage || checkInMessage.equals("")){
				checkInMessage = getString(R.string.facebook_checkin_me_here);
			}
			check.checkIn(checkInMessage);
		}else{
			sendFailIntent();
		}
	}

//	public void showToast(final String msg) {
//		mHandler.post(new Runnable() {
//			@Override
//			public void run() {
//				Toast toast = Toast.makeText(FaceBookService.this, msg,
//						Toast.LENGTH_LONG);
//				toast.show();
//			}
//		});
//	}
	

	/**
	 * request notification
	 */
	@SuppressWarnings("deprecation")
	public void onRequestNotification() {
		Log.i("Fly", "onRequestNotification()");
		if(com.fihtdc.smartbracelet.util.Utility.isInternetWorkValid(this)){
			Log.i("Fly", "onRequestNotification Current work is valid");
			Bundle bundleParam = new Bundle();
			bundleParam.putString("metadata", "1");
			String[] requestURL = new String[3];
			requestURL[2] =INBOX_REQUEST_URL;
			requestURL[1] =NOTIFICATION_REQUEST_URL;
			requestURL[0] =FRIENDREQ_REQUEST_URL;
			bundleParam.putString(Facebook.TOKEN, SessionStore.getCacheAccessToken(this));
			com.fihtdc.smartbracelet.facebook.Utility.initReqEnviroment(this);
			com.fihtdc.smartbracelet.facebook.Utility.mFacebook.setTokenFromCache(SessionStore.getCacheAccessToken(this), 
	        		SessionStore.getCacheExpires(this), SessionStore.getCacheLastUpdate(this));
			Utility.mAsyncRunner.requestBatch(requestURL, bundleParam,
					new onNotificationRequestListener());
		}
		
	}

	public class onNotificationRequestListener extends BaseRequestListener {
		@Override
		public void onComplete(String response, Object state) {
			
		}

		@Override
		public void onBachReqComplete(String[] response, Object state) {
			// 0 flag for notfication,1 friends visited,2, Email
		    int mTotalUnSeenCount =com.fihtdc.smartbracelet.util.Utility.getSharedPreferenceValue(FaceBookService.this, com.fihtdc.smartbracelet.util.Utility.FB_NOTIFICATION_COUNT, 0);
			int totalUnSeenCount = 0;
			boolean isReqError = false;
			for (int i = 0; i < response.length; i++) {
				boolean isLegal =com.fihtdc.smartbracelet.facebook.Utility.checkRespLegitimate(response[i],FaceBookService.this);
				if(!isLegal){
					return;
				}
				Log.i("Fly", "response[]" + response[i]);
				String unSeenCount = null;
				JSONObject jsonObject = null;
				try {
					JSONObject json = Util.parseJson(response[i]);
					String summary = json.getString(SUMMARY_JSON_KEY);
					if (summary == null || summary.equals("[]")) {
						unSeenCount = "0";
					} else {
						jsonObject = new JSONObject(summary);
						if(summary.contains(UNSEEN_JSON_KEY)){
							unSeenCount = jsonObject.getString(UNSEEN_JSON_KEY);
						}else{
							unSeenCount = jsonObject.getString(UNREAD_JSON_KEY);
						}
					}
				} catch (JSONException e) {
					Log.i("Fly", "onBachReqComplete JSONException");
					isReqError = true;
					unSeenCount ="0";
				} catch (FacebookError e) {
					unSeenCount ="0";
					isReqError = true;
					LogApp.Logd("notification request error");
				}finally{
					if(unSeenCount!=null){
						totalUnSeenCount = totalUnSeenCount
								+ Integer.parseInt(unSeenCount);
					}
				}
			}
			Log.i("Fly", "totalUnSeenCount=====" + totalUnSeenCount);
			if(!isReqError){
				if (totalUnSeenCount > mTotalUnSeenCount || totalUnSeenCount == 0) {
					sendNotificationIntent(totalUnSeenCount);
				}
				mTotalUnSeenCount = totalUnSeenCount;
				com.fihtdc.smartbracelet.util.Utility.setSharedPreferenceValue(FaceBookService.this, com.fihtdc.smartbracelet.util.Utility.FB_NOTIFICATION_COUNT, mTotalUnSeenCount);
			}
			Log.i("Fly", "mTotalUnSeenCount====" + mTotalUnSeenCount);
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public void sendNotificationIntent(int notificationNum) {
		Log.i("Fly", "send NotificationIntent");
		Intent intent = new Intent();
		intent.setAction(Constants.COMMAND_ACTION);
		intent.putExtra(Constants.COMMAND_EXTRA, Constants.COMMAND_EXTRA_FB_NF);
		intent.putExtra(Constants.COMMAND_EXTRA_FB_NF_COUNT,notificationNum);
		startService(intent);
	}

	@Override
	public void onCheckInSuccess() {
		sendSuccessIntent();

	}

	private void sendSuccessIntent() {
		Intent intent = new Intent();
		intent.setAction(Constants.COMMAND_ACTION);
		intent.putExtra(Constants.COMMAND_EXTRA,
				Constants.COMMAND_EXTRA_FB_CHECKIN_OK);
		startService(intent);
	}

	@Override
	public void onCheckInFail() {
		sendFailIntent();
	}

	private void sendFailIntent() {
		Intent intent = new Intent();
		intent.setAction(Constants.COMMAND_ACTION);
		intent.putExtra(Constants.COMMAND_EXTRA,
				Constants.COMMAND_EXTRA_FB_CHECKIN_FAIL);
		startService(intent);

	}

	private void onStopSelf() {
		// to do stop service
		stopSelf();
		Utility.mFacebook = null;
		Utility.mAsyncRunner = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

}
