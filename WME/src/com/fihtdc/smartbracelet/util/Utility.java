package com.fihtdc.smartbracelet.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.activity.AlertDialogActivity;
import com.fihtdc.smartbracelet.activity.PairActivity;
import com.fihtdc.smartbracelet.fragment.AlertDialogFragment;
import com.fihtdc.smartbracelet.provider.BraceletInfo.Coaching;
import com.fihtdc.smartbracelet.provider.BraceletInfo.DayCoaching;
import com.fihtdc.smartbracelet.provider.BraceletInfo.Measure;
import com.fihtdc.smartbracelet.provider.BraceletInfo.Profile;

public class Utility {

    public static final String SHARED_PREFS_NAME = "com.fihtdc.smartbracelet_preferences";
    public static final String BLE_MAC = "BLE_ADRESS";
    public static final String BLE_NAME = "BLE_NAME";
    
    public static final String FB_ACCOUNT_NAME = "ACCOUNT_NAME";
    public static final String FB_NOTIFICATION_COUNT = "NOTIFICATION_COUNT";
    
    private static final String[] COLUMNS_TO_SHOW = new String[] {
        Constants.NUM_UNREAD_CONVERSATIONS };
    /**
     * @author RobinPei at 2013/7/24
     * @return SharedPreferences
     * @param @param context
     * @param @return
     * @Description: get shared preference
     * @throws 
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * @author RobinPei at 2013/7/24
     * @return boolean
     * @param @param context
     * @param @param key
     * @param @param defaultValue
     * @param @return
     * @Description: get the preference value with the given key
     * @throws 
     */
    public static boolean getSharedPreferenceValue(Context context, String key, boolean defaultValue) {
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getBoolean(key, defaultValue);
    } 
    
    /**
     * @author RobinPei at 2013/7/24
     * @return int
     * @param @param context
     * @param @param key
     * @param @param defaultValue
     * @param @return
     * @Description: get the preference value with the given key
     * @throws 
     */
    public static int getSharedPreferenceValue(Context context, String key, int defaultValue) {
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getInt(key, defaultValue);
    } 
    
    public static String getSharedPreferenceValue(Context context, String key, String defaultValue) {
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getString(key, defaultValue);
    } 
    /**
     * @author RobinPei at 2013/7/24
     * @return boolean
     * @param @param context
     * @param @param key
     * @param @return
     * @Description: return true if the sharepreference xml file contain the given key
     * @throws 
     */
    public static boolean isSharedPreferenceContainKey(Context context, String key) {
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.contains(key);
    } 
    
    /**
     * Asynchronously sets the preference with the given key to the given value
     *
     * @param context the context to use to get preferences from
     * @param key the key of the preference to set
     * @param value the value to set
     */
    public static void setSharedPreferenceValue(Context context, String key, boolean value) {
        SharedPreferences prefs = getSharedPreferences(context);
        prefs.edit().putBoolean(key, value).commit();
    }
    
    /**
     * Asynchronously sets the preference with the given key to the given value
     *
     * @param context the context to use to get preferences from
     * @param key the key of the preference to set
     * @param value the value to set
     */
    public static void setSharedPreferenceValue(Context context, String key, int value) {
        SharedPreferences prefs = getSharedPreferences(context);
        prefs.edit().putInt(key, value).commit();
    }
    
    /**
     * Asynchronously sets the preference with the given key to the given value
     *
     * @param context the context to use to get preferences from
     * @param key the key of the preference to set
     * @param value the value to set
     */
    public static void setSharedPreferenceValue(Context context, String key, String value) {
        SharedPreferences prefs = getSharedPreferences(context);
        prefs.edit().putString(key, value).commit();
    }
    /**
     * @author RobinPei at 2013/7/29
     * @return void
     * @param @param context
     * @param @param key
     * @Description: clear shared preference values
     * @throws 
     */
    public static void clearSharedPreferenceValue(Context context){
        SharedPreferences prefs = getSharedPreferences(context);
        prefs.edit().clear().commit();
    }
    
