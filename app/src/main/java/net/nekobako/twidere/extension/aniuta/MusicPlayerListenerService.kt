package net.nekobako.twidere.extension.aniuta

import android.content.*
import android.os.Bundle
import android.service.notification.NotificationListenerService


class MusicPlayerListenerService : NotificationListenerService() {
	companion object {
		val ACTION_FETCH_MUSIC_METADATA = "${MusicPlayerListenerService::class.java.name}.FETCH_MUSIC_METADATA"
		private val NOTIFICATION_PACKAGE_NAME = "jp.co.aniuta.android.aniutaap"
		private val NOTIFICATION_ID = 1
		private val NOTIFICATION_TITLE = "android.title"
		private val NOTIFICATION_ARTIST = "android.text"
	}


	private lateinit var receiver: Receiver


	override fun onCreate() {
		super.onCreate()

		this.receiver = Receiver()
		val intentFilter = IntentFilter()
		intentFilter.addAction(ACTION_FETCH_MUSIC_METADATA)
		this.registerReceiver(this.receiver, intentFilter)
	}

	override fun onDestroy() {
		super.onDestroy()

		this.unregisterReceiver(this.receiver)
	}


	private inner class Receiver : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent?) {
			this@MusicPlayerListenerService.activeNotifications
				.singleOrNull { it.packageName == NOTIFICATION_PACKAGE_NAME && it.id == NOTIFICATION_ID }
				?.notification
				?.let { notification ->
					val extras = Bundle()
					extras.putParcelable("music_metadata", MusicMetadata(notification.extras.getString(NOTIFICATION_TITLE), notification.extras.getString(NOTIFICATION_ARTIST), notification.largeIcon))
					this.setResultExtras(extras)
				}
		}
	}
}
