package com.example.coinscanner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class CameraActivity extends Activity {

	protected static final String TAG = null;
	private Camera mCamera;
	private CameraPreview mPreview;
	private ImageView MyCameraPreview = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		MyCameraPreview = new ImageView(this);

		// Create an instance of Camera
		mCamera = getCameraInstance();
		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this, mCamera, MyCameraPreview);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);

		RelativeLayout btnLayout = (RelativeLayout) findViewById(R.id.btn_layout);
		btnLayout.bringToFront();

		Button btnCapture = (Button) findViewById(R.id.btn_capture);
		btnCapture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCamera != null) {
					mCamera.takePicture(null, null, mPicture);
				}

			}
		});

		preview.addView(MyCameraPreview, new LayoutParams(640, 480));
	}

	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Bitmap bmp;
			bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

			String path = Environment.getExternalStorageDirectory().toString();
			OutputStream fOutputStream = null;
			String fileName = "coinsframe.jpg";
			String dirName = path + "/coinscanner/";
			File file = new File(dirName, fileName);
			Log.d("LOL", file.toString());
			if (!file.exists()) {
				file.getParentFile().mkdirs();
			}

			try {
				fOutputStream = new FileOutputStream(file);

				bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);

				fOutputStream.flush();
				fOutputStream.close();

				MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(),
						file.getName());
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(CameraActivity.this, "Error occured", Toast.LENGTH_SHORT).show();
				return;
			}

			Intent coinChoosingActivity = new Intent(CameraActivity.this, CoinChosingActivity.class);
			coinChoosingActivity.putExtra("filename", fileName); // put the
																	// bitmap
																	// image in
																	// datas
			coinChoosingActivity.putExtra("dirname", dirName);
			startActivity(coinChoosingActivity);
		}
	};

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			Toast.makeText(null, "Camera is not available (in use or does not exist)", Toast.LENGTH_SHORT).show();
		}
		return c; // returns null if camera is unavailable
	}

	@Override
	protected void onPause() {
		mCamera.stopPreview();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mCamera.startPreview();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mCamera.stopPreview();
		mCamera.release();
		super.onDestroy();
	}
}