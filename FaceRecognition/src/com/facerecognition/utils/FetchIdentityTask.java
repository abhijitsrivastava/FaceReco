package com.facerecognition.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.android.facerecognition.ShowIdentityActivity;
import com.cloudinary.Cloudinary;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class FetchIdentityTask extends AsyncTask<String, String, String> {

	private Context context;

	public FetchIdentityTask(Activity context) {
		this.context = context;
	}

	@Override
	protected String doInBackground(String... params) {

		String responseString = "";

		File file = new File(params[0]);
		String url = "http://res.cloudinary.com/doi38h3hr/image/upload/v1413102074/afuzj1td4virweuqwsmw.jpg";
		Cloudinary cloudinary = new Cloudinary(Cloudinary.asMap("cloud_name",
				"doi38h3hr", "api_key", "876521916516342", "api_secret",
				"00L0PfCC13iT_BGzmVNrsesuT0I"));
		try {
			JSONObject uploadResult = cloudinary.uploader().upload(new FileInputStream(file), null);
			url = (String) uploadResult.getString("url");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("URL is " + url);

		try {
			com.mashape.unirest.http.HttpResponse<JsonNode> response = Unirest
					.post("https://lambda-face-recognition.p.mashape.com/recognize")
					.header("X-Mashape-Key",
							"gMoueY07U1mshpinwogEUEk0B13Qp1TUNLejsnkH2IwTJ9iEWF")
					.field("album", "TEST_NEW")
					.field("albumkey",
							"7bcc2ac04255c7d4859547b05120c2535e84f624b72f9dd42959a0932a91e557")
					.field("urls", url).asJson();
			responseString = response.getBody().toString();
			System.out.println("response is " + response.getBody().toString());
		} catch (UnirestException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
