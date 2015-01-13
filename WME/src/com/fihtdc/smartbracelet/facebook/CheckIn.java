package com.fihtdc.smartbracelet.facebook;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.internal.Utility;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphPlace;
import com.fihtdc.smartbracelet.util.Constants;

@SuppressLint("UseValueOf")
public class CheckIn {
	private static CheckIn mCheckIn;
	private Context mContext;
	private Handler mHandler;
	private JSONObject location;
	protected LocationManager lm;
	protected MyLocationListener locationListener;
	protected static JSONArray jsonArray;
	private String mCheckInMessage;
	private CheckInCallBack mCallBack;
	private static final String LATITUDE="latitude";
	private static final String LONGITUDE="longitude";
	
	private int mCheckTimes = 0;
	
    public interface CheckInCallBack{
    	void onCheckInSuccess();
    	void onCheckInFail();
    }
	private CheckIn(Context context) {
		location = new JSONObject();
		mContext = context;
		mHandler = new Handler();
	}

	synchronized public static CheckIn getInstance(Context context) {
		if (null == mCheckIn) {
			mCheckIn = new CheckIn(context);
		}
		return mCheckIn;

	}

	public void onLocationNearest() {
		getLocation();

	}

	public boolean checkIn(String message) {
		mCheckInMessage = message;
		onLocationNearest();
	    return false;
	
	}

