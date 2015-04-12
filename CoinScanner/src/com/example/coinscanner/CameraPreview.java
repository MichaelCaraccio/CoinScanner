package com.example.coinscanner;

import java.io.IOException;
import java.util.ArrayList;
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
import android.graphics.drawable.shapes.OvalShape;
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
public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {

	private static final String TAG = "CamPrev";

	private SurfaceHolder mHolder;
	private Camera mCamera;
	private ImageView MyCameraPreview = null;
	private int PreviewSizeWidth;
	private int PreviewSizeHeight;




	public CameraPreview(Context context, Camera camera, ImageView CameraPreview) {
		super(context);
		mCamera = camera;
		MyCameraPreview = CameraPreview;
		Size previewSize = camera.getParameters().getPictureSize();
		PreviewSizeWidth = previewSize.width;
		PreviewSizeHeight = previewSize.height;


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

		// start preview with new settings
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();

		} catch (Exception e) {
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
	}

}