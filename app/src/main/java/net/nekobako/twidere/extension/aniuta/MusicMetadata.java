package net.nekobako.twidere.extension.aniuta;

import android.app.Activity;
import android.content.*;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.*;
import android.support.v4.content.FileProvider;
import java.io.*;
import java.security.MessageDigest;


public class MusicMetadata implements Parcelable {
	public static final Parcelable.Creator<MusicMetadata> CREATOR = new Parcelable.Creator<MusicMetadata>() {
		public MusicMetadata createFromParcel(Parcel in) {
			return new MusicMetadata(in);
		}

		public MusicMetadata[] newArray(int size) {
			return new MusicMetadata[size];
		}
	};

	private String title;
	private String artist;
	private Bitmap artwork;


	public MusicMetadata(String title, String artist, Bitmap artwork) {
		this.title = title;
		this.artist = artist;
		this.artwork = artwork;
	}

	public MusicMetadata(Parcel in) {
		this.title = in.readString();
		this.artist = in.readString();
		this.artwork = in.readParcelable(Bitmap.class.getClassLoader());
	}

	public String getTitle() {
		return this.title;
	}

	public String getArtist() {
		return this.artist;
	}

	public Bitmap getArtwork() {
		return this.artwork;
	}

	public Uri getArtworkUri(Context context) {
		File directory = new File(context.getCacheDir(), "artworks");
		if(!directory.exists()) {
			directory.mkdirs();
		}

		for(File file : directory.listFiles()) {
			file.delete();
		}

		File file = null;
		try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			this.getArtwork().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(byteArrayOutputStream.toByteArray());
			StringBuilder stringBuilder = new StringBuilder();
			for(byte data : messageDigest.digest()) {
				stringBuilder.append(String.format("%02x", data));
			}

			file = new File(directory, stringBuilder.toString() + ".png");
			try(FileOutputStream fileOutputStream = new FileOutputStream(file)) {
				byteArrayOutputStream.writeTo(fileOutputStream);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
		context.grantUriPermission(((Activity)context).getCallingPackage(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
		return uri;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(this.getTitle());
		out.writeString(this.getArtist());
		out.writeParcelable(this.getArtwork(), flags);
	}
}
