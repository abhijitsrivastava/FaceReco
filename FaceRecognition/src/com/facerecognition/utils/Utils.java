package com.facerecognition.utils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class Utils {

	public static final String KEY_USERNAME = "username";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_URL = "server_url";
	public static final String KEY_MODE = "streaming_mode";
	public static final String KEY_GLASS_ID = "glass_id";
	
	private static final String TAG = "EduShield";
	

	public static void saveStringPreferences(Context context, String key,
			String value) {
		SharedPreferences sPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sPrefs.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static void deleteStringPreferences(Context context, String key) {
		SharedPreferences sPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sPrefs.edit();
		editor.remove(key);
		editor.commit();
	}

	public static String getStringPreferences(Context context, String key) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String savedPref = sharedPreferences.getString(key, "");
		return savedPref;
	}

	public static void showToast(final Activity context, final String message) {
		context.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(context, message, Toast.LENGTH_LONG)
						.show();
			}
		});
	}
	
	public static HttpResponse makeRequest(String url, JSONObject params) throws ClientProtocolException, IOException {
		
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

        //passes the results to a string builder/entity
        StringEntity se = new StringEntity(params.toString());

        //sets the post request as the resulting string
        httppost.setEntity(se);
        
        //sets a request header so the page receving the request
        //will know what to do with it
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("Content-type", "application/json");
        httppost.setHeader("X-Mashape-Key",
				"gMoueY07U1mshpinwogEUEk0B13Qp1TUNLejsnkH2IwTJ9iEWF");
		
        // Execute HTTP Post Request
        return httpclient.execute(httppost);
	}
	
		
	public static void dLog(String message) {
		Log.d(TAG, message);
	}
	
	public static void eLog(String message) {
		Log.e(TAG, message);
	}

}
