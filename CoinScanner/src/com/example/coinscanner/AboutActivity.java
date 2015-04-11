package com.example.coinscanner;

import android.app.Activity;
import android.os.Bundle;

public class AboutActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		/*
		 * Button button = (Button) findViewById(R.id.btnScan);
		 * button.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Intent cameraIntent = new
		 * Intent(MainActivity.this, CameraActivity.class);
		 * startActivity(cameraIntent); } });
		 */
	}
}
