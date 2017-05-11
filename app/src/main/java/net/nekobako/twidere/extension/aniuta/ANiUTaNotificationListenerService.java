package net.nekobako.twidere.extension.aniuta;

import android.app.Notification;
import android.content.*;
import android.os.Bundle;
import android.service.notification.*;


public class ANiUTaNotificationListenerService extends NotificationListenerService {
	public static final String ACTION = "net.nekobako.twidere.extension.aniuta.MusicPlayerNotificationListenerService.ACTION";
	public static final String NOTIFICATION_PACKAGE_NAME = "jp.co.aniuta.android.aniutaap";
	public static final int NOTIFICATION_ID = 1;
	public static final String NOTIFICATION_TITLE = "android.title";
	public static final String NOTIFICATION_ARTIST = "android.text";

	private Receiver receiver;


	@Override
	public void onCreate() {
		super.onCreate();

		this.receiver = new Receiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION);
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
			for(StatusBarNotification statusBarNotification : ANiUTaNotificationListenerService.this.getActiveNotifications()) {
				if(NOTIFICATION_PACKAGE_NAME.equals(statusBarNotification.getPackageName()) && statusBarNotification.getId() == NOTIFICATION_ID) {
					Notification notification = statusBarNotification.getNotification();
					Bundle resultExtras = new Bundle();
					resultExtras.putString("title", (String)notification.extras.get(NOTIFICATION_TITLE));
					resultExtras.putString("artist", (String)notification.extras.get(NOTIFICATION_ARTIST));
					resultExtras.putParcelable("thumbnail", notification.largeIcon);
					this.setResultExtras(resultExtras);
					return;
				}
			}
		}
	}
}
