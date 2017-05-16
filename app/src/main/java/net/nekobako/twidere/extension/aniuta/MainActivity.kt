package net.nekobako.twidere.extension.aniuta

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.widget.Toast


class MainActivity : AppCompatActivity() {
	companion object {
		private val NOTIFICATION_LISTENER_SETTINGS_REQUEST_CODE = 1
	}


	private lateinit var nowPlaying: NowPlaying


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		this.setContentView(R.layout.activity_main)

		this.nowPlaying = NowPlaying()

		if (savedInstanceState == null) {
			if (this.notificationListenerIsEnabled()) {
				this.nowPlaying.fetchMusicMetadata(this, this::onMusicMetadataFetched)
			}
			else {
				this.requestNotificationListenerSettings()
			}
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)

		if (requestCode == NOTIFICATION_LISTENER_SETTINGS_REQUEST_CODE && this.notificationListenerIsEnabled()) {
			this.nowPlaying.fetchMusicMetadata(this, this::onMusicMetadataFetched)
		}
		else {
			this.finish()
		}
	}

	private fun notificationListenerIsEnabled(): Boolean {
		return Settings.Secure.getString(this.contentResolver, "enabled_notification_listeners")?.split(":")?.any { it.startsWith(BuildConfig.APPLICATION_ID) } ?: false
	}

	private fun requestNotificationListenerSettings() {
		Toast.makeText(this, this.resources.getString(R.string.enable_notification_listener), Toast.LENGTH_LONG).show()
		val intent = Intent()
		intent.action = Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
		this.startActivityForResult(intent, NOTIFICATION_LISTENER_SETTINGS_REQUEST_CODE)
	}

	private fun onMusicMetadataFetched(musicMetadata: MusicMetadata?) {
		musicMetadata?.let { this.setResult(Activity.RESULT_OK, this.nowPlaying.createTweetIntent(this, it)) }
		this.finish()
	}
}
