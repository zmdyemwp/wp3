package com.fihtdc.smartbracelet.facebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;

import android.os.Bundle;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

public class WMEAsyncFacebookRunner extends AsyncFacebookRunner {
    Facebook fb;
	public WMEAsyncFacebookRunner(Facebook facebook) {
		super(facebook);
		fb =  facebook;
	}

	@Deprecated
	public void requestBatch(String[] graphPath, WMERequestListener listener,
			final Object state) {
		requestBatch(graphPath, new Bundle(), "GET", listener, state);
	}

	@Deprecated
	public void requestBatch(String graphPath[], Bundle parameters,
			WMERequestListener listener) {
		requestBatch(graphPath, parameters, "GET", listener, /* state */null);
	}

	/**
	 * Make a request to the Facebook Graph API with the given HTTP method and
	 * string parameters. Note that binary data parameters (e.g. pictures) are
	 * not yet supported by this helper function.
	 * <p/>
	 * See http://developers.facebook.com/docs/api
	 * <p/>
	 * Note that this method is asynchronous and the callback will be invoked in
	 * a background thread; operations that affect the UI will need to be posted
	 * to the UI thread or an appropriate handler.
	 * <p/>
	 * This method is deprecated. See {@link Facebook} and
	 * {@link com.facebook.Request} for more info.
	 * 
	 * @param graphPath
	 *            Path to resource in the Facebook graph, e.g., to fetch data
	 *            about the currently logged authenticated user, provide "me",
	 *            which will fetch http://graph.facebook.com/me
	 * @param parameters
	 *            key-value string parameters, e.g. the path "search" with
	 *            parameters {"q" : "facebook"} would produce a query for the
	 *            following graph resource:
	 *            https://graph.facebook.com/search?q=facebook
	 * @param httpMethod
	 *            http verb, e.g. "POST", "DELETE"
	 * @param listener
	 *            Callback interface to notify the application when the request
	 *            has completed.
	 * @param state
	 *            An arbitrary object used to identify the request when it
	 *            returns to the callback. This has no effect on the request
	 *            itself.
	 */
	@Deprecated
	public void requestBatch(final String[] graphPath, final Bundle parameters,
			final String httpMethod, final WMERequestListener listener,
			final Object state) {
		new Thread() {
			@Override
			public void run() {
				Log.i("Fly", "requestBatch notification");
					int requestURLTimes = graphPath.length;
					String[] response = new String[requestURLTimes];
					
					for (int i = 0; i < requestURLTimes; i++) {
						String resp =(String) invokeMethodOrThrow(Facebook.class,fb,"requestImpl",   new Class<?>[] { String.class,Bundle.class,String.class },
                                new Object[] {graphPath[i], parameters,httpMethod});
						response[i] = resp;
					}
					listener.onBachReqComplete(response, state);
		}
	}.start();}
	 public static Object invokeMethodOrThrow(Object obj, String methodName,
	            Class<?>[] parameterTypes, Object[] args) {
	        return invokeMethodOrThrow(obj.getClass(), obj, methodName, parameterTypes, args);
	    }

	 public static Object invokeMethodOrThrow(Class<?> clazz, Object obj, String methodName,
	            Class<?>[] parameterTypes, Object[] args) {
	        Object ret = null;
	        try {
	            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
	            if (method != null) {
	                if (!method.isAccessible()) {
	                    method.setAccessible(true);
	                }
	                ret = method.invoke(obj, args);
	            }
	        } catch (Exception e) {
	          Log.i("Fly", "batch request facebook notification exception======="+e.getMessage());
	        }

	        return ret;
	    }
}
