package com.fihtdc.smartbracelet.util;

import android.util.Log;

public class LogApp {
    public static final String TAG = "SmartBracelet";
    private static final boolean DEBUG = true;

    /**
     * @author JimmyChen at 2011/04/27
     * @return void
     * @param @param content
     * @Description: show all log v type by HunanTV TAG
     */
    public static final void Logv(String content) {
        if (DEBUG) {
            Log.v(TAG, content);
        }
    }

    /**
     * @author JimmyChen at 2011/04/27
     * @return void
     * @param tag
     * @param content
     * @Description: show all log v type by @param tag
     */
    public static final void Logv(String tag, String content) {
        if (DEBUG) {
            Log.v(tag, content);
        }
    }

    /**
     * @author JimmyChen at 2011/04/27
     * @return void
     * @param @param content
     * @Description: show all log e type by HunanTV TAG
     */
    public static final void Loge(String content) {
        Log.e(TAG, content);
    }

    /**
     * @author JimmyChen at 2011/04/27
     * @return void
     * @param tag
     * @param content
     * @Description: show all log e type by @param tag
     */
    public static final void Loge(String tag, String content) {
        Log.e(tag, content);
    }

    public static final void Logi(String content) {
        Logi(TAG, content);
    }

    public static final void Logi(String tag, String content) {
        if (DEBUG) {
            Log.i(tag, content);
        }
    }

    public static final void Logw(String content) {
        Logw(TAG, content);
    }

    public static final void Logw(String tag, String content) {
        if (DEBUG) {
            Log.w(tag, content);
        }
    }

    public static final void Logd(String content) {
        Logd(TAG, content);
    }

    public static final void Logd(String tag, String content) {
        if (DEBUG) {
            Log.d(tag, content);
        }
    }

}