package com.android.facerecognition;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.facerecognition.utils.BaseGlassActivity;
import com.facerecognition.utils.ImageManager;
import com.facerecognition.utils.Session;

public class TakePictureActivity extends BaseGlassActivity implements
		SurfaceHolder.Callback {

	private static final String TAG = "GlassScan";
	private static final String IMAGE_PREFIX = "GlassScan";

	private Camera camera;
	private boolean mHasSurface;
	private ImageManager mImageManager;
	private Activity activity;
	private boolean isImageNotCaptured = true;

	public static Intent newIntent(Context context) {
		Intent intent = new Intent(context, TakePictureActivity.class);
		return intent;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_take_picture);

		activity = this;
		mImageManager = new ImageManager(this);
		mHasSurface = false;

		// uncomment to debug the application.
		// android.os.Debug.waitForDebugger();
	}

	@Override
	public void onResume() {
		super.onResume();
		startCamera();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mImageManager = null;
		camera = null;
	}

	@Override
	protected void onPause() {
		if (!mHasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		camera.release();
		super.onPause();
		Log.d(TAG, "onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop");
	}

	private void startCamera() {
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (mHasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);
		} else {
			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			surfaceHolder.addCallback(this);
		}

	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}

		try {
			camera = Camera.open();
			camera.setPreviewDisplay(surfaceHolder);
		} catch (IOException e) {
			Toast.makeText(TakePictureActivity.this,
					"Unable to start camera, please restart and try again",
					Toast.LENGTH_LONG).show();
			Log.d(TAG, e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			Toast.makeText(TakePictureActivity.this,
					"Unable to start camera, please restart and try again",
					Toast.LENGTH_LONG).show();
			Log.d(TAG, e.getMessage());
			e.printStackTrace();
		}
		camera.startPreview();
	}

	@Override
	protected boolean onTap() {
		if (isImageNotCaptured) {
			isImageNotCaptured = false;
			camera.takePicture(null, null, mPicture);
			// takePicture = true;
		}
		return super.onTap();
	}

	PictureCallback mPicture = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] captureData, Camera camera) {

			Intent intent = new Intent(getApplicationContext(),
					ShowIdentityActivity.class);
			// intent.putExtra("image", captureData);
			//startActivity(intent);

			Session.getInstant().setImageBytes(captureData);
			isImageNotCaptured = true;

			Bitmap captureImage = null;
			if (captureData != null) {
				captureImage = getBitmapFromByteArray(captureData);
			}

			Uri imageUri = null;

			String imageName = IMAGE_PREFIX + ".png";
			try {
				imageUri = mImageManager.saveImage(imageName, captureImage);
				Log.v(TAG, "Saving image as: " + imageName);
				intent.putExtra("imagePath", imageUri.toString());
				startActivity(intent);

			} catch (IOException e) {
				Log.e(TAG, "Failed to save image!", e);
			} 

		}
	};

	protected Bitmap getBitmapFromByteArray(byte[] captureData) {
		Bitmap captureImage = null;
		captureImage = BitmapFactory.decodeByteArray(captureData, 0,
				captureData.length, null);
		// Mutable copy:
		captureImage = captureImage.copy(Bitmap.Config.ARGB_8888, true);
		return captureImage;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG,
					"*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!mHasSurface) {
			mHasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mHasSurface = false;
	}

}
