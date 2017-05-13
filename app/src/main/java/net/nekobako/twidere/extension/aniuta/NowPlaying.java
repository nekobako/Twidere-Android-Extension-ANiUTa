package net.nekobako.twidere.extension.aniuta;

import android.content.*;
import android.os.Bundle;
import org.mariotaku.twidere.constant.IntentConstants;


public class NowPlaying {
	private Listener listener;


	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public void fetchMusicMetadata(Context context) {
		context.sendOrderedBroadcast(new Intent(MusicPlayerListenerService.ACTION_FETCH_MUSIC_METADATA), null, new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if(NowPlaying.this.listener != null) {
					Bundle extras = this.getResultExtras(false);
					NowPlaying.this.listener.onMusicMetadataFetched(extras != null ? (MusicMetadata)extras.getParcelable("music_metadata") : null);
				}
			}
		}, null, 0, null, null);
	}

	public Intent createTweetIntent(Context context, MusicMetadata musicMetadata) {
		Intent intent = new Intent();
		Bundle extras = new Bundle();
		extras.putString(IntentConstants.EXTRA_APPEND_TEXT, context.getResources().getString(R.string.tweet, musicMetadata.getTitle(), musicMetadata.getArtist()));
		extras.putParcelable(IntentConstants.EXTRA_IMAGE_URI, musicMetadata.getArtworkUri(context));
		intent.putExtras(extras);
		return intent;
	}


	public interface Listener {
		void onMusicMetadataFetched(MusicMetadata musicMetadata);
	}
}
