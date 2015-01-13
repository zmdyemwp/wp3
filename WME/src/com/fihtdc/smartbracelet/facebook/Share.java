package com.fihtdc.smartbracelet.facebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.fihtdc.smartbracelet.R;

public class Share {
	private static final String SHARE_REQUEST_URL ="me/photos";
	public static final String BUNDLE_MESSAGE_KEY ="message";
	public static final String BUNDLE_PHOTO_KEY ="photo";
	public static Share  mShare =null;
	private ShareResultListener mShareResultListener;
	private Context mContext;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			Log.i("Fly", "NetWork request timeout");
			if(null!=mShareResultListener){
				mShareResultListener.onShareResult(false);
			}
			super.handleMessage(msg);
		}
		
	};
	public interface ShareResultListener{
		void onShareResult(boolean isSuccess);
	}
	
	private Share(Context context){
		mContext = context;
	}
	
	public static Share getShareInstance(Context context){
		if(mShare == null){
			mShare = new Share(context);
		}
		return mShare;
	}
	public void onShareToFb(Context context ,String message,String profileName,byte[] bytes,ShareResultListener resultListner) {
		mShareResultListener = resultListner;
		if(com.fihtdc.smartbracelet.facebook.Utility.mFacebook == null){
			mShareResultListener.onShareResult(false);
			return;
		}
		mHandler.removeMessages(0);
		mHandler.sendEmptyMessageDelayed(0, 100*1000);
		Bundle bundleParam = new Bundle();
		bundleParam.putString(BUNDLE_MESSAGE_KEY,
				         profileName
						+context.getString(R.string.facebook_share_message_prefix)
						+com.fihtdc.smartbracelet.util.Utility.getDisplayDate(System.currentTimeMillis(), "yyyy-MM-dd HH:mm")
						+"\r\n"+message);
		bundleParam.putByteArray(BUNDLE_PHOTO_KEY ,bytes);
		bundleParam.putString("metadata", "1");
		bundleParam.putString(Facebook.TOKEN, SessionStore.getCacheAccessToken(context));
		com.fihtdc.smartbracelet.facebook.Utility.initReqEnviroment(mContext);
		com.fihtdc.smartbracelet.facebook.Utility.mFacebook.setTokenFromCache(SessionStore.getCacheAccessToken(context), 
        		SessionStore.getCacheExpires(context), SessionStore.getCacheLastUpdate(context));
		Utility.mAsyncRunner.request(SHARE_REQUEST_URL, bundleParam, "POST",
				new SharePhotoListener (),null);
		
	}
	
	public boolean isSupportFBShare(){
		com.fihtdc.smartbracelet.facebook.Utility.initReqEnviroment(mContext);
		if(com.fihtdc.smartbracelet.facebook.Utility.isSessionValid(mContext)){
			return true;
		}else{
			return false;
		}
		
	}
	
	
	 public class SharePhotoListener extends BaseRequestListener {

		@Override
		public void onComplete(String response, Object state) {
			Log.i("Fly", "Response ======"+response);
			mHandler.removeMessages(0);
			boolean isLegal =com.fihtdc.smartbracelet.facebook.Utility.checkRespLegitimate(response,mContext);
			if(!isLegal){
				mShareResultListener.onShareResult(false);
				return;
			}
			if(response.contains("post_id")){
				mShareResultListener.onShareResult(true);
			}else{
				mShareResultListener.onShareResult(false);
			}
		}
		@Override
	    public void onFacebookError(FacebookError e, final Object state) {
	        Log.e("Facebook", e.getMessage());
	    	mHandler.removeMessages(0);
	    	mShareResultListener.onShareResult(false);
	    }

	    @Override
	    public void onFileNotFoundException(FileNotFoundException e, final Object state) {
	        Log.e("Facebook", e.getMessage());
	    	mHandler.removeMessages(0);
	    	mShareResultListener.onShareResult(false);
	    }

	    @Override
	    public void onIOException(IOException e, final Object state) {
	        Log.e("Facebook", e.getMessage());
	    	mHandler.removeMessages(0);
	    	mShareResultListener.onShareResult(false);
	    }

	    @Override
	    public void onMalformedURLException(MalformedURLException e, final Object state) {
	        Log.e("Facebook", e.getMessage());
	    	mHandler.removeMessages(0);
	    	mShareResultListener.onShareResult(false);
	    }
		

	      
}}