    /**
     * @author RobinPei at 2013/7/30
     * @return boolean
     * @param @param context
     * @param @return
     * @Description: remove all tables data
     * @throws 
     */
    public static void clearTablesData(Context context){
        //return context.deleteDatabase("bracelet.db");
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(Profile.CONTENT_URI, null, null);
        resolver.delete(Coaching.CONTENT_URI, null, null);
        resolver.delete(Measure.CONTENT_URI, null, null);
        resolver.delete(DayCoaching.CONTENT_URI, null, null);
    }
    /**
     * @return Bitmap
     * @param Activity
     * @Description: Get the current activity screen shot(not include status bar
     *               and title bar)
     */
    public static Bitmap getScreenshot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        int contentTop = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        Display display = activity.getWindowManager().getDefaultDisplay();
        view.layout(0, 0, display.getWidth(), display.getHeight());
        view.setDrawingCacheEnabled(true);
        
        Bitmap full = Bitmap.createBitmap(view.getDrawingCache());
        Bitmap content = Bitmap.createBitmap(full, 0, contentTop, full.getWidth(), full.getHeight()
                - contentTop);
        recycleBitmap(full);
        //Disable cache to not save cache after get cache
        view.setDrawingCacheEnabled(false);
        return content;
    }
    
    /**
     * @return Bitmap
     * @param view
     * @Description: Get the current view shot
     */
    public static Bitmap getScreenshot(View view) {
        if (view == null) {
            LogApp.Loge("View is null!");
            return null;
        }
        view.setDrawingCacheEnabled(true);
        Bitmap content = Bitmap.createBitmap(view.getDrawingCache());
        /*storeBitmap(content, Constants.APP_SDCARD_PATH,
                formatDateTime(System.currentTimeMillis()));*/
        view.setDrawingCacheEnabled(false);
        return content;
    }
    
    /**
     * @return void
     * @param Activity
     * @Description: Share screen shot
     */
    public static void shareScreenhot(Activity activity) {
        Bitmap bm = getScreenshot(activity);
        Uri uri = storeBitmap(bm, Constants.APP_SDCARD_PATH,
                formatDateTime(System.currentTimeMillis()));
        if (uri != null) {
            shareImage(uri, activity);
        } else {
            LogApp.Loge("ShareScreenhot Uri is null!");
        }
        recycleBitmap(bm);
    }
    
    
    /**
     * @author F3060326 at 2013/9/10
     * @return void
     * @param @param bp
     * @Description: recycle bitmap 
     */
    public static void recycleBitmap(Bitmap bp){
        if(null!=bp && !bp.isRecycled()){
            bp.recycle();
            bp = null;
        }
    }
    /**
     * @return void
     * @param Uri
     * @param Activity
     * @Description: Share image
     */
    public static void shareImage(Uri uri, Activity activity) { 
        Intent shareIntent = new Intent(Intent.ACTION_SEND); 
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri); 
        shareIntent.setType("image/*"); 
        activity.startActivity(Intent.createChooser(shareIntent, activity.getTitle())); 
    } 
    
    /**
     * @return void
     * @param bitmap
     * @param fileDir: file directory
     * @param fileName: file name
     * @Description: Store bitmap
     */
    public static Uri storeBitmap(Bitmap bitmap, String fileDir, String fileName) {
        File file = new File(fileDir);
        Uri uri = null;
        
        if (bitmap == null) {
            LogApp.Loge("Screenshot bitmap is null!");
            return null;
        }
        
        if (!file.exists()) {
            file.mkdir();
        }
        
        File imageFile = new File(file, fileName + ".jpg");
        FileOutputStream fos = null;
        try {
            if (imageFile.exists()) {
                imageFile.delete();
            }
            fos = new FileOutputStream(imageFile);
            bitmap.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();
            uri = Uri.fromFile(imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return uri;
    }
    
    public static String getDisplayDate(long millis, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(new Date(millis));
    }
    
    public static String getDisplayTime(Context context,long millis, int flags){
        return DateUtils.formatDateTime(context, millis, flags);
    }
    
    /**
     * @return void
     * @param string
     * @param color: file directory
     * @Description: Format text color of number except zero in string to color
     */
    public static SpannableString formatStringColor(String string, int color){
        SpannableString span = new SpannableString(string);
        String pattern = "[1-9][0-9]*+";
        Pattern change = Pattern.compile(pattern);
        Matcher m = change.matcher(span.toString());
        while (m.find()) {
            span.setSpan(new ForegroundColorSpan(color), m.start(), m.end(), 
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        
        return span;
    }
    
    public static String getCurrentProfileName(Context context) {
        String name = null;
        Cursor cursor = context.getContentResolver().query(Profile.CONTENT_URI, null, null, null,
                null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex(Profile.COLUMN_NAME_PROFILE_NAME));
            }

            cursor.close();
        }

        return name;
    }
    public static boolean isInternetWorkValid(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                return true;
            } else {
                return false;
            }
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * @author RobinPei at 2013/8/12
     * @return int
     * @param @param hit
     * @param @param cycle
     * @param @return
     * @param @throws Exception
     * @Description: acculate the hit rate,then show different style star.
     * the rule is:
     * 0 stars : 0~25%
     * 1 stars : 25~50%
     * 2 stars : 50~75%
     * 3 stars : 75~100%
     * @throws 
     */
    public static int getHitStarNum(int hit,int cycle){
        if(cycle == 0){
            return cycle;
        }
        float rate = (float)hit/cycle;
        int value = 0;
        if(rate < 0.25){
            value = 0;
        }else if(rate >= 0.25 && rate < 0.5){
            value = 1;
        }else if(rate >= 0.5 && rate < 0.75){
            value = 2;
        }else if(rate >= 0.75){
            value = 3;
        }else{
            value = 0;
        }
        return value;
    }
    
    /**
     * @author RobinPei at 2013/8/12
     * @return int
     * @param @param hit
     * @param @param cycle
     * @param @return
     * @Description: acculate the hit rate
     * @throws 
     */
    public static int getHitRate(int hit,int cycle){
        int rate = 0;
        if(cycle != 0){
            rate = (int)((hit*100/cycle));
        }
        return rate;
    }
    
    /**
     * @author RobinPei at 2013/8/16
     * @return Cursor
     * @param @param context
     * @param @param account
     * @param @return
     * @Description: read Gmail provider
     * @throws 
     */
    public static Cursor getUnReadGmailCursor(Context context, String account) {
        //LogApp.Logd(TAG, "str=="+account);
        final Uri labelsUri = Uri.parse("content://com.google.android.gm/"+account+"/labels");
        return context.getContentResolver().query(labelsUri, COLUMNS_TO_SHOW, null, null, null);
    }
    
    /**
     * @author RobinPei at 2013/8/16
     * @return int
     * @param @param context
     * @param @param account
     * @param @return
     * @Description: get a given account's unread email count
     * @throws 
     */
    public static int getUnreadGmailCount(Context context,String account){
        Cursor cursor = Utility.getUnReadGmailCursor(context, account);
        int count = 0;
        if(null != cursor){
            if(cursor.moveToFirst()){
                //LogApp.Logd(TAG, "###cursor.count=="+cursor.getCount());
                //LogApp.Logd(TAG, "Unread count=="+cursor.getInt(cursor.getColumnIndex("numUnreadConversations")));
                count = cursor.getInt(cursor.getColumnIndex(Constants.NUM_UNREAD_CONVERSATIONS));
            }
            cursor.close();
            cursor = null;
        }
        return count;
    }
    
    /**
     * @author Robin at 2013/8/16
     * @return int
     * @param @param context
     * @param @param accounts
     * @param @return
     * @Description: calculate all gmail accounts' total unread email count
     * @throws 
     */
    public static int  onAccountResults(Context context,Account[] accounts) {
        //Log.i(TAG, "received accounts: " + Arrays.toString(accounts));
        int total = 0;
        if (accounts != null && accounts.length > 0) {
            // Pick the first one, and calculate the total unread gmail count
            for(int i = 0;i < accounts.length;i++){
                final String account = accounts[i].name;
                total = total + Utility.getUnreadGmailCount(context,account);
            }
        }
        LogApp.Logd("BraceletReceiver", "total=="+total);
        return total;
    }
    
    public static AlertDialog onCreateAccessPositionDialog(final Context context){
    	return new AlertDialog.Builder(context.getApplicationContext())
    	          .setIconAttribute(android.R.attr.alertDialogIcon)
    	          .setTitle(R.string.setting_network_open)
    	          .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
    	              public void onClick(DialogInterface dialog, int whichButton) {
    	            	  Intent intentd = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    	            	  intentd.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	            	  context.startActivity(intentd);
    	                  /* User clicked OK so do some stuff */
    	              }
    	          })
    	          .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
    	              public void onClick(DialogInterface dialog, int whichButton) {

    	                  /* User clicked Cancel so do some stuff */
    	              }
    	          })
    	          .create();
    }
    
    
    /**
     * @author F3060326 at 2013/8/21
     * @return void
     * @param @param context
     * @Description: start to pair BT first
     * @throws 
     */
    public static void startPaired(Context context){
        Intent intent = new Intent(context,PairActivity.class);
        context.startActivity(intent);
    }
    
    public static void startPairedForResult(Context context, Fragment fragment){
        Intent intent = new Intent(context,PairActivity.class);
        fragment.startActivityForResult(intent, PairActivity.REQUEST_PAIR_CODE);
    }
    
    public static String formatDateTime(long currentTimeMillis) {
        Time time = new Time();
        time.set(currentTimeMillis);
        return time.format("%Y%m%dT%H%M%SZ");
    }
    
    /**
     * @author RobinPei at 2013/4/9
     * @return boolean
     * @param @param path:the folder's absolute path,for example:"/storage/emulated/0/SmartBracelet/"
     * @param @return
     * @Description: Delete all files which are in the folder
     * @throws 
     */
    public static boolean delAllFile(String path) {
         boolean flag = false;
         File file = new File(path);
         if (!file.exists()) {
           return flag;
         }
         if (!file.isDirectory()) {
           return flag;
         }
         String[] tempList = file.list();
         File temp = null;
         for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
               temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
                flag = true;
             }
            if (temp.isDirectory()) {
               delAllFile(path + "/" + tempList[i]);//delete file
               delFolder(path + "/" + tempList[i]); // having folder and delete it
               flag = true;
            }
         }
         return flag;
       }
    
    /**
     * @author RobinPei at 2013/4/9
     * @return void
     * @param @param folderPath
     * @Description: delete the folder with the given absolute path
     * @throws 
     */
    public static void delFolder(String folderPath) {
        try {
           delAllFile(folderPath); //delete all files
           String filePath = folderPath;
           filePath = filePath.toString();
           java.io.File myFilePath = new java.io.File(filePath);
           myFilePath.delete(); //delete the folder which is in the folder
        } catch (Exception e) {
          e.printStackTrace(); 
        }
   }
    
    public static boolean isActivityLive(Activity activity){
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
           return false; 
        }
        return true;
    }
    
    public static boolean isFragmentLive(Fragment fragment) {
        if (fragment != null && fragment.isAdded() && !fragment.isDetached()
                && fragment.getActivity() != null) {
            return true;
        }
        return false;
    }
    
    public static boolean isBTConnect(){
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if(null == adapter){
            return false;
        }
        return adapter.isEnabled();
    }
    
    
    public static boolean startBTEnable(Activity context,BluetoothAdapter adapter,int requestCode ){
        boolean isStart = false;
        if (adapter == null || !adapter.isEnabled()) {
            LogApp.Logd("initBTAdapter");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity) context).startActivityForResult(enableBtIntent, requestCode);
            isStart = true;
        }
        return isStart;
    }
    
    /**
     * @return void
     * @param @param activity
     * @param @param type
     * @param @param bundle
     * @Description: Show the type of dialog
     * @throws
     */
    public static void showAlertDialog(Activity activity, int type, Bundle bundle) {
        if (!isActivityLive(activity)) {
            return;
        }

        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        Fragment prev;
        DialogFragment newFragment;

        Bundle args;
        if (bundle != null) {
            args = bundle;
        } else {
            args = new Bundle();
        }

        prev = activity.getFragmentManager().findFragmentByTag(String.valueOf(type));
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        newFragment = new AlertDialogFragment();
        args.putInt("type", type);
        newFragment.setArguments(args);
        newFragment.show(ft, String.valueOf(type));
    }
    
    
    /**
     * @author F3060326 at 2013/9/2
     * @return Point
     * @param @param context
     * @param @return Point
     * @Description: get current window size for draw and animation in different size
     */
    public static Point getCurrentWindowSize(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        return size;
    }
    
    /**
     * @author F3060326 at 2013/9/5
     * @return String
     * @param @param context
     * @param @param score
     * @param @param agsage
     * @param @param userage
     * @param @param BPM
     * @param @return
     * @Description: get agility description 
     */
    public static String getAgilityDescrption(Context context,int score,int agsage,int userage,int BPM){
        
        Resources rs = context.getResources();
        String[] string = rs.getStringArray(R.array.agility_level);
        int scoreindex = 2;
        if(score > 79){
            scoreindex = 0;
        }else if(score < 59){
            scoreindex = 1;
        }
        
        String[] string_age = rs.getStringArray(R.array.agility_level_age);
        int levelageindex = 0;
        if(agsage > userage){
            levelageindex = 1;
        }else if(agsage < userage){
            levelageindex = 2;
        }
        return String.format(string[scoreindex], score,agsage,string_age[levelageindex],BPM);
    }
    
    
    public static String getANSDescrption(Context context,int agsage,int userage){
        
        Resources rs = context.getResources();
        String[] string = rs.getStringArray(R.array.ans_level);
        int scoreindex = 0;
        int agsGap = agsage - userage;
        if(agsGap > 0){
            scoreindex = 2;
        }else if(agsGap < 0){
            scoreindex = 1;
        }
        
        String[] string_age = rs.getStringArray(R.array.ans_level_age);
        String one;
        if(agsGap < -1){
            one = String.format(string_age[3], Math.abs(agsGap));
        }else if(agsGap > 1){
            one = String.format(string_age[1], agsGap);
        }else if(agsGap > 0){
            one = string_age[0];
        }else {
            one = string_age[2];
        }
        
        String string_end = rs.getString(R.string.suggestion_ans_end);
        
        if(2 == scoreindex){
            return String.format(string[scoreindex], agsage,one) + string_end;
        }else{
            return String.format(string[scoreindex], one) + string_end;
        }
        
    }
    
    public static String getBPMDescrption(Context context){
        Resources rs = context.getResources();
        return  rs.getString(R.string.suggestion_bpm);
    }
    
    
    /**
     * @return int: if -1 return, means parameter error or birthday after today
     * @param @param birthday: format 2013,10,10
     * @Description: get the age
     */
    public static int getAge(String birthday) {
        int age = -1;
        if (!TextUtils.isEmpty(birthday)) {
            String[] values = birthday.split(",");
            if (values.length == 3) {
                int year = Integer.valueOf(values[0]);
                int month = Integer.valueOf(values[1]) - 1;
                int day = Integer.valueOf(values[2]);

                Calendar now = Calendar.getInstance();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);

                // Can not set birthday after today
                if (calendar.after(now)) {
                    age = -1;
                    return age;
                }

                age = Calendar.getInstance().get(Calendar.YEAR) - year;

                if (age > 0) {
                    if (now.get(Calendar.MONTH) < month
                            || (now.get(Calendar.MONTH) == month && now.get(Calendar.DAY_OF_MONTH) < day)) {
                        age--;
                    }
                }
            }
        }
        return age;
    }
    
    public static void startAlertDialogActivity(Activity activity, Bundle bundle){
        if (!Utility.isActivityLive(activity)) {
            return;
        }
        
        Intent intent = new Intent(activity, AlertDialogActivity.class);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }
    
    public static int transformAgility(int aglity) {
        final int AGILITY_MAX = 100;
        final int AGILITY_MIN = 30;
        return (int) (AGILITY_MIN + aglity * (AGILITY_MAX - AGILITY_MIN) / 100.0f);
    }
    
    /**
     * @author F3060326 at 2013/12/12
     * @return boolean
     * @param @param wm
     * @param @return
     * @Description: check show WIFI or close WIFI command
     */
    public static boolean wifiCondition(WifiManager wm){
        
        boolean isWifiClose = false;
        if(null == wm){
            return isWifiClose;
        }
        if(wm.isWifiEnabled() /*&& Build.VERSION.SDK_INT<19 */
                && !Build.MANUFACTURER.contains("FIH")){
            isWifiClose = true; 
        }
        
        return isWifiClose;
    }
}
