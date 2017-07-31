package com.jalaraye.mytvapplication.utils

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import android.os.Build

@SuppressLint("ObsoleteSdkInt")
        /**
 * Created by usrmac-0238 on 31/07/2017.
 */

val videoUrl = "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Zeitgeist/Zeitgeist%202010_%20Year%20in%20Review.mp4"

@SuppressLint("ObsoleteSdkInt")
fun getDuration(videoUrl: String) : Long {
    val mmr = MediaMetadataRetriever()
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
        mmr.setDataSource(videoUrl, HashMap<String, String>())
    } else {
        mmr.setDataSource(videoUrl)
    }
    return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()
}