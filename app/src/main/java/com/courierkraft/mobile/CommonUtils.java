package com.courierkraft.mobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;
import java.util.Locale;

public class CommonUtils {
	public static Ambience ambience = new Ambience();
	public static void Echo(String p, String c, String m, int level ) {
		switch (level) {
			case 1:
				Log.i(p + "-" + c, m);
				break;
			case 2:
				Log.d(p + "-" + c, m);
				break;
			case 3:
				Log.e(p + "-" + c, m);
				break;
			default:
				Log.e(p + " >>> fix this <<< " + c, m);
				break;
		}
	}
	public static void Vibrate (final Context c, final int dur, final int cnt, final int slp) {

		Thread thread = new Thread() {
			public void run() {
				for ( int x=0; x<cnt; x++ ) {
					Vibrator v = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
					v.vibrate(VibrationEffect.createOneShot(dur, VibrationEffect.DEFAULT_AMPLITUDE));
					try {
						Thread.sleep(slp);
					} catch (Exception e) {
						return;
					}
				}
			}
		}; thread.start();

	}
	public interface updateButtons {
		void onMessageUpdate( String which );
		void onCapabilityUpdate( String state );
	}

	static class Ambience {
		// an object which can be used across different activities etc by using commonutils
		private boolean state;
		public Ambience(){
			this.state = false;
		}
		public void setAmbientState(boolean s) {
			this.state = s;
		}
		public boolean getAmbientState() {
			return state;
		}
	}
	public static void doToast(Context c, String msg, boolean isLong) {
		if ( c != null ) {
			Handler handler = new Handler(Looper.getMainLooper());
			handler.post(() -> {
				if ( isLong ) {
					Toast.makeText(c, msg, Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
	//
	public static boolean isBrightColor(int color) {
		if (android.R.color.transparent == color) {
			return true;
		}
		boolean rtnValue = false;
		int[] rgb = { Color.red(color), Color.green(color), Color.blue(color) };
		int brightness = (int) Math.sqrt(rgb[0] * rgb[0] * .241 + rgb[1] * rgb[1] * .691 + rgb[2] * rgb[2] * .068);
		// color is light
		if (brightness >= 175) {
			rtnValue = true;
		}
		return rtnValue;
	}
	public static class SensorReceiver extends BroadcastReceiver {
		public IntentFilter filter;
		public boolean isRegistered;
		//
		SensorManager sensorManager;
		Sensor bpmSensor;
		SensorEventListener sensorEventListener;
		//
		public int sensorAccuracy;
		public int lastBPM = -1;
		public SensorReceiver( String[] f ) {
			this.filter = new IntentFilter();
			for ( String s : f ) {
				this.filter.addAction( s );
			}
		}
		public SensorReceiver(){}
		public boolean registerThis(Context context) {
			try {
				sensorManager = ((SensorManager) context.getSystemService(Context.SENSOR_SERVICE));
				bpmSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
				sensorEventListener = new SensorEventListener() {
					@Override
					public void onSensorChanged(SensorEvent event) {
						//CommonUtils.Echo("SensorReceiver", "onSensorChanged", Arrays.toString(event.values)+"", 1);
						if ( event.values.length == 1 ) {
							if ( sensorAccuracy == SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM || sensorAccuracy == SensorManager.SENSOR_STATUS_ACCURACY_HIGH ) {
								if ( Math.round( event.values[0] ) != lastBPM ) {
									lastBPM = Math.round( event.values[0] );
									Intent sensorIntent = new Intent(context, CommonUtils.SensorReceiver.class);
									sensorIntent.putExtra("type", "onSensorChanged");
									sensorIntent.putExtra("value", String.valueOf(lastBPM));
									context.sendBroadcast(sensorIntent);
								}
							}
						}
					}

					@Override
					public void onAccuracyChanged(Sensor sensor, int accuracy) {
						if ( accuracy != sensorAccuracy ) {
							sensorAccuracy = accuracy;
						}
					}
				};
				sensorManager.registerListener(sensorEventListener, bpmSensor, SensorManager.SENSOR_DELAY_UI);
				context.registerReceiver( this, this.filter );
				isRegistered = true;
				return true;
			} catch ( Exception e ) {
				e.printStackTrace();
				return false;
			}
		}
		public boolean unregisterThis(Context context) {
			try {
				context.unregisterReceiver( this );
				sensorManager.unregisterListener(sensorEventListener);
				isRegistered = false;
				return true;
			} catch ( Exception e ) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		public void onReceive(Context context, Intent i) {
			final String CTAG = "onReceive";
			String action = i.getAction()+""; //"null"
			switch ( action ) {
				case Intent.ACTION_BATTERY_CHANGED:
					if ( i.getIntExtra("level", -1) > -1 ) {
						CommonUtils.Echo("SensorReceiver-Changed", CTAG, String.format(Locale.getDefault(), "battery level: %d", i.getIntExtra("level", -1)), 1);
					}
					break;
				case Intent.ACTION_BATTERY_LOW:
					if ( i.getStringExtra("battery_low") != null ) {
						CommonUtils.Echo("SensorReceiver-Low", CTAG, String.format(Locale.getDefault(), "battery low: %s", i.getStringExtra("battery_low")), 1);
					}
					if ( i.getIntExtra("level", -1) > -1 ) {
						CommonUtils.Echo("SensorReceiver-Low", CTAG, String.format(Locale.getDefault(), "battery level: %d", i.getIntExtra("level", -1)), 1);
					}
					break;
				default:
					String type = i.getStringExtra("type");
					if ( type != null ) {
						switch ( type ) {
							case "onSensorChanged":
								CommonUtils.Echo("SensorReceiver", CTAG, String.format(Locale.getDefault(), "bpm: %s", i.getStringExtra("value")), 1);
								break;
							default:
								CommonUtils.Echo("SensorReceiver", "onReceive", "unknown type: " + type, 1);
								CommonUtils.Echo("SensorReceiver", "onReceive", i.toUri(Intent.URI_INTENT_SCHEME)+"\n"+action, 1);
								break;
						}
					}
					break;
			}
		}
	}

	public static long diffDatesInMillis(Date older, Date newer ) {
		final String CTAG = "diffBetweenTwoDates";
		return Math.abs( older.getTime() - newer.getTime() );
	}
}

