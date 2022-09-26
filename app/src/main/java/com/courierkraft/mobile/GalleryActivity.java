package com.courierkraft.mobile;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.palette.graphics.Palette;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.Locale;

public class GalleryActivity extends WearableActivity {
	final String TAG = "GalleryActivity";
	private SharedPreferences sharedPrefs;
	private ImageView mainImage;
	TextView imageDescriptionTxt;
	TextView imageIndexTxt;
	ImageView buttonBack;
	ImageView buttonForward;
	private int currentImageIndex;
	public static boolean isShown = false;
	private ImageView dimmerImage;

	private PowerManager.WakeLock mWakeLock;
	private Handler mWakeLockHandler;

	private void setButtonTint(ImageView v, int res, String color){
		Drawable drawable = ResourcesCompat.getDrawable(getResources(), res, null);
		if ( drawable != null ) {
			drawable = DrawableCompat.wrap(drawable);
			DrawableCompat.setTint(drawable, Color.parseColor(color));
			//DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
			v.setImageDrawable(drawable);
		}
	}

	public boolean isColorDark(int color){
		double darkness = 1-(0.299*Color.red(color) + 0.587*Color.green(color) + 0.114* Color.blue(color))/255;
		// It's a dark color
		return !(darkness < 0.5); // true if dark, false if light
	}

