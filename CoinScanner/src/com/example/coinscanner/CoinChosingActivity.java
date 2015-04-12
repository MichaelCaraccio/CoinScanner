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

	private ArrayList<MyCircle> circlesList;
	private ArrayList<MyCircle> scaledCircleList;
	Matrix scaleAndRotateMatrix;
	Canvas drawingCanvas;
	ImageView imageView;
	AlertDialog.Builder builder;
	int circleIndex;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coin_chosing);

		scaledCircleList = new ArrayList<MyCircle>();
		imageView = new ImageView(getApplicationContext());
		circleIndex = -1;

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.canvas_image);
		Bitmap mainImage = BitmapFactory.decodeFile(getIntent().getStringExtra("dirname") + "/"
				+ getIntent().getStringExtra("filename"));
		Bitmap workingCopy = mainImage.copy(Bitmap.Config.ARGB_8888, true);

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		float widthRatio = (float) width / workingCopy.getHeight();
		float heightRatio = (float) height / workingCopy.getWidth();

		Bitmap screenBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		circlesList = (ArrayList<MyCircle>) getIntent().getSerializableExtra("circles");

		drawingCanvas = new Canvas(screenBitmap);
		scaleAndRotateMatrix = new Matrix();
		scaleAndRotateMatrix.postScale(widthRatio, heightRatio);
		scaleAndRotateMatrix.postRotate(90);
		scaleAndRotateMatrix.postTranslate(screenBitmap.getWidth(), 0);

		drawingCanvas.drawBitmap(workingCopy, scaleAndRotateMatrix, null);
		drawCircles();
		imageView.setImageDrawable(new BitmapDrawable(getResources(), screenBitmap));
		layout.addView(imageView);

		if (circlesList.size() > 0) {
			Toast.makeText(getApplicationContext(), "Chose a coin and give the value of it !", Toast.LENGTH_LONG)
					.show();
			createScaledCirclePoints((widthRatio + heightRatio) / 2);
			imageView.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int i = 0;
					for (MyCircle circle : scaledCircleList) {
						if (Math.sqrt(Math.pow(event.getX() - circle.getCenterX(), 2)
								+ Math.pow(event.getY() - circle.getCenterY(), 2)) <= circle.getRadius()) {
							builder.show();
							circleIndex = i;
						}
						i++;
					}
					return false;
				}
			});
		} else {
			Toast.makeText(getApplicationContext(), "No coin found, try again !", Toast.LENGTH_LONG).show();
		}
		createPickBox();
	}

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

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (circleIndex >= 0) {
					Coin selected = coins[which];
					MyCircle selectedCircle = circlesList.get(circleIndex);
					double monneySum = calculateMonneySum(selected, selectedCircle);
					Toast.makeText(getApplicationContext(), "You own " + String.format("%.2f", monneySum) + " CHF", Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	private double calculateMonneySum(Coin selected, MyCircle selectedCircle) {
		HashMap<Double, Coin> ratios = CHFStore.getRatios(selected);
		Set<Double> keyset = ratios.keySet();
		Double[] keys = keyset.toArray(new Double[keyset.size()]);
		double diff = 10000, tmpDiff = 0;
		double ratio = 0, coinKey = -1;
		double monney = 0;
		for (MyCircle circle : circlesList) {
			for (Double key : keys) {
				ratio = (double) selectedCircle.getRadius() / (double) circle.getRadius();
				tmpDiff = Math.abs(ratio - key.doubleValue());
				if (tmpDiff < diff) {
					diff = tmpDiff;
					coinKey = key;
				}
			}
			Log.d("LOL", ratios.get(Double.valueOf(coinKey)).getValue()+"");
			monney += ratios.get(Double.valueOf(coinKey)).getValue();
			coinKey = -1;
			diff = 10000;
			tmpDiff = 0;
		}

		return monney;
	}

	private void drawCircles() {
		Paint paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		drawingCanvas.setMatrix(scaleAndRotateMatrix);
		for (MyCircle circle : circlesList) {
			drawingCanvas.drawCircle(circle.getCenterX(), circle.getCenterY(), circle.getRadius(), paint);
		}
	}

	private void createScaledCirclePoints(float scaling) {
		for (MyCircle circle : circlesList) {
			float point[] = { (float) circle.getCenterX(), (float) circle.getCenterY() };
			scaleAndRotateMatrix.mapPoints(point);
			scaledCircleList.add(new MyCircle((int) point[0], (int) point[1], (int) (circle.getRadius() * scaling)));
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
