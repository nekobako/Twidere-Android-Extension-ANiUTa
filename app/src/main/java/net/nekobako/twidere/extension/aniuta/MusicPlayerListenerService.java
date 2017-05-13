package net.nekobako.twidere.extension.aniuta;

import android.app.Notification;
import android.content.*;
import android.os.Bundle;
import android.service.notification.*;


public class MusicPlayerListenerService extends NotificationListenerService {
	public static final String ACTION_FETCH_MUSIC_METADATA = MusicPlayerListenerService.class.getName() + ".FETCH_MUSIC_METADATA";
	private static final String NOTIFICATION_PACKAGE_NAME = "jp.co.aniuta.android.aniutaap";
	private static final int NOTIFICATION_ID = 1;
	private static final String NOTIFICATION_TITLE = "android.title";
	private static final String NOTIFICATION_ARTIST = "android.text";

	private Receiver receiver;


	@Override
	public void onCreate() {
		super.onCreate();

		this.receiver = new Receiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_FETCH_MUSIC_METADATA);
		this.registerReceiver(this.receiver, intentFilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(this.receiver);
	}


	private class Receiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			for(StatusBarNotification statusBarNotification : MusicPlayerListenerService.this.getActiveNotifications()) {
				if(NOTIFICATION_PACKAGE_NAME.equals(statusBarNotification.getPackageName()) && statusBarNotification.getId() == NOTIFICATION_ID) {
					Notification notification = statusBarNotification.getNotification();
					Bundle extras = new Bundle();
					extras.putParcelable("music_metadata", new MusicMetadata((String)notification.extras.get(NOTIFICATION_TITLE), (String)notification.extras.get(NOTIFICATION_ARTIST), notification.largeIcon));
					this.setResultExtras(extras);
					return;
				}
			}
		}
	}
}
