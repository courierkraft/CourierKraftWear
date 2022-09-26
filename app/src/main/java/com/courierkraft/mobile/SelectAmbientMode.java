package com.courierkraft.mobile;

import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;


public class SelectAmbientMode extends WearableActivity {
	private SharedPreferences sharedPrefs;
	private SharedPreferences.Editor editor;

	private Button btnAlwaysOn;
	private Button btnDim;
	private Button btnFull;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_ambient_mode);

		btnAlwaysOn = findViewById(R.id.button_always_on);
		btnDim = findViewById(R.id.button_dim);
		btnFull = findViewById(R.id.button_full);

		sharedPrefs = getApplicationContext().getSharedPreferences("user_settings", Context.MODE_PRIVATE);
		switch (sharedPrefs.getString("ambient_mode", "none")) {
			case "always_on":
				btnAlwaysOn.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.ambient_button_enabled));
				break;
			case "dim":
				btnDim.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.ambient_button_enabled));
				break;
			case "full":
				btnFull.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.ambient_button_enabled));
				break;
			default:
				editor = sharedPrefs.edit();
				editor.putString("ambient_mode", "none");
				editor.apply();
				break;

		}

		btnAlwaysOn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				editor = sharedPrefs.edit();
				editor.putString("ambient_mode", "always_on");
				editor.apply();
				btnAlwaysOn.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.ambient_button_enabled));
				btnDim.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.ambient_button_disabled));
				btnFull.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.ambient_button_disabled));
			}
		});
		btnDim.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				editor = sharedPrefs.edit();
				editor.putString("ambient_mode", "dim");
				editor.apply();
				btnDim.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.ambient_button_enabled));
				btnAlwaysOn.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.ambient_button_disabled));
				btnFull.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.ambient_button_disabled));
			}
		});
		btnFull.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				editor = sharedPrefs.edit();
				editor.putString("ambient_mode", "full");
				editor.apply();
				btnFull.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.ambient_button_enabled));
				btnDim.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.ambient_button_disabled));
				btnAlwaysOn.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.ambient_button_disabled));
			}
		});
	}
}