package com.courierkraft.mobile;
//com.courierkraft.mobile
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.wear.ambient.AmbientModeSupport;

import com.google.android.gms.wearable.Node;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements AmbientModeSupport.AmbientCallbackProvider {
	final String TAG = "MainActivity";
	public Context context;
	private SharedPreferences sharedPrefs;
	public static WeakReference<MainActivity> instance;
	private ButtonController buttonController;
	public static WeakReference<ButtonController> buttonControllerRef;
	public static Node currentNode = null;
	public View mainLayout;
	private GestureDetector mDetector;

	private CommonUtils.SensorReceiver sensorReceiver;

	private ButtonController.ResetTimerTask resetTask;

	private ImageView dimmerImage;

	AmbientModeSupport.AmbientController controller;
	boolean isAmbient;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final String CTAG = "onCreate";
		context = getApplicationContext();
		instance = new WeakReference<>(this);
		controller = AmbientModeSupport.attach(this);
		isAmbient = controller.isAmbient();
		sharedPrefs = getApplicationContext().getSharedPreferences("user_settings", Context.MODE_PRIVATE);
		mDetector = new GestureDetector(context, new LogoGestureListener( context ));
		resetTask = new ButtonController.ResetTimerTask();

		String ambientPref = sharedPrefs.getString("ambient_mode", "none");
		if (ambientPref.equals("always_on")) {
			//keeps screen on
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		//
		mainLayout = findViewById(R.id.main_activity_layout);
		buttonController = new ButtonController(this);
		buttonControllerRef = new WeakReference<>(buttonController);
		buttonController.setButtonsToModeColors(0);
		dimmerImage = findViewById(R.id.imageView_dimmer1);
		View logoView = findViewById(R.id.center_button_layout);

		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int screenHeight = displayMetrics.heightPixels;
		int screenWidth = displayMetrics.widthPixels;

		final float[] logoOriginalX = {50};
		final float[] logoOriginalY = {50};
		logoView.getViewTreeObserver().addOnGlobalLayoutListener(
			new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					logoOriginalX[0] = logoView.getX();
					logoOriginalY[0] = logoView.getY();
					logoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}
		});

		View.OnTouchListener touchListener = new View.OnTouchListener() {
			Date btnPressedDate = new Date();
			int btnDelay = 500;
			float viewX, viewY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: {
						viewX = v.getX() - event.getRawX();
						viewY = v.getY() - event.getRawY();
						btnPressedDate = new Date();
						btnDelay = 500;
						break;
					} case MotionEvent.ACTION_UP: {
						int newX = (int) (event.getRawX() + viewX);
						int newY = (int) (event.getRawY() + viewY);
						if (newY < screenHeight / 2) {
							// btns 1 or 2
							if (newX < screenWidth / 2) {
								//btn 1
								CommonUtils.Echo(TAG, CTAG, "button #1", 1);
								if ( ButtonController.currentMode != 1 ) {
									buttonController.button1Ref.performClick();
								} else {
									buttonController.setButtonsToModeColors(0);
								}
							} else {
								//btn 2
								CommonUtils.Echo(TAG, CTAG, "button #2", 1);
								if ( ButtonController.currentMode != 2 ) {
									buttonController.button2Ref.performClick();
								} else {
									buttonController.setButtonsToModeColors(0);
								}
							}
						} else if (newY > screenHeight / 2) {
							// btns 3 or 4
							if (newX < screenWidth / 2) {
								//btn 3
								CommonUtils.Echo(TAG, CTAG, "button #3", 1);
								if ( ButtonController.currentMode != 3 ) {
									buttonController.button3Ref.performClick();
								} else {
									buttonController.setButtonsToModeColors(0);
								}
							} else {
								//btn 4
								CommonUtils.Echo(TAG, CTAG, "button #4", 1);
								if ( ButtonController.currentMode != 4 ) {
									buttonController.button4Ref.performClick();
								} else {
									buttonController.setButtonsToModeColors(0);
								}
							}
						}
						v.animate()
								.withStartAction(() -> v.setDrawingCacheEnabled(true))
								.x(logoOriginalX[0])
								.y(logoOriginalY[0])
								.setDuration(0)
								.withEndAction(() -> {
									v.setDrawingCacheEnabled(false);
									v.clearAnimation();
									mainLayout.invalidate();
								})
								.start();
						break;
					} case MotionEvent.ACTION_MOVE: {
						if (CommonUtils.diffDatesInMillis(btnPressedDate, new Date()) > btnDelay) {
							int newX = (int) (event.getRawX() + viewX);
							int newY = (int) (event.getRawY() + viewY);
							if (newY < screenHeight / 2) {
								// btns 1 or 2
								if (newX < screenWidth / 2) {
									//btn 1
									CommonUtils.Echo(TAG, CTAG, "button #1", 1);
									if ( ButtonController.currentMode == 1 ) {
										buttonController.setButtonsToModeColors(0);
									} else {
										buttonController.setButtonsToModeColors(1);
									}
								} else {
									//btn 2
									CommonUtils.Echo(TAG, CTAG, "button #2", 1);
									if ( ButtonController.currentMode == 2 ) {
										buttonController.setButtonsToModeColors(0);
									} else {
										buttonController.setButtonsToModeColors(2);
									}
								}
								btnDelay = 2000;
							} else if (newY > screenHeight / 2) {
								// btns 3 or 4
								if (newX < screenWidth / 2) {
									//btn 3
									CommonUtils.Echo(TAG, CTAG, "button #3", 1);
									if ( ButtonController.currentMode == 3 ) {
										buttonController.setButtonsToModeColors(0);
									} else {
										buttonController.setButtonsToModeColors(3);
									}
								} else {
									//btn 4
									CommonUtils.Echo(TAG, CTAG, "button #4", 1);
									if ( ButtonController.currentMode == 4 ) {
										buttonController.setButtonsToModeColors(0);
									} else {
										buttonController.setButtonsToModeColors(4);
									}
								}
								btnDelay = 2000;
							}
							btnPressedDate = new Date();
						}
						v.animate()
								.withStartAction(() -> v.setDrawingCacheEnabled(true))
								.x(event.getRawX() + viewX)
								.y(event.getRawY() + viewY)
								.setDuration(0)
								.withEndAction(() -> {
									v.setDrawingCacheEnabled(false);
									v.clearAnimation();
									mainLayout.invalidate();
								})
								.start();
						break;
					}
				}
				return mDetector.onTouchEvent(event);
			}
		};
		logoView.setClickable(true);
		logoView.setLongClickable(true);
		logoView.setOnTouchListener(touchListener);
	}

	@Override
	public void onResume() {
		super.onResume();
		final String CTAG = "onResume";
		CommonUtils.Echo(TAG, CTAG, "called", 1);
		// we disable the logo button after a double click because it takes time to load and don't we people clicking and starting many activities
		// re-enable it
		View logoView = findViewById(R.id.center_button_layout);
		logoView.setEnabled(true);
		if (sharedPrefs.getBoolean("reset_mode", false)) {
			if ( ! resetTask.isRunning ) {
				resetTask = new ButtonController.ResetTimerTask();
				resetTask.start();
			}
		} else {
			if ( resetTask.isRunning ) {
				resetTask.stop();
			}
		}
		//
		if ( ! DataLayerListenerService.isRunning ) {
			Intent ir = new Intent(getApplicationContext(), DataLayerListenerService.class);
			getApplicationContext().startService(ir);
		}
		//
		boolean needsPermsSDK31 = false;
		boolean needsPermsSDK30 = false;
		boolean needsBluetooth = false;
		if ( SDK_INT >= 31 ) {
			CommonUtils.Echo(TAG, CTAG, "request >= 31", 1);
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
					&& ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
				CommonUtils.Echo(TAG, CTAG, "Missing Bluetooth perms SDK >= 31", 3);
				needsPermsSDK31 = true;
			}
		} else {
			CommonUtils.Echo(TAG, CTAG, "request < 31", 1);
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
				CommonUtils.Echo(TAG, CTAG, "Missing Bluetooth perms SDK < 31", 3);
				needsPermsSDK30 = true;
			}
		}
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			CommonUtils.Echo(TAG, CTAG, "Device does not support Bluetooth", 3);
			needsBluetooth = true;
		} else if (!mBluetoothAdapter.isEnabled()) {
			CommonUtils.Echo(TAG, CTAG, "Bluetooth is not enabled", 3);
			needsBluetooth = true;
		} else {
			// Bluetooth is enabled
			CommonUtils.Echo(TAG, CTAG, "Bluetooth is enabled", 1);
			if ( currentNode == null ) {
				//startActivity(new Intent(context, SelectDeviceActivity.class));
			}
		}
		if ( SDK_INT >= 31 &&  needsPermsSDK31 ) {
			CommonUtils.Echo(TAG, CTAG, "request >= 31", 1);
			requestPermissions( new String[] {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_CONNECT }, 1 );
		} else if ( needsPermsSDK30 ) {
			requestPermissions( new String[] {Manifest.permission.BLUETOOTH }, 1 );
		} else if ( needsBluetooth ) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 1);
		}
		//
		if ( sharedPrefs.getBoolean("sensor_info", false) ) {
			if ( sensorReceiver == null ) {
				sensorReceiver = new CommonUtils.SensorReceiver(
						new String[] {Intent.ACTION_BATTERY_CHANGED, Intent.ACTION_BATTERY_LOW}
				);
				if ( sensorReceiver.registerThis(context) ) {
					CommonUtils.Echo(TAG, CTAG, "sensor receiver registered!", 1);
				}
			} else if ( ! sensorReceiver.isRegistered && sensorReceiver.filter != null ) {
				if ( sensorReceiver.registerThis(context) ) {
					CommonUtils.Echo(TAG, CTAG, "sensor receiver registered!", 1);
				}
			}
		} else {
			if ( sensorReceiver != null && sensorReceiver.isRegistered ) {
				if ( sensorReceiver.unregisterThis(context) ) {
					CommonUtils.Echo(TAG, "onDestroy", "sensor receiver unregistered!", 1);
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		CommonUtils.Echo(TAG, "onActivityResult", "resultCode: " + resultCode + " requestCode:" + requestCode, 1);
		if (resultCode == Activity.RESULT_OK && requestCode == 1) {
			//startActivity(new Intent(getApplicationContext(), SelectDeviceActivity.class));
		} else {
			CommonUtils.doToast(getApplicationContext(), "Remote functions disabled!", true);
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		CommonUtils.Echo(TAG, "onActivityResult", "requestCode: " + requestCode + " grantResults:" + Arrays.toString(grantResults), 1);
		if (requestCode == 1) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				if (SDK_INT >= 31) {
					int check1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH);
					int check2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT);
					if (check1 == PackageManager.PERMISSION_GRANTED && check2 == PackageManager.PERMISSION_GRANTED) {
						Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(enableBtIntent, 1);
					}
				} else {
					int check = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH);
					if (check == PackageManager.PERMISSION_GRANTED) {
						Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(enableBtIntent, 1);
					}
				}
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy: activity destroyed");
		if ( DataLayerListenerService.isRunning ) {
			Intent ir = new Intent(getApplicationContext(), DataLayerListenerService.class);
			getApplicationContext().stopService(ir);
		}
		if ( sensorReceiver != null && sensorReceiver.isRegistered ) {
			if ( sensorReceiver.unregisterThis(context) ) {
				CommonUtils.Echo(TAG, "onDestroy", "sensor receiver unregistered!", 1);
			}
		}
	}


	@Override
	public AmbientModeSupport.AmbientCallback getAmbientCallback() {
		return new MyAmbientCallback();
	}

	private static class MyAmbientCallback extends AmbientModeSupport.AmbientCallback {
		@Override
		public void onEnterAmbient(Bundle ambientDetails) {
			// Handle entering ambient mode
			CommonUtils.Echo("MainActivity", "onEnterAmbient", ambientDetails.toString(), 1);
			String ambientPref = MainActivity.instance.get().sharedPrefs.getString("ambient_mode", "none");
			switch ( ambientPref ) {
				case "full":
					// Questionable to stop the data service
					if ( DataLayerListenerService.isRunning ) {
						Intent ir = new Intent(MainActivity.instance.get(), DataLayerListenerService.class);
						MainActivity.instance.get().stopService(ir);
					}
					MainActivity.instance.get().dimmerImage.setAlpha(1f);
					MainActivity.instance.get().dimmerImage.setVisibility(View.VISIBLE);
					MainActivity.instance.get().buttonController.Ambient(true);
					break;
				case "always_on":
					MainActivity.instance.get().dimmerImage.setAlpha(0f);
					MainActivity.instance.get().dimmerImage.setVisibility(View.GONE);
					MainActivity.instance.get().buttonController.Ambient(false);
					break;
				default: //dim or none
					// Questionable to stop the data service
					MainActivity.instance.get().dimmerImage.setAlpha(0.5f);
					MainActivity.instance.get().dimmerImage.setVisibility(View.VISIBLE);
					MainActivity.instance.get().buttonController.Ambient(true);
					break;
			}
			CommonUtils.ambience.setAmbientState(true);
		}

		@Override
		public void onExitAmbient() {
			// Handle exiting ambient mode
			CommonUtils.Echo("MainActivity", "onExitAmbient", "---", 1);
			if ( ! DataLayerListenerService.isRunning ) {
				Intent ir = new Intent(MainActivity.instance.get(), DataLayerListenerService.class);
				MainActivity.instance.get().startService(ir);
			}
			//
			MainActivity.instance.get().buttonController.Ambient(false);
			MainActivity.instance.get().dimmerImage.setVisibility(View.GONE);
			CommonUtils.ambience.setAmbientState(false);
		}

		@Override
		public void onUpdateAmbient() {
			// Update the content
			CommonUtils.Echo("MainActivity", "onUpdateAmbient", "---", 1);
		}
	}
}