package net.nekobako.twidere.extension.aniuta

import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.support.v4.content.FileProvider
import java.io.*
import java.security.MessageDigest


class MusicMetadata(
	val title: String,
	val artist: String,
	val artwork: Bitmap
) : Parcelable {
	companion object {
		@JvmField
		val CREATOR: Parcelable.Creator<MusicMetadata> = object : Parcelable.Creator<MusicMetadata> {
			override fun createFromParcel(parcel: Parcel?): MusicMetadata {
				return MusicMetadata(parcel)
			}

			override fun newArray(size: Int): Array<MusicMetadata?> {
				return arrayOfNulls(size)
			}
		}
	}


	constructor(parcel: Parcel?) : this(
		parcel?.readString() ?: "",
		parcel?.readString() ?: "",
		parcel?.readParcelable<Bitmap>(Bitmap::class.java.classLoader) ?: Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888))

	fun getArtworkUri(context: Context): Uri? {
		val directory = File(context.cacheDir, "artworks")
		if (directory.exists()) {
			directory.listFiles()
				.forEach { it.delete() }
		}
		else {
			directory.mkdirs()
		}

		ByteArrayOutputStream().use { byteArrayOutputStream ->
			this.artwork.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

			val messageDigest = MessageDigest.getInstance("MD5")
			messageDigest.update(byteArrayOutputStream.toByteArray())
			val md5 = messageDigest.digest()
				.map { String.format("%02x", it) }
				.joinToString("")

			val file = File(directory, "$md5.png")
			FileOutputStream(file).use { byteArrayOutputStream.writeTo(it) }

			return FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.fileprovider", file)
		}
	}

	override fun writeToParcel(parcel: Parcel?, flags: Int) {
		parcel?.writeString(this.title)
		parcel?.writeString(this.artist)
		parcel?.writeParcelable(this.artwork, flags)
	}

	override fun describeContents(): Int {
		return 0
	}
}
