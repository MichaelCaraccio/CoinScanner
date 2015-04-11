package com.example.coinscanner;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

/** A basic Camera preview class */
@SuppressLint("ClickableViewAccessibility")
@SuppressWarnings({ "deprecation" })
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = "CamPrev";
	private static final int MAX_HORIZONTAL_SCREEN_RESOLUTION = 2000;

	private SurfaceHolder mHolder;
	private Camera mCamera;
	private boolean bProcessing = false;
	private Bitmap bitmap = null;
	private int[] pixels = null;
	private byte[] FrameData = null;
	private ImageView MyCameraPreview = null;
	private int PreviewSizeWidth;
	private int PreviewSizeHeight;

	Handler mHandler = new Handler(Looper.getMainLooper());

	private PreviewCallback prevCB = new PreviewCallback() {

		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {

			System.out.println("onPreviewFrame");

			// We only accept the NV21(YUV420) format.
			if (!bProcessing) {
				FrameData = data;
				mHandler.post(DoImageProcessing);
			}
			/*
			 * Size previewSize = camera.getParameters().getPreviewSize();
			 * YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21,
			 * previewSize.width, previewSize.height, null);
			 * ByteArrayOutputStream baos = new ByteArrayOutputStream();
			 * yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width,
			 * previewSize.height), 80, baos); byte[] jdata =
			 * baos.toByteArray(); Bitmap sourceFrame =
			 * BitmapFactory.decodeByteArray(jdata, 0, jdata.length);
			 * 
			 * System.out.println("csasasa"+sourceFrame.getWidth());
			 */

			// Convert to JPG
			/*
			 * Size previewSize = camera.getParameters().getPreviewSize();
			 * YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21,
			 * previewSize.width, previewSize.height, null);
			 * ByteArrayOutputStream baos = new ByteArrayOutputStream();
			 * yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width,
			 * previewSize.height), 80, baos); byte[] jdata =
			 * baos.toByteArray();
			 * 
			 * Bitmap sourceFrame = BitmapFactory.decodeByteArray(jdata, 0,
			 * jdata.length);
			 * 
			 * if (sourceFrame == null) {
			 * System.out.println("CA A CRASH BMP == NULL PUTAIN"); } else {
			 * System.out.println("onPreviewFrame1");
			 * 
			 * // Original picture converted to mat Mat matSourceFrame = new
			 * Mat(); Utils.bitmapToMat(sourceFrame, matSourceFrame);
			 * 
			 * // List of matrix circles Mat circles = new Mat();
			 * 
			 * // Appel de la methode circles =
			 * processingTools.findCircles(matSourceFrame);
			 * 
			 * // Dessiner les cercles sur l'ecrans if (circles.cols() > 0) for
			 * (int x = 0; x < circles.cols(); x++) { double vCircle[] =
			 * circles.get(0, x); System.out.println(x + ": " + vCircle[0] +
			 * " | " + vCircle[1] + " | " + vCircle[2]);
			 * 
			 * Point pt = new Point(Math.round(vCircle[0]),
			 * Math.round(vCircle[1])); float radius = (int)
			 * Math.round(vCircle[2]);
			 * 
			 * // draw the found circle Core.circle(matSourceFrame, pt, (int)
			 * radius, new Scalar(0, 255, 0), 2); }
			 * 
			 * // Write image on external storage File path = new
			 * File(Environment.getExternalStorageDirectory() + "/Images/");
			 * path.mkdirs(); File file = new File(path,
			 * "imagewithcircles.png");
			 * 
			 * Boolean bool = Highgui.imwrite(file.toString(), matSourceFrame);
			 * if (bool) Log.i("SAVE IMAGE",
			 * "SUCCESS writing image to external storage"); else
			 * Log.i("SAVE IMAGE", "Fail writing image to external storage"); }
			 */
		}
	};

	public CameraPreview(Context context, Camera camera, ImageView CameraPreview) {
		super(context);
		mCamera = camera;
		MyCameraPreview = CameraPreview;
		Size previewSize = camera.getParameters().getPreviewSize();
		PreviewSizeWidth = previewSize.width;
		PreviewSizeHeight = previewSize.height;

		bitmap = Bitmap.createBitmap(PreviewSizeWidth, PreviewSizeHeight, Bitmap.Config.ARGB_8888);
		pixels = new int[PreviewSizeWidth * PreviewSizeHeight];

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// Image de test
		// Bitmap image = BitmapFactory.decodeResource(this.getResources(),
		// R.drawable.c7);

		// Listes des cercles obtenus
		// Mat circles = new Mat();

		// Appel de la methode
		// circles = processingTools.findCircles(image);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Camera.Parameters params = mCamera.getParameters();
		List<Size> camResolutions = params.getSupportedPictureSizes();
		Collections.sort(camResolutions, new Comparator<Size>() {
			@Override
			public int compare(Size arg0, Size arg1) {
				if (arg1.width != arg0.width) {
					return arg0.width - arg1.width;
				}
				return arg0.height - arg1.height;
			}
		});
		Size tmp = camResolutions.get(0);
		for (Size s : camResolutions) {
			if (s.width < MAX_HORIZONTAL_SCREEN_RESOLUTION && s.width > tmp.width)
				tmp = s;
		}

		params.setPictureSize(tmp.width, tmp.height);
		mCamera.setParameters(params);

		// The Surface has been created, now tell the camera where to draw the
		// preview.
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.setDisplayOrientation(90);
			mCamera.startPreview();
		} catch (IOException e) {
			Log.d(TAG, "Error setting camera preview: " + e.getMessage());
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mCamera.autoFocus(new AutoFocusCallback() {
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				// nothing
			}
		});
		return super.onTouchEvent(event);
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// empty. Take care of releasing the Camera preview in your activity.
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		// set preview size and make any resize, rotate or
		// reformatting changes here
		mCamera.setPreviewCallback(prevCB); // set preview callback

		// start preview with new settings
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();

		} catch (Exception e) {
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
	}

	private Runnable DoImageProcessing = new Runnable() {
		public void run() {
			//Log.i("Processing", "DoImageProcessing():");
			bProcessing = true;

			Mat mat = new Mat(PreviewSizeHeight, PreviewSizeWidth, CvType.CV_8UC1);
			mat.put(0, 0, FrameData);

			// List of matrix circles
			Mat circles = new Mat();

			// Appel de la methode
			circles = processingTools.findCircles(mat);

			Imgproc.cvtColor(mat, mat, Imgproc.COLOR_GRAY2BGR);

			// Dessiner les cercles sur l'ecrans
			if (circles.cols() > 0) {
				for (int x = 0; x < circles.cols(); x++) {
					double vCircle[] = circles.get(0, x);
					System.out.println(x + ": " + vCircle[0] + " | " + vCircle[1] + " | " + vCircle[2]);

					Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
					float radius = (int) Math.round(vCircle[2]);

					// draw the found circle
					Core.circle(mat, pt, (int) radius, new Scalar(0, 255, 0), 2);
				}
			} else {
				Log.i("Processing", "Aucun cercles trouves");
			}

			Utils.matToBitmap(mat, bitmap);
			MyCameraPreview.setImageBitmap(bitmap);
			MyCameraPreview.setRotation(90);

			bProcessing = false;
		}
	};
}