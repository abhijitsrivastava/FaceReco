package com.android.facerecognition;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facerecognition.utils.FetchIdentityTask;
import com.google.android.glass.app.Card;

public class ShowIdentityActivity extends Activity {

	// private byte[] capturedImage = null;
	private String imagePath = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Card card = new Card(this);
		card.setText("Finding person, Please wait");
		View cardView = card.getView();

		setContentView(cardView);
		// capturedImage = Session.getInstant().getImageBytes();

		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			imagePath = bundle.getString("imagePath");
		}

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (null != imagePath) {
			Log.d("imagePath : ", imagePath);
			new FetchIdentityTask(this).execute(imagePath);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public void onCompleteUpdateStatusRequest(String identity) {
		if (null != identity) {
			Card card = new Card(this);
			card.setText("This person is : " + identity);
			View cardView = card.getView();
			setContentView(cardView);
		} else {
			Card card = new Card(this);
			card.setText("This person is : Unknown");
			View cardView = card.getView();
			setContentView(cardView);
		}
	}
}
