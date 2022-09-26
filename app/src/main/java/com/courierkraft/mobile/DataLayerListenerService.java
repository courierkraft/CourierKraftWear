package com.courierkraft.mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;


public class DataLayerListenerService extends WearableListenerService {
	private static final String TAG = "DataLayerSample";
	public static boolean isRunning = false;
	public static String[] buttonColors = new String[9]; //command, 8 for buttons and mode colors
	public static String[] clockColors = new String[6]; //command,  hr, min, sec, am, clock color
	public static ArrayList<Bitmap> imagesList = new ArrayList<>();
	public static ArrayList<String> imagesDesc = new ArrayList<>();
	public static ArrayList<String> macroNames = new ArrayList<>();
	private static CommonUtils.updateButtons listener = null;

	public DataLayerListenerService() {
		Log.i(TAG, "DataLayerListenerService constructed");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "DataLayerListenerService onCreate()");
		isRunning = true;
	}

	public static void setListener( CommonUtils.updateButtons l ) {
		listener = l;
	}

	@Override
	public void onDataChanged(@NotNull DataEventBuffer dataEvents) {
		final String CTAG = "onDataChanged";
		// Loop through the events and send a message
		// to the node that created the data item.
		CommonUtils.Echo(TAG, CTAG, "data changed!", 1);
		for (DataEvent event : dataEvents) {
			if (event.getType() == DataEvent.TYPE_DELETED) {
				Log.d(TAG, "DataItem deleted: " + event.getDataItem().getUri());
			} else if (event.getType() == DataEvent.TYPE_CHANGED) {
				Log.d(TAG, "DataItem changed: " + event.getDataItem().getUri());
				SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences("user_settings", Context.MODE_PRIVATE);
				if ( sharedPrefs.getBoolean("address_images", false) ) {
					DataItem dataItem = event.getDataItem();
					String DATA_TYPE_IMAGE = "data_image";
					String dataStr = DataMapItem.fromDataItem(dataItem).getDataMap().getString(DATA_TYPE_IMAGE);
					if ( dataStr != null && dataStr.contains("^") ) {
						String[] dataParts = dataStr.split("\\^");
						if ( dataParts.length == 2 ) {
							//0=base 63 img, 1=descriptor
							Log.i(TAG, "onDataChanged: got an image - > " + dataParts[1]);
							if ( imagesList.size() > 3 ) {
								imagesList.remove(0);
							}
							if ( imagesDesc.size() > 3 ) {
								imagesDesc.remove(0);
							}
							byte[] decodedString = Base64.decode(dataParts[0], Base64.DEFAULT);
							Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
							imagesList.add( decodedByte );
							imagesDesc.add( dataParts[1] );
							if ( ! GalleryActivity.isShown ) {
								Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								getApplicationContext().startActivity(intent);
							} else {
								CommonUtils.doToast(getApplicationContext(), "New gallery image received!", false);
							}
							if ( listener != null ) {
								listener.onMessageUpdate("image");
							}
							CommonUtils.Vibrate(getApplicationContext(), 25, 1, 25);
						} else {
							CommonUtils.Echo(TAG, CTAG, "invalid data parts len: " + dataParts.length, 3);
						}
					} else {
						CommonUtils.Echo(TAG, CTAG, "data null or does not contain seperator", 3);
					}
				}
			}
		}
	}

	@Override
	public void onMessageReceived (@NonNull MessageEvent messageEvent) {
		final String CTAG = "onMessageReceived";
		Log.i(TAG, "onMessageReceived called");

		String str = new String(messageEvent.getData(), StandardCharsets.UTF_8);
		String[] command = str.split("\\^");
		Log.i(TAG, "onMessageReceived: " + str + " " + command.length);
		if ( command.length == 6 && command[0].equals("update_clock") ) {
			//update clock colors
			clockColors = new String[6];
			clockColors = command;
		} else if ( command.length == 9 && command[0].equals("update_buttons")) {
			//update button colors
			buttonColors = new String[9];
			buttonColors = command;
		} else if ( command.length == 17 && command[0].equals("update_macros")) {
			//update button colors
			macroNames = new ArrayList<>();
			for ( int x=0; x<command.length; x++ ) {
				if ( x != 0 ) {
					macroNames.add(command[x]);
					CommonUtils.Echo(TAG, CTAG, "macroNames: " + x + " = " + command[x], 1);
				}
			}
		}
		if ( listener != null ) {
			listener.onMessageUpdate("update");
		}
	}

	@Override
	public void onCapabilityChanged (CapabilityInfo capabilityInfo) {
		final String CTAG = "onCapabilityChanged";
		CommonUtils.Echo(TAG, CTAG, capabilityInfo+"", 1);
		//disconnect = onCapabilityChanged: CapabilityInfo{voice_transcription, [Node{moto g(7) power, id=8415ecee, hops=2, isNearby=false}]}
		//connect = onCapabilityChanged: CapabilityInfo{voice_transcription, [Node{moto g(7) power, id=8415ecee, hops=1, isNearby=true}]}
		Set<Node> capNodes = capabilityInfo.getNodes();
		for (Node n : capNodes) {
			if (MainActivity.currentNode != null) {
				if ( n.getDisplayName().equalsIgnoreCase(MainActivity.currentNode.getDisplayName() ) ) {
					if (!n.isNearby()) {
						CommonUtils.Echo(TAG, CTAG, "node disconnected", 3);
						if (listener != null) {
							listener.onCapabilityUpdate("disconnected");
						}
					} else {
						CommonUtils.Echo(TAG, CTAG, "node reconnected", 1);
						if (listener != null) {
							listener.onCapabilityUpdate("connected");
						}
					}
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy: service destroyed");
		isRunning = false;
	}
}

