package net.nekobako.twidere.extension.aniuta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
	private static final int NOTIFICATION_LISTENER_SETTINGS_REQUEST_CODE = 1;

	private NowPlaying nowPlaying;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);

		this.nowPlaying = new NowPlaying();
		this.nowPlaying.setListener(new NowPlaying.Listener() {
			@Override
			public void onMusicMetadataFetched(MusicMetadata musicMetadata) {
				if(musicMetadata != null) {
					MainActivity.this.setResult(Activity.RESULT_OK, MainActivity.this.nowPlaying.createTweetIntent(MainActivity.this, musicMetadata));
				}
				MainActivity.this.finish();
			}
		});

		if(!this.notificationListenerIsEnabled()) {
			this.showNotificationListenerSettings();
		}
		else {
			this.nowPlaying.fetchMusicMetadata(this);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == NOTIFICATION_LISTENER_SETTINGS_REQUEST_CODE) {
			this.nowPlaying.fetchMusicMetadata(this);
		}
	}

	private boolean notificationListenerIsEnabled() {
		String rawListeners = Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners");
		if(rawListeners == null || "".equals(rawListeners)) {
			return false;
		}
		for(String listener : rawListeners.split(":")) {
			if(listener.startsWith(BuildConfig.APPLICATION_ID)) {
				return true;
			}
		}
		return false;
	}

	private void showNotificationListenerSettings() {
		Toast.makeText(this, this.getResources().getString(R.string.enable_notification_listener), Toast.LENGTH_LONG).show();
		Intent intent = new Intent();
		intent.setAction(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
		this.startActivityForResult(intent, NOTIFICATION_LISTENER_SETTINGS_REQUEST_CODE);
	}
}
