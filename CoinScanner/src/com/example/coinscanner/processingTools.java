package com.example.coinscanner;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import android.graphics.Bitmap;

// REFS
// shttp://stackoverflow.com/questions/9445102/detecting-hough-circles-android

public class processingTools {

	public static Mat findCircles(Bitmap image) {

		// Kernel 3x3
		//Mat kernel = Mat.ones(3, 3, CvType.CV_32F);
		
		Mat kernel = new Mat(3,3, CvType.CV_32F){
            {
               put(1,1,1);
               put(1,1,1);
               put(1,1,1);
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

		Imgproc.morphologyEx(img, tmp, Imgproc.MORPH_RECT, kernel, anchor, 4);
		Imgproc.morphologyEx(tmp, tmp, Imgproc.MORPH_OPEN, kernel, anchor, 5);

		// Circles detection using Hough
		Imgproc.HoughCircles(tmp, circles, Imgproc.CV_HOUGH_GRADIENT, 1.5, 50, 100, 90, 20, 120);

		
		if (circles.cols() > 0)
			for (int x = 0; x < circles.cols(); x++) {
				double vCircle[] = circles.get(0, x);
				System.out.println(x + ": " + vCircle[0] + " | " + vCircle[1] + " | " + vCircle[2]);
			}
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