	public void getLocation() {
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				if (lm == null) {
					lm = (LocationManager) mContext
							.getSystemService(Context.LOCATION_SERVICE);
				}
				if (locationListener == null) {
					locationListener = new MyLocationListener();
				}
//				Criteria criteria = new Criteria();
//				criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//				String provider = lm.getBestProvider(criteria, true);
//				 boolean isGPS =   Settings.Secure.isLocationProviderEnabled(mContext.getContentResolver(), LocationManager.GPS_PROVIDER);
//			     boolean isNetwork =   Settings.Secure.isLocationProviderEnabled(mContext.getContentResolver(), LocationManager.NETWORK_PROVIDER); 
//			     if(!isGPS && !isNetwork){
//			    	 mHandler.post(new Runnable() {
//						@Override
//						public void run() {
//							sendCheckInFailIntent();
//							AlertDialog dialog =com.fihtdc.smartbracelet.util.Utility.onCreateAccessPositionDialog(mContext);
//							dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//							dialog.show();
//						}
//					});
//			     }
			     
				String provider = null;
			     boolean isNetwork =   Settings.Secure.isLocationProviderEnabled(mContext.getContentResolver(), LocationManager.NETWORK_PROVIDER); 
			     
				    if(isNetwork){
			    		provider = LocationManager.NETWORK_PROVIDER;
			    	}else{
				 boolean isGPS =   Settings.Secure.isLocationProviderEnabled(mContext.getContentResolver(), LocationManager.GPS_PROVIDER);
			    		if(isGPS){
			    			provider = LocationManager.GPS_PROVIDER;
			    		}else{
			     if(!isGPS && !isNetwork){
			    	 mHandler.post(new Runnable() {
						@Override
						public void run() {
							sendCheckInFailIntent();
							AlertDialog dialog =com.fihtdc.smartbracelet.util.Utility.onCreateAccessPositionDialog(mContext);
							dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
							dialog.show();
						}
					});
			     }
			    		}
			    	}
			     
			     
				if (provider != null && lm.isProviderEnabled(provider)) {
					lm.requestLocationUpdates(provider, 0, 0, locationListener,
							Looper.getMainLooper());
				}
				Looper.loop();
			}
		}.start();
	}
	private void sendCheckInFailIntent() {
		Intent intent = new Intent();
		intent.setAction(Constants.COMMAND_ACTION);
		intent.putExtra(Constants.COMMAND_EXTRA,
				Constants.COMMAND_EXTRA_FB_CHECKIN_FAIL);
		mContext.startService(intent);

	}
	/*
	 * Callback after places are fetched.
	 */
	public class placesRequestListener extends BaseRequestListener {
		@Override
		public void onComplete(final String response, final Object state) {
			Log.d("Facebook-FbAPIs", "Got response: " + response);
			boolean isLegal =com.fihtdc.smartbracelet.facebook.Utility.checkRespLegitimate(response,mContext);
			if(!isLegal){
				mCallBack.onCheckInFail();
				return;
			}
			try {
				jsonArray = new JSONObject(response).getJSONArray("data");
				if (jsonArray == null) {
					showToast("Error: nearby places could not be fetched");
					fetchPlaces(mCheckTimes);
					return;
				}
			} catch (JSONException e) {
				fetchPlaces(mCheckTimes);
				return;
			}
			
			// fihtdc  fly.f.ren 2013/10/23 update for fixed  APPG2-5483 begin
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					JSONObject jsonObject = null;
					try {
						jsonObject = jsonArray.getJSONObject(0);
					} catch (JSONException e1) {
						//e1.printStackTrace();
						fetchPlaces(mCheckTimes);
						return;
					}
					String placeID = null;
					try {
						if(null!=jsonObject){
							placeID = jsonObject.getString("id");	
						}else{
							fetchPlaces(mCheckTimes);
							return;
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						fetchPlaces(mCheckTimes);
						return;
					}
		    // fihtdc  fly.f.ren 2013/10/23 update for fixed  APPG2-5483 end 
					/*
					 * Source tag: check_in_tag Check-in user at the selected location
					 * posting to the me/checkins endpoint. More info here:
					 * https://developers.facebook .com/docs/reference/api/user/ -
					 * checkins
					 */
					mCheckTimes = 0;
					
					Bundle params = new Bundle();
					params.putString("place", placeID);
					params.putString("message", mCheckInMessage);
					params.putString("coordinates", location.toString());
					params.putString(Facebook.TOKEN, SessionStore.getCacheAccessToken(mContext));
					com.fihtdc.smartbracelet.facebook.Utility.initReqEnviroment(mContext);
					com.fihtdc.smartbracelet.facebook.Utility.mFacebook.setTokenFromCache(SessionStore.getCacheAccessToken(mContext), 
			        		SessionStore.getCacheExpires(mContext), SessionStore.getCacheLastUpdate(mContext));
					com.fihtdc.smartbracelet.facebook.Utility.mAsyncRunner.request("me/feed", params, "POST",
							new placesCheckInListener(), null);
					
//					GraphPlace place  = GraphObject.Factory
//                            .create(jsonObject, GraphPlace.class);
//					
//					Request request = Request
//		                    .newStatusUpdateRequest(Session.getActiveSession(), "WME��������aaaaaaaaaaa����", new Request.Callback() {
//		                        @Override
//		                        public void onCompleted(Response response) {
//		                            //showPublishResult(message, response.getGraphObject(), response.getError());
//		                        	Log.v("2222","onCompleted response="+response);
//		                        }
//		                    });
//					request.setGraphObject(place);
//		            request.executeAsync();
				}
			});
		}

		public void onFacebookError(FacebookError error) {
			showToast("Fetch Places Error: " + error.getMessage());
		}
	}

	public class placesCheckInListener extends BaseRequestListener {
		@Override
		public void onFacebookError(FacebookError e, Object state) {
			super.onFacebookError(e, state);
			mCallBack.onCheckInFail();
		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			super.onFileNotFoundException(e, state);
			mCallBack.onCheckInFail();
		}

		@Override
		public void onIOException(IOException e, Object state) {
			super.onIOException(e, state);
			mCallBack.onCheckInFail();
		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			super.onMalformedURLException(e, state);
			mCallBack.onCheckInFail();
		}

		@Override
		public void onComplete(final String response, final Object state) {
		    Log.i("Fly", "Check In onComplete-->response="+response);
		    boolean isLegal =com.fihtdc.smartbracelet.facebook.Utility.checkRespLegitimate(response,mContext);
                    if(!isLegal){
                         mCallBack.onCheckInFail();
                         return;
                    }
		    if(null!=response && response.contains("OAuthException")){
		    	mCallBack.onCheckInFail();
		    	Log.i("Fly", "Check In onCheckInFail");
		    }else{
		    	mCallBack.onCheckInSuccess();
		    	Log.i("Fly", "Check In onCheckInSuccess");
		    }
		}

		public void onFacebookError(FacebookError error) {
		    Log.i("Fly", "Check In Fail");
			mCallBack.onCheckInFail();
	
		}
	}

	public void showToast(final String msg) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
				toast.show();
			}
		});
	}

	class ViewHolder {
		TextView name;
		TextView location;
	}

	class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			Log.i("Fly", "onLocationChanged");
			if (loc != null) {
				try {
					location.put(LATITUDE, new Double(loc.getLatitude()));
					location.put(LONGITUDE, new Double(loc.getLongitude()));
				} catch (JSONException e) {
				}
				lm.removeUpdates(this);
				fetchPlaces(0);
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.i("Fly", "on Provider Disable");
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.i("Fly", "on Provider Enabled");
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.i("Fly", "onStatusChanged");
		}
	}

	public void fetchPlaces(int i) {
		/*
		 * Source tag: fetch_places_tag
		 */
		Log.i("Fly", "fetchPlaces"+i);
		Bundle params = new Bundle();
		params.putString("type", "place");
		try {
			params.putString("center", location.getString(LATITUDE) + ","
					+ location.getString(LONGITUDE));
		} catch (JSONException e) {
			return;
		}
		if(0== i){
			mCheckTimes = 1;
		params.putString("distance", "100");
		}else if(1== i){
			mCheckTimes = 2;
			params.putString("distance", "1000");
		}else if(2== i){
			mCheckTimes = 3;
			params.putString("distance", "10000");
		}else{
			mCallBack.onCheckInFail();
			return;
		}
		params.putString(Facebook.TOKEN, SessionStore.getCacheAccessToken(mContext));
		com.fihtdc.smartbracelet.facebook.Utility.initReqEnviroment(mContext);
		com.fihtdc.smartbracelet.facebook.Utility.mAsyncRunner.request("search", params,
				new placesRequestListener());

	}

	
	public CheckInCallBack getmCallBack() {
		return mCallBack;
	}

	public void setmCallBack(CheckInCallBack mCallBack) {
		this.mCallBack = mCallBack;
	}

}