	private void setColorPalette(Bitmap bitmap) {
		final String CTAG = "setColorPalette";
		Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
			public void onGenerated(Palette p) {
				// Use generated instance
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						int defaultValue = 0x000000;
						int domColor = p.getDominantColor(defaultValue);
						if ( isColorDark(domColor) ) {
							mainImage.setBackgroundColor(Color.parseColor("#000000"));
							imageDescriptionTxt.setTextColor((ContextCompat.getColor(getApplicationContext(), R.color.white)));
							imageIndexTxt.setTextColor((ContextCompat.getColor(getApplicationContext(), R.color.white)));
							setButtonTint(buttonBack, R.drawable.icon_arrow_back_circle_outline, "#FFFFFF");
							setButtonTint(buttonForward, R.drawable.icon_arrow_forward_circle_outline, "#FFFFFF");
							CommonUtils.Echo(TAG, CTAG, "palette is light for " + domColor, 1);
						} else {
							mainImage.setBackgroundColor(Color.parseColor("#FFFFFF"));
							imageDescriptionTxt.setTextColor((ContextCompat.getColor(getApplicationContext(), R.color.black)));
							imageIndexTxt.setTextColor((ContextCompat.getColor(getApplicationContext(), R.color.black)));
							setButtonTint(buttonBack, R.drawable.icon_arrow_back_circle_outline, "#000000");
							setButtonTint(buttonForward, R.drawable.icon_arrow_forward_circle_outline, "#000000");
							CommonUtils.Echo(TAG, CTAG, "palette is dark for " + domColor, 1);
						}
					}
				});
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final String CTAG = "onCreate";
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		// Enables Always-on
		setAmbientEnabled();
		sharedPrefs = getApplicationContext().getSharedPreferences("user_settings", Context.MODE_PRIVATE);

		isShown = true; //used in datalayerservice to check if a new activity should be changed
		mainImage = findViewById(R.id.imageView_main_image);
		buttonBack = findViewById(R.id.imageView_image_back);
		buttonForward = findViewById(R.id.imageView_image_forward);
		TextView noImageWarningTxt = findViewById(R.id.textView_no_images_warning);
		imageDescriptionTxt = findViewById(R.id.textView_img_desc);
		imageIndexTxt = findViewById(R.id.textView_img_desc2);
		dimmerImage = findViewById(R.id.imageView_dimmer3);

		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		mWakeLock = powerManager.newWakeLock((PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "CKWear:WakeLock");
		mWakeLockHandler = new Handler();
		mWakeLock.acquire(10*60*1000L /*10 minutes*/);

		if ( DataLayerListenerService.imagesList.size() <= 0 ) {
			noImageWarningTxt.setVisibility(View.VISIBLE);
		} else {
			//last image
			currentImageIndex = DataLayerListenerService.imagesList.size() - 1;
			mainImage.setImageBitmap(DataLayerListenerService.imagesList.get(currentImageIndex));
			imageDescriptionTxt.setText(DataLayerListenerService.imagesDesc.get(currentImageIndex));
			imageIndexTxt.setText(String.valueOf(currentImageIndex+1));
			setColorPalette(DataLayerListenerService.imagesList.get(currentImageIndex));
			CommonUtils.Echo(TAG, CTAG, "index: " + currentImageIndex, 1);
			// get palette

		}

		mainImage.setOnClickListener(new DoubleClickListener() {
			@Override
			public void onSingleClick() {
				CommonUtils.doToast(getApplicationContext(), "Double tap center to close...", false);
			}

			@Override
			public void onDoubleClick() {
				finish();
			}
		});

		buttonBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final String CTAG = "buttonBack-onClick";
				if ( currentImageIndex > 0 ) {
					currentImageIndex -= 1;
					mainImage.setImageBitmap(DataLayerListenerService.imagesList.get(currentImageIndex));
					imageDescriptionTxt.setText(DataLayerListenerService.imagesDesc.get(currentImageIndex));
					imageIndexTxt.setText(String.valueOf(currentImageIndex+1));
					setColorPalette(DataLayerListenerService.imagesList.get(currentImageIndex));
					CommonUtils.Vibrate(getApplicationContext(), 25, 1, 25);
				}
				CommonUtils.Echo(TAG, CTAG, "index: " + currentImageIndex, 1);
			}
		});
		buttonForward.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final String CTAG = "buttonForward-onClick";
				if ( currentImageIndex < DataLayerListenerService.imagesList.size() - 1 ) {
					currentImageIndex += 1;
					mainImage.setImageBitmap(DataLayerListenerService.imagesList.get(currentImageIndex));
					imageDescriptionTxt.setText(DataLayerListenerService.imagesDesc.get(currentImageIndex));
					imageIndexTxt.setText(String.valueOf(currentImageIndex+1));
					setColorPalette(DataLayerListenerService.imagesList.get(currentImageIndex));
					CommonUtils.Vibrate(getApplicationContext(), 25, 1, 25);
				} else {
					CommonUtils.doToast(getApplicationContext(),  String.format(Locale.getDefault(), "Only have %d images!", DataLayerListenerService.imagesList.size()), false);
				}
				CommonUtils.Echo(TAG, CTAG, "index: " + currentImageIndex, 1);
			}
		});
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy: activity destroyed");
		mWakeLock.release();
		isShown = false;
	}

	@Override
	public void onEnterAmbient(Bundle ambientDetails) {
		super.onEnterAmbient(ambientDetails);
		// Handle entering ambient mode
		String ambientPref = sharedPrefs.getString("ambient_mode", "none");
		switch ( ambientPref ) {
			case "full":
				// Questionable to stop the data service
				dimmerImage.setAlpha(1f);
				dimmerImage.setVisibility(View.VISIBLE);
				break;
			case "always_on":
				dimmerImage.setAlpha(0f);
				dimmerImage.setVisibility(View.GONE);
				break;
			default: //dim or none
				// Questionable to stop the data service
				dimmerImage.setAlpha(0.5f);
				dimmerImage.setVisibility(View.VISIBLE);
				break;
		}
		CommonUtils.ambience.setAmbientState(true);
	}

	@Override
	public void onUpdateAmbient() {
		super.onUpdateAmbient();
		// Handle exiting ambient mode. called every 60s by default
		CommonUtils.ambience.setAmbientState(true);
	}

	@Override
	public void onExitAmbient() {
		super.onExitAmbient();
		// Update the content
		dimmerImage.setVisibility(View.GONE);
	}
}