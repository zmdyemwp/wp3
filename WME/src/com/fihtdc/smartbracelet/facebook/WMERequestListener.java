package com.fihtdc.smartbracelet.facebook;

import com.facebook.android.AsyncFacebookRunner.RequestListener;

public interface WMERequestListener extends RequestListener{

	  /**
     * Called when a request completes with the given response.
     *
     * Executed by a background thread: do not update the UI in this method.
     */
    public void onBachReqComplete(String[] response, Object state);
}
