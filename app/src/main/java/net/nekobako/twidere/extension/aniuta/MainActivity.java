package net.nekobako.twidere.extension.aniuta;

import android.app.Activity;
import android.content.*;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import java.io.*;
import java.security.MessageDigest;
import org.mariotaku.twidere.constant.IntentConstants;


public class MainActivity extends AppCompatActivity {
	public static final int NOTIFICATION_LISTENER_SETTINGS_REQUEST_CODE = 1;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_aniuta);

		if(!this.notificationListenerIsEnabled()) {
			Toast.makeText(this, String.format("Enable\n\"%s\"\nthen go back to Twidere", this.getResources().getString(R.string.app_name)), Toast.LENGTH_LONG).show();
			Intent intent = new Intent();
			intent.setAction(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
			this.startActivityForResult(intent, NOTIFICATION_LISTENER_SETTINGS_REQUEST_CODE);
		}
		else {
			this.composeTweet();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == NOTIFICATION_LISTENER_SETTINGS_REQUEST_CODE) {
			this.composeTweet();
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

	private void composeTweet() {
		this.sendOrderedBroadcast(new Intent(ANiUTaNotificationListenerService.ACTION), null, new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Bundle receivedExtras = this.getResultExtras(false);
				if(receivedExtras != null) {
					Intent resultIntent = new Intent();
					Bundle resultExtras = new Bundle();
					resultExtras.putString(IntentConstants.EXTRA_APPEND_TEXT, String.format("#nowplaying %s by %s", receivedExtras.getString("title"), receivedExtras.getString("artist")));
					resultExtras.putParcelable(IntentConstants.EXTRA_IMAGE_URI, MainActivity.this.updateThumbnail((Bitmap)receivedExtras.getParcelable("thumbnail")));
					resultIntent.putExtras(resultExtras);
					MainActivity.this.setResult(Activity.RESULT_OK, resultIntent);
				}

				MainActivity.this.finish();
			}
		}, null, 0, null, null);
	}

	private Uri updateThumbnail(Bitmap bitmap) {
		File dir = new File(this.getCacheDir(), "thumbnails");
		if(!dir.exists()) {
			dir.mkdirs();
		}

		for(File file : dir.listFiles()) {
			file.delete();
		}

		File file = null;

		try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(byteArrayOutputStream.toByteArray());
			StringBuilder stringBuilder = new StringBuilder();
			for(byte d : messageDigest.digest()) {
				stringBuilder.append(String.format("%02x", d));
			}

			file = new File(dir, stringBuilder.toString() + ".png");
			try(FileOutputStream fileOutputStream = new FileOutputStream(file)) {
				byteArrayOutputStream.writeTo(fileOutputStream);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", file);
		this.grantUriPermission(this.getCallingPackage(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

		return uri;
	}
}
