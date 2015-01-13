/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fihtdc.smartbracelet.facebook;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.provider.MediaStore;
import android.util.Log;

import com.facebook.android.Facebook;
import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.util.Constants;

public class Utility extends Application {

    public static Facebook mFacebook;
    @SuppressWarnings("deprecation")
    public static WMEAsyncFacebookRunner mAsyncRunner;
    public static String userUID = null;
    public static String objectID = null;
    public static AndroidHttpClient httpclient = null;
    public static Hashtable<String, String> currentPermissions = new Hashtable<String, String>();
    private static int MAX_IMAGE_DIMENSION = 720;
    // error code
    private static final int EC_PERMISSION_DENIED = 10;
    private static final int EC_INVALID_SESSION = 102;
    private static final int EC_INVALID_TOKEN = 190;
    private static final int EC_INVALID_PERMISSION = 200;
    private static final int EC_LACK_OF_TOKEN = 104;
    
    public static Bitmap getBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(new FlushedInputStream(is));
            bis.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpclient != null) {
                httpclient.close();
            }
        }
        return bm;
    }

    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break; // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

    public static byte[] scaleImage(Context context, Uri photoUri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(context, photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight; 
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
            float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
            float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        String type = context.getContentResolver().getType(photoUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (type.equals("image/png")) {
            srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        } else if (type.equals("image/jpg") || type.equals("image/jpeg")) {
            srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        }
        byte[] bMapArray = baos.toByteArray();
        baos.close();
        return bMapArray;
    }

    public static int getOrientation(Context context, Uri photoUri) {
        /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        int orientation = cursor.getInt(0);
        cursor.close();

        return orientation;
    }
    
    public static boolean isSessionValid(Context context){
    	if(null == mFacebook){
    		return false;
    	}else{
    		if(!com.fihtdc.smartbracelet.util.Utility.getSharedPreferenceValue(context, com.fihtdc.smartbracelet.util.Utility.FB_ACCOUNT_NAME, "").equals("")){
    			return  true;
    		}else{
    			return false;
    		}
    		
    	}
    }
    public static  void initReqEnviroment(Context context) {
		if(com.fihtdc.smartbracelet.facebook.Utility.mFacebook == null || com.fihtdc.smartbracelet.facebook.Utility.mAsyncRunner == null){
			 // Create the Facebook Object using the app id.
			com.fihtdc.smartbracelet.facebook. Utility.mFacebook = new Facebook(context.getString(R.string.applicationId));
	        // Instantiate the asynrunner object for asynchronous api calls.
			com.fihtdc.smartbracelet.facebook.Utility.mAsyncRunner = new WMEAsyncFacebookRunner(com.fihtdc.smartbracelet.facebook.Utility.mFacebook);
			com.fihtdc.smartbracelet.facebook.Utility.mFacebook.setTokenFromCache(SessionStore.getCacheAccessToken(context), 
	        		SessionStore.getCacheExpires(context), SessionStore.getCacheLastUpdate(context));
		}
		
	}
    
    public static boolean checkRespLegitimate(String response,Context context){
    	if (null != response && response.contains("OAuthException")) {
			try {
				Log.i("Fly","Error Session Response ======"+response);
				JSONObject jsonBody = new JSONObject(response);
				if (jsonBody.has("error")) {
					// We assume the error object is correctly formatted.
					JSONObject error;
					error = (JSONObject) com.facebook.internal.Utility.getStringPropertyAsJSON(
							jsonBody, "error", null);
					int errorCode = error.getInt("code");
					Log.i("Fly", "errorCode======"+errorCode);
					return com.fihtdc.smartbracelet.facebook.Utility.reOuthFBAccount(errorCode, context);
				}else{
					return true;
				}
			} catch (JSONException e) {
			       return true;
			}
		}else{
			if(null!=response){
				return true;
			}else{
				return false;
			}
		}
    }
    public static boolean reOuthFBAccount(int errorCode,Context context){
    	if(errorCode == EC_PERMISSION_DENIED || errorCode == EC_INVALID_SESSION 
    			|| errorCode==EC_INVALID_TOKEN || errorCode ==EC_INVALID_PERMISSION || errorCode ==EC_LACK_OF_TOKEN){
    		Log.i("Fly", "reOuthFBAccount errorCode===="+errorCode);
    		if(SessionEvents.isReOutListener()){
        		SessionEvents.onReOuthAction();
        	}else{
        		Intent intent = new Intent();
            	ComponentName name = new ComponentName("com.fihtdc.smartbracelet", "com.fihtdc.smartbracelet.activity.FaceBookSettingActivity");
            	intent.setComponent(name);
            	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            	intent.putExtra(Constants.ACCOUNT_IS_REOUTH, true);
            	context.startActivity(intent);
        	}
        	return false;
    	}
    	return true;
    }
    
    
}
