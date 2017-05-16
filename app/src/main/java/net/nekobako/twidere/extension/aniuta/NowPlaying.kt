package net.nekobako.twidere.extension.aniuta

import android.content.*
import android.os.Bundle
import org.mariotaku.twidere.constant.IntentConstants


class NowPlaying {
	companion object {
		private val TWIDERE_PACKAGE_NAME = "org.mariotaku.twidere"
	}


	fun fetchMusicMetadata(context: Context, callback: (MusicMetadata?) -> Unit) {
		context.sendOrderedBroadcast(Intent(MusicPlayerListenerService.ACTION_FETCH_MUSIC_METADATA), null, object : BroadcastReceiver() {
			override fun onReceive(context: Context, intent: Intent?) {
				callback.invoke(this.getResultExtras(false)?.getParcelable("music_metadata"))
			}
		}, null, 0, null, null)
	}

	fun createTweetIntent(context: Context, musicMetadata: MusicMetadata): Intent {
		val intent = Intent()
		val extras = Bundle()
		val uri = musicMetadata.getArtworkUri(context)
		extras.putString(IntentConstants.EXTRA_APPEND_TEXT, context.resources.getString(R.string.tweet, musicMetadata.title, musicMetadata.artist))
		extras.putParcelable(IntentConstants.EXTRA_IMAGE_URI, uri)
		intent.putExtras(extras)
		context.grantUriPermission(TWIDERE_PACKAGE_NAME, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
		return intent
	}
}
