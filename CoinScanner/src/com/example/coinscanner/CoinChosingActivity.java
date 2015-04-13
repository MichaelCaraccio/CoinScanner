package com.example.coinscanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class CoinChosingActivity extends Activity {

	// attributes
	private ArrayList<MyCircle> circlesList;
	private ArrayList<MyCircle> scaledCircleList;
	Matrix scaleAndRotateMatrix;
	Canvas drawingCanvas;
	ImageView imageView;
	AlertDialog.Builder builder;
	int circleIndex;
	private Paint paint;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coin_chosing);

		paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		paint.setTextSize(20);

		scaledCircleList = new ArrayList<MyCircle>();
		imageView = new ImageView(getApplicationContext());
		circleIndex = -1;

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.canvas_image);
		// open the image saved in CameraActivity using filepath in extras
		Bitmap mainImage = BitmapFactory.decodeFile(getIntent().getStringExtra(
				"dirname")
				+ "/" + getIntent().getStringExtra("filename"));
		// create a muable copy of image
		Bitmap workingCopy = mainImage.copy(Bitmap.Config.ARGB_8888, true);

		// calculate ratios for scaling the image in fullscreen (even if
		// proportions are lost)
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		float widthRatio = (float) width / workingCopy.getHeight();
		float heightRatio = (float) height / workingCopy.getWidth();

		// create an empty bitmap with the size of screen to draw on it
		Bitmap screenBitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);

		// get circles from extras
		circlesList = (ArrayList<MyCircle>) getIntent().getSerializableExtra(
				"circles");

		// create new Canvas with the empty bitmap into it
		drawingCanvas = new Canvas(screenBitmap);
		// create the transformations matrix
		scaleAndRotateMatrix = new Matrix();
		scaleAndRotateMatrix.postScale(widthRatio, heightRatio);
		scaleAndRotateMatrix.postRotate(90);
		scaleAndRotateMatrix.postTranslate(screenBitmap.getWidth(), 0);

		// draw the image (scaled and rotated by matrix)
		drawingCanvas.drawBitmap(workingCopy, scaleAndRotateMatrix, null);

		drawingCanvas.setMatrix(scaleAndRotateMatrix);

		// draw cirlces (detected coins)
		drawCircles();

		// set the bitmap in the view
		imageView.setImageDrawable(new BitmapDrawable(getResources(),
				screenBitmap));
		// add the view in layout
		layout.addView(imageView);

		// if we have atleast one circle (one coin)
		if (circlesList.size() > 0) {
			// inform the user that he can pick one and give his value
			Toast.makeText(getApplicationContext(),
					"Choose a coin and give the value of it !",
					Toast.LENGTH_LONG).show();
			createScaledCirclePoints((widthRatio + heightRatio) / 2);
			imageView.setOnTouchListener(new OnTouchListener() {

				// add a listener on the touchscreen event
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// check if event pos are in a circle
					int i = 0;
					for (MyCircle circle : scaledCircleList) {
						if (Math.sqrt(Math.pow(
								event.getX() - circle.getCenterX(), 2)
								+ Math.pow(event.getY() - circle.getCenterY(),
										2)) <= circle.getRadius()) {
							builder.show(); // if a circle was picked show a box
											// to chose a value
							circleIndex = i;
						}
						i++;
					}
					return false;
				}
			});
		} else {
			Toast.makeText(getApplicationContext(),
					"No coin found, try again !", Toast.LENGTH_LONG).show();
		}
		createPickBox();
	}

	/**
	 * Create the box that allow user to select a coin's value
	 */
	private void createPickBox() {
		final Coin[] coins = CHFStore.getSortedCoinTab();
		String coinsStr[] = new String[coins.length];
		int i = 0;
		for (Coin c : coins) {
			coinsStr[i++] = c.getDisplay();
		}
		builder = new AlertDialog.Builder(this);
		builder.setTitle("Value of coin");
		builder.setItems(coinsStr, new OnClickListener() {

			// as soon as a value is selected for a coin
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (circleIndex >= 0) {
					Coin selected = coins[which];
					MyCircle selectedCircle = circlesList.get(circleIndex);
					// launch calculation process and display it in a toast
					 double monneySum = calculateMonneySum(selected, selectedCircle);
					Toast.makeText(
							getApplicationContext(),
							"You own " + String.format("%.2f", monneySum)
									+ " CHF", Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	/**
	 * Calculate the monney sum based on the selected circle and his value The
	 * calculation use ratios between circles diameters according to real ratios
	 * between swiss coins diameters to determine the value of each circles
	 * found
	 * 
	 * @param selected
	 * @param selectedCircle
	 * @return
	 */
	private double calculateMonneySum(Coin selected, MyCircle selectedCircle) {
		// get the selected coin related ratios (according to the real diameter
		// of swiss coin)
		// the ratios are the keys of the map
		HashMap<Double, Coin> ratios = CHFStore.getRatios(selected);
		Set<Double> keyset = ratios.keySet();
		Double[] keys = keyset.toArray(new Double[keyset.size()]);
		// initialize vars
		double diff = 10000, tmpDiff = 0;
		double ratio = 0, coinKey = -1;
		double monney = 0;

		// for each detected circle
		for (MyCircle circle : circlesList) {
			// for each real ratios
			for (Double key : keys) {
				// calculate the ratio between the selected circle on image and
				// others circles
				ratio = (double) selectedCircle.getRadius()
						/ (double) circle.getRadius();
				// calculate the difference between real ratio and detected
				// circles ratio
				tmpDiff = Math.abs(ratio - key.doubleValue());

				// save the ratio with the smallest difference
				if (tmpDiff < diff) {
					diff = tmpDiff;
					coinKey = key;
				}
			}
			// the coin value is the coin with the ratio that is the closest to
			// the real ratio
			monney += ratios.get(Double.valueOf(coinKey)).getValue(); // add the
																		// coin
																		// value
			// reset vars
			coinKey = -1;
			diff = 10000;
			tmpDiff = 0;
		}

		return monney;
	}

	private double[] getMinMaxRadius(ArrayList<MyCircle> circles) {
		double[] minMax = new double[2];
		double min = 1000000, max = -1000000;
		for (MyCircle c : circles) {
			if (c.getRadius() > max)
				max = c.getRadius();
			if (c.getRadius() < min)
				min = c.getRadius();
		}
		minMax[0] = min;
		minMax[1] = max;

		return minMax;
	}

	private double calculateMonneySumCalibration() {
		double[] minMaxCircles = getMinMaxRadius(circlesList);
		double[] minMaxCoins = CHFStore.getMinMaxRadius();
		CalibratorF calibrator = new CalibratorF(new IntervalF(
				minMaxCircles[0], minMaxCircles[1]), new IntervalF(
				minMaxCoins[0], minMaxCoins[1]));

		double tmpDiff = 0;
		double diff = 10000;
		double monneySum = 0;
		Coin chosenCoin = null;
		for (MyCircle circle : circlesList) {
			double calibratedRadius = calibrator.calibrate(circle.getRadius());
			for (Coin coin : CHFStore.getSortedCoinTab()) {
				System.out.println(calibratedRadius+"");
				tmpDiff = Math.abs(coin.getRadius() - calibratedRadius);
				if (tmpDiff < diff) {
					diff = tmpDiff;
					chosenCoin = coin;
				}
			}
			diff = 1000;
			tmpDiff = 0;
			if (chosenCoin != null) {
				monneySum += chosenCoin.getValue();
				chosenCoin = null;
			}
		}
		return monneySum;
	}

	private void drawCircles() {

		// draw circles
		for (MyCircle circle : circlesList) {
			drawingCanvas.drawCircle(circle.getCenterX(), circle.getCenterY(),
					circle.getRadius(), paint);
		}
	}

	/**
	 * create the list of scaled circle (with transform matrix) for the
	 * touchscreen selection event because the selection is done on scaled
	 * picture
	 * 
	 * @param scaling
	 */
	private void createScaledCirclePoints(float scaling) {
		for (MyCircle circle : circlesList) {
			float point[] = { (float) circle.getCenterX(),
					(float) circle.getCenterY() };
			scaleAndRotateMatrix.mapPoints(point);
			scaledCircleList.add(new MyCircle((int) point[0], (int) point[1],
					(int) (circle.getRadius() * scaling)));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.coin_chosing, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
