package com.facerecognition.utils;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.facerecognition.ShowIdentityActivity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

public class FetchIdentityTask extends AsyncTask<String, String, String> {

	private Context context;

	public FetchIdentityTask(Activity context) {
		this.context = context;
	}

	@Override
	protected String doInBackground(String... params) {

		String responseString = "";

		try {

			// These code snippets use an open-source library.
			// http://unirest.io/java
			/*
			 * HttpResponse<JsonNode> response1 = Unirest
			 * .post("https://lambda-face-recognition.p.mashape.com/recognize")
			 * .header("X-Mashape-Key",
			 * "gMoueY07U1mshpinwogEUEk0B13Qp1TUNLejsnkH2IwTJ9iEWF")
			 * .field("album", "TEST_NEW") .field("albumkey",
			 * "7bcc2ac04255c7d4859547b05120c2535e84f624b72f9dd42959a0932a91e557"
			 * ) .field("files", new File("IMG_20141010_114308239.jpg"))
			 * .asJson();
			 */

			// convert parameters into JSON object
			JSONObject holder = new JSONObject();
			holder.put(Constants.KEY_ALBUM, Constants.VALUE_ALBUM);
			holder.put(Constants.KEY_ALBUM_KEY, Constants.VALUE_ALBUM_KEY);
			holder.put(Constants.KEY_FILES, new File(params[0]));

			// Execute HTTP Post Request
			HttpResponse response = Utils.makeRequest(
					Constants.FETCH_IDENTITY_URL, holder);
			if (response != null) {
				responseString = EntityUtils.toString(response.getEntity());
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseString;
	}

	@Override
	protected void onPostExecute(String result) {
		Utils.eLog("response" + result);
		super.onPostExecute(result);
		if (!"".equals(result)) {
			try {
				JSONObject response = new JSONObject(result);
				JSONArray jsonArrayPhotos = response.getJSONArray("photos");
				JSONObject jsonObjectPhotosOne = (JSONObject) jsonArrayPhotos
						.get(0);
				JSONArray jsonArrayTags = jsonObjectPhotosOne
						.getJSONArray("tags");
				JSONObject jsonObjectTagsOne = (JSONObject) jsonArrayTags
						.get(0);
				JSONArray jsonArrayUids = jsonObjectTagsOne
						.getJSONArray("uids");
				JSONObject jsonObjectUidsOne = (JSONObject) jsonArrayUids
						.get(0);
				String prediction = jsonObjectUidsOne.optString("prediction");

				((ShowIdentityActivity) context)
						.onCompleteUpdateStatusRequest(prediction);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Utils.dLog("Getting empty response");
		}
	}
}
