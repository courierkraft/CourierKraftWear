package com.courierkraft.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ButtonController implements CommonUtils.updateButtons {
	final String TAG = "SetButtonModeColors";
	public static Date dateLastButtonPressed;
	public static int currentMode = -1;
	Context context;
	TextView buttonTxt1Ref;
	TextView buttonTxt2Ref;
	TextView buttonTxt3Ref;
	TextView buttonTxt4Ref;
	ImageView button1Ref;
	ImageView button2Ref;
	ImageView button3Ref;
	ImageView button4Ref;
	Handler handler;

	public ButtonController(Activity activity ) {
		this.context = activity.getApplicationContext();
		this.handler = new Handler(Looper.getMainLooper());
		DataLayerListenerService.setListener( this );
		buttonTxt1Ref = activity.findViewById(R.id.textView_btn1);
		buttonTxt2Ref = activity.findViewById(R.id.textView_btn2);
		buttonTxt3Ref = activity.findViewById(R.id.textView_btn3);
		buttonTxt4Ref = activity.findViewById(R.id.textView_btn4);
		buttonTxt1Ref.setTypeface(ResourcesCompat.getFont(context, R.font.gugi));
		buttonTxt2Ref.setTypeface(ResourcesCompat.getFont(context, R.font.gugi));
		buttonTxt3Ref.setTypeface(ResourcesCompat.getFont(context, R.font.gugi));
		buttonTxt4Ref.setTypeface(ResourcesCompat.getFont(context, R.font.gugi));
		button1Ref =    activity.findViewById(R.id.imageView_btn1);
		button2Ref =    activity.findViewById(R.id.imageView_btn2);
		button3Ref =    activity.findViewById(R.id.imageView_btn3);
		button4Ref =    activity.findViewById(R.id.imageView_btn4);
		buttonSetup();
	}
	public void Ambient(boolean state){
		if ( state ) {
			button1Ref.setEnabled(false);
			button2Ref.setEnabled(false);
			button3Ref.setEnabled(false);
			button4Ref.setEnabled(false);
			/*
			buttonTxt1Ref.getPaint().setAntiAlias(true);
			buttonTxt2Ref.getPaint().setAntiAlias(true);
			buttonTxt3Ref.getPaint().setAntiAlias(true);
			buttonTxt4Ref.getPaint().setAntiAlias(true);
			 */
		} else {
			button1Ref.setEnabled(true);
			button2Ref.setEnabled(true);
			button3Ref.setEnabled(true);
			button4Ref.setEnabled(true);
			/*
			buttonTxt1Ref.getPaint().setAntiAlias(false);
			buttonTxt2Ref.getPaint().setAntiAlias(false);
			buttonTxt3Ref.getPaint().setAntiAlias(false);
			buttonTxt4Ref.getPaint().setAntiAlias(false);
			 */
		}
	}
	public void buttonSetup() {
		final String CTAG = "buttonSetup";
		button1Ref.setOnClickListener(view -> {
			CommonUtils.Echo(TAG, CTAG, "button 1 single click", 1);
			if (MainActivity.currentNode != null) {
				switch (currentMode) {
					case 0:
						Wearable.getMessageClient(context).sendMessage(MainActivity.currentNode.getId(), "/voice_transcription", "0".getBytes());
						CommonUtils.Vibrate(context, 50, 2, 250);
						dateLastButtonPressed = new Date();
						//startMacroReplay( 0 );
						break;
					case 1:
						//mode button
						break;
					case 2:
						CommonUtils.Vibrate(context, 50, 2, 250);
						Wearable.getMessageClient(context).sendMessage(MainActivity.currentNode.getId(), "/voice_transcription", "7".getBytes());
						dateLastButtonPressed = new Date();
						//startMacroReplay( 7 );
						break;
					case 3:
						CommonUtils.Vibrate(context, 50, 2, 250);
						Wearable.getMessageClient(context).sendMessage(MainActivity.currentNode.getId(), "/voice_transcription", "10".getBytes());
						dateLastButtonPressed = new Date();
						//startMacroReplay( 10 );
						break;
					case 4:
						CommonUtils.Vibrate(context, 50, 2, 250);
						Wearable.getMessageClient(context).sendMessage(MainActivity.currentNode.getId(), "/voice_transcription", "13".getBytes());
						dateLastButtonPressed = new Date();
						//startMacroReplay( 13 );
						break;
					default:
						CommonUtils.Echo(TAG, CTAG, "unknown mode="+currentMode, 3);
						break;
				}
			} else {
				CommonUtils.Echo(TAG, CTAG, "node is null!", 3);
			}
		});
		button1Ref.setOnLongClickListener(v -> {
			if ( currentMode != 1 ) {
				setButtonsToModeColors(1);
			} else {
				setButtonsToModeColors(0);
			}
			dateLastButtonPressed = new Date();
			return true;
		});
		//
		button2Ref.setOnClickListener(v -> {
			if (MainActivity.currentNode != null) {
				switch (currentMode) {
					case 0:
						CommonUtils.Vibrate(context, 50, 2, 250);
						Wearable.getMessageClient(context).sendMessage(MainActivity.currentNode.getId(), "/voice_transcription", "1".getBytes());
						dateLastButtonPressed = new Date();
						//startMacroReplay( 1 );
						break;
					case 1:
						CommonUtils.Vibrate(context, 50, 2, 250);
						Wearable.getMessageClient(context).sendMessage(MainActivity.currentNode.getId(), "/voice_transcription", "4".getBytes());
						dateLastButtonPressed = new Date();
						//startMacroReplay( 4 );
						break;
					case 2:
						//mode button
						break;
					case 3:
						CommonUtils.Vibrate(context, 50, 2, 250);
						Wearable.getMessageClient(context).sendMessage(MainActivity.currentNode.getId(), "/voice_transcription", "11".getBytes());
						dateLastButtonPressed = new Date();
						//startMacroReplay( 11 );
						break;
					case 4:
						CommonUtils.Vibrate(context, 50, 2, 250);
						Wearable.getMessageClient(context).sendMessage(MainActivity.currentNode.getId(), "/voice_transcription", "14".getBytes());
						dateLastButtonPressed = new Date();
						//startMacroReplay( 14 );
						break;
				}
			}
		});
		button2Ref.setOnLongClickListener(v -> {
			if ( currentMode != 2 ) {
				setButtonsToModeColors(2);
			} else {
				setButtonsToModeColors(0);
			}
			dateLastButtonPressed = new Date();
			return true;
		});
		//
		button3Ref.setOnClickListener(v -> {
			if (MainActivity.currentNode != null) {
				switch (currentMode) {
					case 0:
						CommonUtils.Vibrate(context, 50, 2, 250);
						Wearable.getMessageClient(context).sendMessage(MainActivity.currentNode.getId(), "/voice_transcription", "2".getBytes());
						dateLastButtonPressed = new Date();
						break;
					case 1:
						CommonUtils.Vibrate(context, 50, 2, 250);
						Wearable.getMessageClient(context).sendMessage(MainActivity.currentNode.getId(), "/voice_transcription", "5".getBytes());
						dateLastButtonPressed = new Date();
						break;
					case 2:
						CommonUtils.Vibrate(context, 50, 2, 250);
						Wearable.getMessageClient(context).sendMessage(MainActivity.currentNode.getId(), "/voice_transcription", "8".getBytes());
						dateLastButtonPressed = new Date();
						break;
					case 3:
						//mode button
						break;
					case 4:
						CommonUtils.Vibrate(context, 50, 2, 250);
						Wearable.getMessageClient(context).sendMessage(MainActivity.currentNode.getId(), "/voice_transcription", "15".getBytes());
						dateLastButtonPressed = new Date();
						break;
				}
			}
		});
		button3Ref.setOnLongClickListener(v -> {
			if ( currentMode != 3 ) {
				setButtonsToModeColors(3);
			} else {
				setButtonsToModeColors(0);
			}
			dateLastButtonPressed = new Date();
			return true;
		});
		//
		button4Ref.setOnClickListener(v -> {
			if (MainActivity.currentNode != null) {
				switch (currentMode) {
					case 0:
						CommonUtils.Vibrate(context, 50, 2, 250);
						Wearable.getMessageClient(context).sendMessage(MainActivity.currentNode.getId(), "/voice_transcription", "3".getBytes());
						dateLastButtonPressed = new Date();
						break;
					case 1:
						CommonUtils.Vibrate(context, 50, 2, 250);
						Wearable.getMessageClient(context).sendMessage(MainActivity.currentNode.getId(), "/voice_transcription", "6".getBytes());
						dateLastButtonPressed = new Date();
						break;
					case 2:
						CommonUtils.Vibrate(context, 50, 2, 250);
						Wearable.getMessageClient(context).sendMessage(MainActivity.currentNode.getId(), "/voice_transcription", "9".getBytes());
						dateLastButtonPressed = new Date();
						break;
					case 3:
						CommonUtils.Vibrate(context, 50, 2, 250);
						Wearable.getMessageClient(context).sendMessage(MainActivity.currentNode.getId(), "/voice_transcription", "12".getBytes());
						dateLastButtonPressed = new Date();
						break;
					case 4:
						//mode button
						break;
				}
			}
		});
		button4Ref.setOnLongClickListener(v -> {
			if ( currentMode != 4 ) {
				setButtonsToModeColors(4);
			} else {
				setButtonsToModeColors(0);
			}
			dateLastButtonPressed = new Date();
			return true;
		});
	}
	public void UpdateColorPrefs() {
		final String CTAG = "UpdateColorPrefs";
		String[] colors;
		if ( DataLayerListenerService.buttonColors[0] != null && ! DataLayerListenerService.buttonColors[0].isEmpty() ) {
			SharedPreferences sharedPrefs = context.getSharedPreferences("user_settings", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPrefs.edit();
			colors = DataLayerListenerService.buttonColors;
			//colors[0] = command
			editor.putString("color_button_index", colors[1]);
			editor.putString("color_button_middle", colors[2]);
			editor.putString("color_button_ring", colors[3]);
			editor.putString("color_button_pinky", colors[4]);
			editor.putString("color_mode_index", colors[5]);
			editor.putString("color_mode_middle", colors[6]);
			editor.putString("color_mode_ring", colors[7]);
			editor.putString("color_mode_pinky", colors[8]);
			editor.apply();
			DataLayerListenerService.buttonColors = new String[9];
			CommonUtils.Echo(TAG, CTAG, "updated button colors in prefs", 1);
		}
		if ( DataLayerListenerService.clockColors[0] != null && ! DataLayerListenerService.clockColors[0].isEmpty() ) {
			SharedPreferences sharedPrefs = context.getSharedPreferences("user_settings", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPrefs.edit();
			colors = DataLayerListenerService.clockColors;
			//colors[0] = command
			editor.putString("color_clock_primary", colors[1]);
			editor.putString("color_clock_hr", colors[2]);
			editor.putString("color_clock_min", colors[3]);
			editor.putString("color_clock_sec", colors[4]);
			editor.putString("color_mode_ampm", colors[5]);
			editor.apply();
			DataLayerListenerService.clockColors = new String[6];
			CommonUtils.Echo(TAG, CTAG, "updated clock colors in prefs", 1);
		}
		if ( DataLayerListenerService.macroNames != null && DataLayerListenerService.macroNames.size() == 16 ) {
			SharedPreferences sharedPrefs = context.getSharedPreferences("user_settings", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPrefs.edit();
			ArrayList<String> names = DataLayerListenerService.macroNames;
			//colors[0] = command
			editor.putString("macro_1",  names.get(0));
			editor.putString("macro_2",  names.get(1));
			editor.putString("macro_3",  names.get(2));
			editor.putString("macro_4",  names.get(3));
			editor.putString("macro_5",  names.get(4));
			editor.putString("macro_6",  names.get(5));
			editor.putString("macro_7",  names.get(6));
			editor.putString("macro_8",  names.get(7));
			editor.putString("macro_9",  names.get(8));
			editor.putString("macro_10", names.get(9));
			editor.putString("macro_11", names.get(10));
			editor.putString("macro_12", names.get(11));
			editor.putString("macro_13", names.get(12));
			editor.putString("macro_14", names.get(13));
			editor.putString("macro_15", names.get(14));
			editor.putString("macro_16", names.get(15));
			editor.apply();
			DataLayerListenerService.buttonColors = new String[9];
			CommonUtils.Echo(TAG, CTAG, "updated button colors in prefs", 1);
		}
		CommonUtils.Echo(TAG, CTAG, "buttons and clock: no update available", 1);
	}
	public void setText(int mode) {
		SharedPreferences sharedPrefs = context.getSharedPreferences("user_settings", Context.MODE_PRIVATE);
		handler.post(() -> {
			if ( ! sharedPrefs.getString("macro_1", "none").equals("none") ) {
				//ArrayList<String> names = new ArrayList<>(DataLayerListenerService.macroNames);
				switch ( mode ) {
					case 0:
						buttonTxt1Ref.setText(sharedPrefs.getString("macro_1", "broken!"));
						buttonTxt2Ref.setText(sharedPrefs.getString("macro_2", "broken!"));
						buttonTxt3Ref.setText(sharedPrefs.getString("macro_3", "broken!"));
						buttonTxt4Ref.setText(sharedPrefs.getString("macro_4", "broken!"));
						break;
					case 1:
						buttonTxt1Ref.setText("");
						buttonTxt2Ref.setText(sharedPrefs.getString("macro_5", "broken!"));
						buttonTxt3Ref.setText(sharedPrefs.getString("macro_6", "broken!"));
						buttonTxt4Ref.setText(sharedPrefs.getString("macro_7", "broken!"));
						break;
					case 2:
						buttonTxt1Ref.setText(sharedPrefs.getString("macro_8", "broken!"));
						buttonTxt2Ref.setText("");
						buttonTxt3Ref.setText(sharedPrefs.getString("macro_9", "broken!"));
						buttonTxt4Ref.setText(sharedPrefs.getString("macro_10", "broken!"));
						break;
					case 3:
						buttonTxt1Ref.setText(sharedPrefs.getString("macro_11", "broken!"));
						buttonTxt2Ref.setText(sharedPrefs.getString("macro_12", "broken!"));
						buttonTxt3Ref.setText("");
						buttonTxt4Ref.setText(sharedPrefs.getString("macro_13", "broken!"));
						break;
					case 4:
						buttonTxt1Ref.setText(sharedPrefs.getString("macro_14", "broken!"));
						buttonTxt2Ref.setText(sharedPrefs.getString("macro_15", "broken!"));
						buttonTxt3Ref.setText(sharedPrefs.getString("macro_16", "broken!"));
						buttonTxt4Ref.setText("");
						break;
				}
			} else {
				switch ( mode ) {
					case 0:
						buttonTxt1Ref.setText("1"); buttonTxt2Ref.setText("2");
						buttonTxt3Ref.setText("3"); buttonTxt4Ref.setText("4");
						break;
					case 1:
						buttonTxt1Ref.setText(""); buttonTxt2Ref.setText("5");
						buttonTxt3Ref.setText("6"); buttonTxt4Ref.setText("7");
						break;
					case 2:
						buttonTxt1Ref.setText("8"); buttonTxt2Ref.setText("");
						buttonTxt3Ref.setText("9"); buttonTxt4Ref.setText(R.string.btn_ctrl_10);
						break;
					case 3:
						buttonTxt1Ref.setText(R.string.btn_ctrl_11); buttonTxt2Ref.setText(R.string.btn_ctrl_12);
						buttonTxt3Ref.setText(""); buttonTxt4Ref.setText(R.string.btn_ctrl_13);
						break;
					case 4:
						buttonTxt1Ref.setText(R.string.btn_ctrl_14); buttonTxt2Ref.setText(R.string.btn_ctrl_15);
						buttonTxt3Ref.setText(R.string.btn_ctrl_16); buttonTxt4Ref.setText("");
						break;
				}
			}
			String color = sharedPrefs.getString("color_button_index", null);
			if ( color != null ) {
				if ( CommonUtils.isBrightColor( Integer.parseInt( color ) ) ) {
					buttonTxt1Ref.setTextColor( ContextCompat.getColor(context, R.color.black ) );
				} else {
					buttonTxt1Ref.setTextColor( ContextCompat.getColor(context, R.color.white ) );
				}
			}
			color = sharedPrefs.getString("color_button_middle", null);
			if ( color != null ) {
				if ( CommonUtils.isBrightColor( Integer.parseInt( color ) ) ) {
					buttonTxt2Ref.setTextColor( ContextCompat.getColor(context, R.color.black ) );
				} else {
					buttonTxt2Ref.setTextColor( ContextCompat.getColor(context, R.color.white ) );
				}
			}
			color = sharedPrefs.getString("color_button_ring", null);
			if ( color != null ) {
				if ( CommonUtils.isBrightColor( Integer.parseInt( color ) ) ) {
					buttonTxt3Ref.setTextColor( ContextCompat.getColor(context, R.color.black ) );
				} else {
					buttonTxt3Ref.setTextColor( ContextCompat.getColor(context, R.color.white ) );
				}
			}
			color = sharedPrefs.getString("color_button_pinky", null);
			if ( color != null ) {
				if ( CommonUtils.isBrightColor( Integer.parseInt( color ) ) ) {
					buttonTxt4Ref.setTextColor( ContextCompat.getColor(context, R.color.black ) );
				} else {
					buttonTxt4Ref.setTextColor( ContextCompat.getColor(context, R.color.white ) );
				}
			}
		});
	}
	private void setColorAll( String color ){
		button1Ref.setColorFilter(Integer.parseInt( color ), PorterDuff.Mode.MULTIPLY);
		button2Ref.setColorFilter(Integer.parseInt( color ), PorterDuff.Mode.MULTIPLY);
		button3Ref.setColorFilter(Integer.parseInt( color ), PorterDuff.Mode.MULTIPLY);
		button4Ref.setColorFilter(Integer.parseInt( color ), PorterDuff.Mode.MULTIPLY);

		/*
		button1Ref.setBackgroundColor( Integer.parseInt( color ) );
		button2Ref.setBackgroundColor( Integer.parseInt( color ) );
		button3Ref.setBackgroundColor( Integer.parseInt( color ) );
		button4Ref.setBackgroundColor( Integer.parseInt( color ) );

		setTintedDrawable(button1Ref, R.drawable.button_shape_1, color);
		setTintedDrawable(button2Ref, R.drawable.button_shape_2, color);
		setTintedDrawable(button3Ref, R.drawable.button_shape_3, color);
		setTintedDrawable(button4Ref, R.drawable.button_shape_4, color);
		 */
	}
	private void setTintedDrawable(ImageView v, int drawRes, String color) {
		/*
		Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), drawRes, null);
		if ( drawable != null ) {
			drawable = DrawableCompat.wrap(drawable);
			DrawableCompat.setTint(drawable, Integer.parseInt( color ));
			DrawableCompat.setTintMode(drawable, PorterDuff.Mode.MULTIPLY);
			v.setBackground(drawable);
		}
		 */
	}

	public void setButtonsToModeColors(int newMode) {
		final String CTAG = "setButtonsToModeColors";
		CommonUtils.Echo(TAG, CTAG, String.format(Locale.getDefault(), "%d -> %d", newMode, currentMode), 1);
		if ( newMode != currentMode ) {
			currentMode = newMode;
			setText(currentMode);
			UpdateColorPrefs();
			SharedPreferences sharedPrefs = context.getSharedPreferences("user_settings", Context.MODE_PRIVATE);
			ArrayList<String> missingPrefs = new ArrayList<>();
			new Handler(Looper.getMainLooper()).post(() -> {
				String color;
				if (currentMode == 0) {
					CommonUtils.Echo(TAG, CTAG, "setting buttons colors to mode: " + currentMode, 1);
					color = sharedPrefs.getString("color_button_index", null); //mode 1
					if (color != null) {
						button1Ref.setColorFilter(Integer.parseInt(color), PorterDuff.Mode.MULTIPLY);
						//button1Ref.setBackgroundColor( Integer.parseInt( color ) );
					} else {
						button1Ref.setColorFilter(R.color.white_smoke, PorterDuff.Mode.MULTIPLY);
						missingPrefs.add("color_button_index");
					}
					color = sharedPrefs.getString("color_button_middle", null); //mode 0
					if (color != null) {
						button2Ref.setColorFilter(Integer.parseInt(color), PorterDuff.Mode.MULTIPLY);
						//button2Ref.setBackgroundColor( Integer.parseInt( color ) );
					} else {
						button2Ref.setColorFilter(R.color.white_smoke, PorterDuff.Mode.MULTIPLY);
						missingPrefs.add("color_button_middle");
					}
					color = sharedPrefs.getString("color_button_ring", null); //mode 0
					if (color != null) {
						button3Ref.setColorFilter(Integer.parseInt(color), PorterDuff.Mode.MULTIPLY);
						//button3Ref.setBackgroundColor( Integer.parseInt( color ) );
					} else {
						button3Ref.setColorFilter(R.color.white_smoke, PorterDuff.Mode.MULTIPLY);
						missingPrefs.add("color_button_ring");
					}
					color = sharedPrefs.getString("color_button_pinky", null); //mode 0
					if (color != null) {
						button4Ref.setColorFilter(Integer.parseInt(color), PorterDuff.Mode.MULTIPLY);
						//button4Ref.setBackgroundColor( Integer.parseInt( color ) );
					} else {
						button4Ref.setColorFilter(R.color.white_smoke, PorterDuff.Mode.MULTIPLY);
						missingPrefs.add("color_button_pinky");
					}
					CommonUtils.Vibrate(MainActivity.instance.get(), 12, 2, 12);
				} else if (currentMode == 1) {
					color = sharedPrefs.getString("color_mode_index", null); //mode 1
					if (color != null) {
						CommonUtils.Echo(TAG, CTAG, "setting buttons colors to mode: " + currentMode, 1);
						setColorAll(color);
					} else {
						setColorAll(String.valueOf(R.color.white_smoke));
						missingPrefs.add("color_mode_index");
					}
					CommonUtils.Vibrate(MainActivity.instance.get(), 25, 1, 25);
				} else if (currentMode == 2) {
					color = sharedPrefs.getString("color_mode_middle", null); //mode 1
					if (color != null) {
						CommonUtils.Echo(TAG, CTAG, "setting buttons colors to mode: " + currentMode, 1);
						setColorAll(color);
					} else {
						setColorAll(String.valueOf(R.color.white_smoke));
						missingPrefs.add("color_mode_middle");
					}
					CommonUtils.Vibrate(MainActivity.instance.get(), 25, 1, 25);
				} else if (currentMode == 3) {
					color = sharedPrefs.getString("color_mode_ring", null); //mode 1
					if (color != null) {
						CommonUtils.Echo(TAG, CTAG, "setting buttons colors to mode: " + currentMode, 1);
						setColorAll(color);
					} else {
						setColorAll(String.valueOf(R.color.white_smoke));
						missingPrefs.add("color_mode_ring");
					}
					CommonUtils.Vibrate(MainActivity.instance.get(), 25, 1, 25);
				} else if (currentMode == 4) {
					color = sharedPrefs.getString("color_mode_pinky", null); //mode 1
					if (color != null) {
						CommonUtils.Echo(TAG, CTAG, "setting buttons colors to mode: " + currentMode, 1);
						setColorAll(color);
					} else {
						setColorAll(String.valueOf(R.color.white_smoke));
						missingPrefs.add("color_mode_pinky");
					}
					CommonUtils.Vibrate(MainActivity.instance.get(), 25, 1, 25);
				}
			/*
			button1Ref.invalidate();
			button2Ref.invalidate();
			button3Ref.invalidate();
			button4Ref.invalidate();
			 */
				MainActivity.instance.get().mainLayout.invalidate();
			});
			if (missingPrefs.size() > 0) {
				CommonUtils.Echo(TAG, CTAG, String.valueOf(missingPrefs), 3);
			}
		}
	}

	static class ResetTimerTask extends TimerTask {
		final String TAG = "ResetTimerTask";
		Date myId;
		Handler handler;
		boolean isRunning;
		public Timer resetTimer;
		public ResetTimerTask(){
			CommonUtils.Echo(TAG, "constructor", myId + " starting", 1);
			this.myId = new Date();
			this.handler = new Handler(Looper.getMainLooper());
		}
		public void start() {
			if ( ! this.isRunning ) {
				resetTimer = new Timer();
				this.isRunning = true;
				this.resetTimer.scheduleAtFixedRate(this, 10, 10*1000);
			}
		}
		public void stop() {
			if ( this.isRunning && resetTimer != null ) {
				this.resetTimer.cancel();
				this.isRunning = false;
			}
		}
		@Override
		public void run() {
			final String CTAG = "run";
			if ( currentMode != 0 ) {
				Date currentDate = new Date();
				if ( dateLastButtonPressed != null ) {
					long diffInMs = currentDate.getTime() - dateLastButtonPressed.getTime();
					long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
					if ( diffInSec > 5 ) {
						if ( MainActivity.buttonControllerRef.get() != null ) {
							MainActivity.buttonControllerRef.get().setButtonsToModeColors(0);
							CommonUtils.Echo(TAG, CTAG, myId + " resetting mode - diff:" + diffInSec, 1);
						} else {
							CommonUtils.Echo(TAG, CTAG, myId + " button controller ref is null", 3);
						}
					}
				}
			}
		}
	}

	@Override
	public void onMessageUpdate(String which) {
		setButtonsToModeColors(currentMode);
	}

	@Override
	public void onCapabilityUpdate(String state) {
		if ( state.equals("disconnected") ) {
			handler.post(() -> setColorAll( String.valueOf(R.color.black_54p) ));
		}
	}
}
