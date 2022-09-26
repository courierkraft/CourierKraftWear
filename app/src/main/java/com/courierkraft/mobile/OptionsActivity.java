package com.courierkraft.mobile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;


public class OptionsActivity extends AppCompatActivity {
	final String TAG = "Options";
	public Context context;
	private SharedPreferences sharedPrefs;
	private SharedPreferences.Editor editor;
	public SwitchCompat sensorInfoSwitch;
	View promptView;

	private final ActivityResultLauncher<String> bodySensorsPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
		if ( promptView != null && promptView.getVisibility() != View.GONE ) {
			promptView.setVisibility(View.GONE);
		}
		if (isGranted) {
			editor = sharedPrefs.edit();
			editor.putBoolean("sensor_info", true);
			editor.apply();
		} else {
			editor = sharedPrefs.edit();
			editor.putBoolean("sensor_info", false);
			editor.apply();
		}
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(() -> sensorInfoSwitch.setChecked(isGranted));
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final String CTAG = "onCreate";
		setContentView(R.layout.activity_options);
		context = getApplicationContext();

		sharedPrefs = getApplicationContext().getSharedPreferences("user_settings", Context.MODE_PRIVATE);

		sensorInfoSwitch = findViewById(R.id.switch_sensor_info);
		sensorInfoSwitch.setChecked(sharedPrefs.getBoolean("sensor_info", false));
		sensorInfoSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if ( isChecked ) {
				if (ContextCompat.checkSelfPermission( context, Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED) {
					editor = sharedPrefs.edit();
					editor.putBoolean("sensor_info", true);
					editor.apply();
				} else if (shouldShowRequestPermissionRationale(Manifest.permission.BODY_SENSORS)) {
					promptView = findViewById(R.id.body_sensor_prompt_view);
					Button cancel = findViewById(R.id.body_sensor_cancel_button);
					cancel.setOnClickListener(v -> promptView.setVisibility(View.GONE));
					Button confirm = findViewById(R.id.body_sensor_confirm_button);
					confirm.setOnClickListener(v -> bodySensorsPermissionLauncher.launch(Manifest.permission.BODY_SENSORS));
					promptView.setVisibility(View.VISIBLE);
					sensorInfoSwitch.setChecked(false);
				} else {
					bodySensorsPermissionLauncher.launch(Manifest.permission.BODY_SENSORS);
				}
			} else {
				editor = sharedPrefs.edit();
				editor.putBoolean("sensor_info", false);
				editor.apply();
			}
		});

		SwitchCompat addressImagesSwitch = findViewById(R.id.switch_address_images);
		addressImagesSwitch.setChecked(sharedPrefs.getBoolean("address_images", false));
		addressImagesSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
			editor = sharedPrefs.edit();
			editor.putBoolean("address_images", isChecked);
			editor.apply();
			CommonUtils.Echo(TAG, CTAG, "address_images:"+isChecked, 1);
		});

		SwitchCompat resetModeSwitch = findViewById(R.id.switch_reset_mode);
		resetModeSwitch.setChecked(sharedPrefs.getBoolean("reset_mode", false));
		resetModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
			editor = sharedPrefs.edit();
			editor.putBoolean("reset_mode", isChecked);
			editor.apply();
			CommonUtils.Echo(TAG, CTAG, "reset_mode:"+isChecked, 1);
		});

		Button selectDevice = findViewById(R.id.button_select_device);
		selectDevice.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SelectDeviceActivity.class)));

		Button selectAmbientMode = findViewById(R.id.button_select_ambient_mode);
		selectAmbientMode.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SelectAmbientMode.class)));

		Button exitApp = findViewById(R.id.button_exit_app);
		exitApp.setOnClickListener(view -> {
			MainActivity.instance.get().finish();
			this.finish();
		});

	}

	@Override
	public void onPause() {
		super.onPause();
	}

}