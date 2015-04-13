package com.example.coinscanner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class CameraActivity extends Activity {

	protected static final String TAG = null;
	private Camera mCamera;
	private CameraPreview mPreview;
	private ImageView MyCameraPreview = null;

	private static final int MAX_HORIZONTAL_SCREEN_RESOLUTION = 2000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		MyCameraPreview = new ImageView(this);

		// Create an instance of Camera
		mCamera = getCameraInstance();
		configureCamera();

		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this, mCamera, MyCameraPreview);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);

		//get the capture button and bring it to front
		Button btnCapture = (Button) findViewById(R.id.btn_capture);
		btnCapture.bringToFront();

		//add a listener to catch click event
		btnCapture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//on click if camera is not null autofocus and take picture
				if (mCamera != null) {
					
					// Without autoFocus
					mCamera.takePicture(null, null, mPicture);
					
					/*mCamera.autoFocus(new AutoFocusCallback() {
						@Override
						public void onAutoFocus(boolean success, Camera camera) {
							camera.takePicture(null, null, mPicture);
						}
					});*/
				}

			}
		});
	}

	/***
	 * Configure the camera (size of picture taken and size of preview)
	 */
	private void configureCamera() {
		// first get and sort the available resolutions
		Camera.Parameters params = mCamera.getParameters();
		List<Size> pictureResolutions = params.getSupportedPictureSizes();
		List<Size> previewResolutions = params.getSupportedPreviewSizes();
		//sort with comparator
		Collections.sort(pictureResolutions, new Comparator<Size>() {
			@Override
			public int compare(Size arg0, Size arg1) {
				if (arg1.width != arg0.width) {
					return arg0.width - arg1.width;
				}
				return arg0.height - arg1.height;
			}
		});

		//sort with comparator
		Collections.sort(previewResolutions, new Comparator<Size>() {
			@Override
			public int compare(Size arg0, Size arg1) {
				if (arg1.width != arg0.width) {
					return arg0.width - arg1.width;
				}
				return arg0.height - arg1.height;
			}
		});

		//iterate on sorted available resolutions and get the best one (the biggest resolution with width < 2000px)
		Size tmpPictureSize = pictureResolutions.get(0);
		for (Size s : pictureResolutions) {
			if (s.width < MAX_HORIZONTAL_SCREEN_RESOLUTION && s.width > tmpPictureSize.width) {
				tmpPictureSize = s;
			}
		}
		params.setPictureSize(tmpPictureSize.width, tmpPictureSize.height);
		Size tmpPreviewSize = previewResolutions.get(0);
		for (Size s : previewResolutions) {
			if (s.width < MAX_HORIZONTAL_SCREEN_RESOLUTION && s.width > tmpPreviewSize.width) {
				tmpPreviewSize = s;
			}
		}
		params.setPreviewSize(tmpPreviewSize.width, tmpPreviewSize.height);
		mCamera.setParameters(params);
	}

	/**
	 * callback when a picture is taken
	 */
	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			//convert the byte data into bitmap (image will be save on storage)
			Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
			Bitmap myBitmap32 = bmp.copy(Bitmap.Config.ARGB_8888, true);
			//then convert into mat for opencv processing
			Mat image = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC3);
			Utils.bitmapToMat(myBitmap32, image);
			Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY); //color to grayscaled
			ArrayList<MyCircle> circlesList = processingTools.findCircles(image); //get circles

			//get the path where the image should be save
			String path = Environment.getExternalStorageDirectory().toString();
			OutputStream fOutputStream = null;
			String fileName = "coinsframe.jpg";
			String dirName = path + "/coinscanner/";
			File file = new File(dirName, fileName);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
			}

			try {
				//save the image
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

			//launch the next activity with the image path and the circle list
			Intent coinChoosingActivity = new Intent(CameraActivity.this, CoinChosingActivity.class);
			coinChoosingActivity.putExtra("filename", fileName);
			coinChoosingActivity.putExtra("dirname", dirName);
			coinChoosingActivity.putExtra("circles", circlesList);
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
		super.onPause();

		if (mCamera != null)
			mCamera.stopPreview();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mCamera == null)
			mCamera.startPreview();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
		}
	}
}