package com.courierkraft.mobile;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Locale;

public class SelectDeviceActivity extends AppCompatActivity {
	private final String TAG = "SelectDeviceActivity";
	private Context context;
	protected LinearLayout deviceContainerView;
	protected ImageView refreshDevices;
	private ArrayList<Node> nodes = new ArrayList<>();

	private LinearLayout deviceRowLayout() {
		LinearLayout rowLLayout = new LinearLayout( context );
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,	84);
		rowLLayout.setLayoutParams(layoutParams);
		rowLLayout.setOrientation(LinearLayout.HORIZONTAL);
		rowLLayout.setGravity( Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL );
		//
		return rowLLayout;
	}

	private View deviceDividerView() {
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,	1);
		View divider = new View( context );
		divider.setLayoutParams(layoutParams);
		divider.setBackgroundColor( Color.parseColor("#707070") );
		return divider;
	}

	private TextView deviceTextView(String str, int minSize, int maxSize, String color, int[] padding, boolean singleLine, int maxLines, int horiGrav, int vertGrav) {
		TextView textView = new TextView( context );
		textView.setAutoSizeTextTypeUniformWithConfiguration(minSize, maxSize, 2, TypedValue.COMPLEX_UNIT_SP);
		textView.setTextColor(Color.parseColor(color));
		textView.setText(str);
		textView.setGravity( horiGrav | vertGrav);
		textView.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
		if ( padding != null && padding.length == 4 ) {
			textView.setPadding(padding[0],padding[1],padding[2],padding[3]);
		}
		if ( maxLines > 0 ) {
			textView.setMaxLines(maxLines);
		}
		if ( singleLine ) {
			textView.setSingleLine(singleLine);
		}
		return textView;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_device);
		context = getApplicationContext();
		final String CTAG = "onCreate";
		//
		deviceContainerView = findViewById(R.id.device_container_layout);
		refreshDevices = findViewById(R.id.imageView_refresh_devices);
		//
		refreshDevices.setOnClickListener(v -> {
			refreshDevices.setEnabled( false );
			//
			RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			rotate.setDuration(1500);
			rotate.setRepeatCount(Animation.INFINITE);
			rotate.setInterpolator(new LinearInterpolator());
			refreshDevices.startAnimation(rotate);
			//
			CommonUtils.Echo(TAG, CTAG, "looking for nearest...", 1);
			Task<CapabilityInfo> capabilitiesTask = Wearable.getCapabilityClient(context).getCapability("voice_transcription", CapabilityClient.FILTER_ALL);
			capabilitiesTask.addOnSuccessListener(capabilityInfo -> {
				deviceContainerView.removeAllViews();
				nodes.clear();
				nodes.addAll( capabilityInfo.getNodes() );
				for (Node node : nodes) {
					LinearLayout hLayout = deviceRowLayout();
					TextView txt = deviceTextView( String.format(Locale.getDefault(), "%s", node.getDisplayName()), 18, 28, "#FFFFFF", new int[]{ 2,8,2,8}, true, 1, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL );
					txt.setOnClickListener(view -> {
						MainActivity.currentNode = node;
						finish();
					});
					hLayout.addView( txt );
					deviceContainerView.addView( deviceDividerView() );
					deviceContainerView.addView( hLayout );
					CommonUtils.Echo(TAG, CTAG, String.format(Locale.getDefault(), "%s", node.getDisplayName()), 1);
				}
				deviceContainerView.addView( deviceDividerView() );
				deviceContainerView.invalidate();
				refreshDevices.setEnabled( true );
				refreshDevices.clearAnimation();
			});
			capabilitiesTask.addOnFailureListener(e -> {
				CommonUtils.Echo(TAG, CTAG, "failure", 1);
				//
				LinearLayout hLayout = deviceRowLayout();
				TextView txt = deviceTextView( "Failed to find devices!", 18, 28, "#FFFFFF", new int[]{ 2,8,2,8}, true, 1, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL );
				hLayout.addView( txt );
				deviceContainerView.addView( deviceDividerView() );
				deviceContainerView.addView( hLayout );
				//
				refreshDevices.setEnabled( true );
				refreshDevices.clearAnimation();
			});
			capabilitiesTask.addOnCompleteListener(task -> {
				if ( nodes.size() <= 0 ) {
					LinearLayout hLayout = deviceRowLayout();
					TextView txt = deviceTextView( "No devices found!", 18, 28, "#FFFFFF", new int[]{ 2,8,2,8}, true, 1, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL );
					hLayout.addView( txt );
					deviceContainerView.addView( hLayout );
					deviceContainerView.addView( deviceDividerView() );
					//
					hLayout = deviceRowLayout();
					txt = deviceTextView( "Check bluetooth.", 14, 28, "#FFFFFF", new int[]{ 2,8,2,8}, true, 1, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL );
					hLayout.addView( txt );
					deviceContainerView.addView( hLayout );
					deviceContainerView.addView( deviceDividerView() );
					//
					hLayout = deviceRowLayout();
					txt = deviceTextView( "Check the Wear OS app on your phone.", 14, 28, "#FFFFFF", new int[]{ 2,8,2,8}, true, 1, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL );
					hLayout.addView( txt );
					deviceContainerView.addView( hLayout );
					deviceContainerView.addView( deviceDividerView() );
					//
				}
				refreshDevices.setEnabled( true );
				refreshDevices.clearAnimation();
			});

		});
	}

	@Override
	public void onResume() {
		super.onResume();
		// -- init
		LinearLayout hLayout = deviceRowLayout();
		TextView txt = deviceTextView( "Searching...", 18, 28, "#FFFFFF", new int[]{ 2,8,2,8}, true, 1, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL );
		hLayout.addView( txt );
		deviceContainerView.addView( hLayout );
		deviceContainerView.addView( deviceDividerView() );
		deviceContainerView.invalidate();
		refreshDevices.performClick();
	}
}

