package com.jalaraye.mytvapplication.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by jessicaaraye on 28/07/17.
 */

//TODO This must exist in the core

data class Video(var watId: String = "0",
                 var videoId: String = "0",
                 var title: String ="Title"): Parcelable {

    companion object{
        @JvmField val CREATOR : Parcelable.Creator<Video>  = object : Parcelable.Creator<Video>{
            override fun createFromParcel(source: Parcel): Video = Video(source)
            override fun newArray(size: Int): Array<Video?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString())

    override fun writeToParcel(dest: Parcel?, p1: Int) {
        dest?.writeString(watId)
        dest?.writeString(videoId)
        dest?.writeString(title)
    }

    override fun describeContents() = 0

}