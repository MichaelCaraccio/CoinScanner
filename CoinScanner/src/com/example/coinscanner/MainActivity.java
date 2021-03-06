package com.example.coinscanner;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Check if opencv is opencv manager is installed and loaded
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);

		// Scan button
		Button button_scan = (Button) findViewById(R.id.btnScan);
		button_scan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent cameraIntent = new Intent(MainActivity.this, CameraActivity.class);
				startActivity(cameraIntent);
			}
		});

		// Help button
		Button button_help = (Button) findViewById(R.id.btnHelp);
		button_help.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent helpIntent = new Intent(MainActivity.this, HelpActivity.class);
				startActivity(helpIntent);
			}
		});

		// About button
		Button button_about = (Button) findViewById(R.id.btnAbout);
		button_about.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
				startActivity(aboutIntent);
			}
		});
	}

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i("OPENCV", "OpenCV loaded successfully");
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
