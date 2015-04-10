package com.example.coinscanner;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** A basic Camera preview class */
@SuppressWarnings({ "deprecation" })
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	
    private static final String TAG = "CamPrev";
	private static final int MAX_HORIZONTAL_SCREEN_RESOLUTION = 4000;

	private SurfaceHolder mHolder;
	private Camera mCamera;
	private PreviewCallback prevCB = new PreviewCallback() {

		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			Bitmap sourceFrame = BitmapFactory.decodeByteArray(data, 0, data.length);
			Mat matSourceFrame = new Mat(sourceFrame.getWidth(), sourceFrame.getHeight(), CvType.CV_8UC4);
			
			// Listes des cercles obtenus
		    Mat circles = new Mat();		
		    
		    // Appel de la methode
		    circles = processingTools.findCircles(matSourceFrame);
		}
	};

	public CameraPreview(Context context, Camera camera) {
		super(context);
		mCamera = camera;

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// Image de test
	    Bitmap image = BitmapFactory.decodeResource(this.getResources(), R.drawable.c7);
	
	    // Listes des cercles obtenus
	    Mat circles = new Mat();		
	    
	    // Appel de la methode
	    circles = processingTools.findCircles(image);
	}
	
    public void surfaceCreated(SurfaceHolder holder) {
    	Camera.Parameters params = mCamera.getParameters();
    	List<Size> camResolutions = params.getSupportedPictureSizes();
		Collections.sort(camResolutions, new Comparator<Size>()
		{
			@Override
			public int compare(Size arg0, Size arg1)
			{
				if (arg1.width != arg0.width)
				{
					return arg0.width - arg1.width;
				}
				return arg0.height - arg1.height;
			}
		});
		Size tmp = camResolutions.get(0);
		for (Size s : camResolutions)
		{
			if (s.width < MAX_HORIZONTAL_SCREEN_RESOLUTION && s.width > tmp.width)
				tmp = s;
		}

		params.setPictureSize(tmp.width, tmp.height);
		mCamera.setParameters(params);
    	
    	// The Surface has been created, now tell the camera where to draw the preview.
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
				//nothing
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

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        mCamera.setPreviewCallback(prevCB); //set preview callback

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}