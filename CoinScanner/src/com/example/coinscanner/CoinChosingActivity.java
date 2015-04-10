package com.example.coinscanner;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class CoinChosingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coin_chosing);

		RelativeLayout canvas = (RelativeLayout) findViewById(R.id.canvas_image);
		ImageView imageView = new ImageView(getApplicationContext());
		Bitmap mainImage = BitmapFactory.decodeFile(getIntent().getStringExtra("dirname") + "/"
				+ getIntent().getStringExtra("filename"));
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		Bitmap rotated = Bitmap
				.createBitmap(mainImage, 0, 0, mainImage.getWidth(), mainImage.getHeight(), matrix, true);
		imageView.setImageBitmap(rotated);
		canvas.addView(imageView);

		Toast.makeText(getApplicationContext(), "Chose a coin and give the value of it !", Toast.LENGTH_LONG).show();
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
