package com.example.coinscanner;

import java.io.File;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import android.R.color;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

// REFS
// shttp://stackoverflow.com/questions/9445102/detecting-hough-circles-android

public class processingTools {

	public static Mat findCircles(Bitmap image) {

		// Kernel 3x3
		// Mat kernel = Mat.ones(3, 3, CvType.CV_32F);

		Mat kernel = new Mat(3, 3, CvType.CV_8U) {
			{
				put(1, 1, 1);
				put(1, 1, 1);
				put(1, 1, 1);
			}
		};

		// List of Circles
		Mat circles = new Mat();

		// Anchor point - Kernel center
		Point anchor = new Point(-1, -1);

		// Original picture
		Mat img = new Mat();
		Utils.bitmapToMat(image, img);

		// Image Colored to grayscale
		Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2GRAY);

		// Temp matrix
		Mat tmp = new Mat(img.width(), img.height(), img.type());

		// Denoising
		Photo.fastNlMeansDenoising(img, tmp, 21, 7, 7);

		Imgproc.morphologyEx(img, tmp, Imgproc.MORPH_RECT, kernel, anchor, 2);
		Imgproc.morphologyEx(tmp, tmp, Imgproc.MORPH_OPEN, kernel, anchor, 3);		

		// Circles detection using Hough
		Imgproc.HoughCircles(tmp, circles, Imgproc.CV_HOUGH_GRADIENT, 1.5, 50, 100, 90, 20, 120);
		
		// Image gray to color
		Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_GRAY2BGR);
				
		if (circles.cols() > 0)
			for (int x = 0; x < circles.cols(); x++) {
				double vCircle[] = circles.get(0, x);
				System.out.println(x + ": " + vCircle[0] + " | " + vCircle[1] + " | " + vCircle[2]);
				
				Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));  
	            float radius = (int)Math.round(vCircle[2]);  
	            
	            // draw the found circle  
	            Core.circle(tmp, pt, (int)radius, new Scalar(0, 255, 0), 2);  
			}
		
		// Write image on external storage
		File path = new File(Environment.getExternalStorageDirectory() + "/Images/");
		path.mkdirs();
		File file = new File(path, "image.png");
		   
		Boolean bool = Highgui.imwrite(file.toString(), tmp);
		if (bool)
		    Log.i("SAVE", "SUCCESS writing image to external storage");
		else
		    Log.i("SAVE", "Fail writing image to external storage");
		
		
		return circles;
	}

	public static Mat findCircles(Mat image) {

		// Kernel 3x3
		Mat kernel = Mat.ones(3, 3, CvType.CV_8U);

		// Circles
		Mat circles = new Mat();

		// Anchor point - Kernel center
		Point anchor = new Point(-1, -1);

		// Image Color to grayscale
		Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2GRAY);

		// Matrice temporaire
		Mat tmp = new Mat(image.width(), image.height(), image.type());

		// Denoising
		Photo.fastNlMeansDenoising(image, tmp, 21, 7, 7);

		Imgproc.morphologyEx(image, tmp, Imgproc.MORPH_RECT, kernel, anchor, 4);
		Imgproc.morphologyEx(tmp, tmp, Imgproc.MORPH_OPEN, kernel, anchor, 5);

		// Circles detection using Hough
		Imgproc.HoughCircles(tmp, circles, Imgproc.CV_HOUGH_GRADIENT, 1.5, 50, 100, 90, 20, 120);

		if (circles.cols() > 0)
			for (int x = 0; x < circles.cols(); x++) {
				double vCircle[] = circles.get(0, x);
				System.out.println(x + ": " + vCircle[0] + " | " + vCircle[1]);
			}
		return circles;
	}
}