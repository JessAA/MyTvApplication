package com.jalaraye.mytvapplication

import android.app.Activity
import android.os.Build
import android.os.Bundle
import com.jalaraye.mytvapplication.utils.getDuration
import com.jalaraye.mytvapplication.utils.videoUrl
import kotlinx.android.synthetic.main.activity_playback_overlay.*
import timber.log.Timber


class PlaybackOverlayActivity : Activity() {

    var mPlaybackState = LeanbackPlaybackState.IDLE
    var mPosition = 0
    var mStartTimeMillis: Long = 0
    var mDuration: Long = -1

    /*
     * List of various states that we can be in
     */
    enum class LeanbackPlaybackState {
        PLAYING, PAUSED, IDLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback_overlay)

        loadViews()
        setVideoPath(videoUrl)

        //initialise Timber
        Timber.plant(Timber.DebugTree())
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlayback()
        videoView.suspend()
        videoView.setVideoURI(null)
    }

    fun loadViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            videoView.isFocusable = false
            videoView.isFocusableInTouchMode = false
        }
    }

    fun setVideoPath(videoUrl: String) {
        setPosition(0)
        videoView.setVideoPath(videoUrl)
        mStartTimeMillis = 0
        mDuration = getDuration(videoUrl)
    }

    fun stopPlayback() {
        if (videoView != null) {
            videoView.stopPlayback()
        }
    }

    fun setPosition(position: Int) {
        if (position > mDuration) {
            mPosition = mDuration.toInt()
        } else if (position < 0) {
            mPosition = 0
            mStartTimeMillis = System.currentTimeMillis()
        } else {
            mPosition = position
        }

        mStartTimeMillis = System.currentTimeMillis()
        Timber.d("position set to " + mPosition)
    }

    fun playPause(doPlay: Boolean) {
        if (mPlaybackState == LeanbackPlaybackState.IDLE) {
            /* Callbacks for mVideoView */
            setupCallbacks()
        }

        if (doPlay && mPlaybackState != LeanbackPlaybackState.PLAYING) {
            mPlaybackState = LeanbackPlaybackState.PLAYING
            if (mPosition > 0) {
                videoView.seekTo(mPosition)
            }
            videoView.start()
            mStartTimeMillis = System.currentTimeMillis()
        } else {
            mPlaybackState = LeanbackPlaybackState.PAUSED
            var timeElapsedSinceStart: Int = (System.currentTimeMillis() - mStartTimeMillis).toInt()
            setPosition(mPosition + timeElapsedSinceStart)
            videoView.pause()
        }
    }

    fun fastForward(){
        if (mDuration > -1) {
            //Fast forward 10 seconds
            setPosition(videoView.currentPosition + (10 * 1000))
            videoView.seekTo(mPosition)
        }
    }

    fun rewind(){
        //rewind 10 seconds
        setPosition(videoView.currentPosition - (10 * 1000))
        videoView.seekTo(mPosition)
    }

    fun setupCallbacks(){
        videoView.setOnErrorListener({ mp, what, extra ->
            videoView.stopPlayback()
            mPlaybackState = LeanbackPlaybackState.IDLE
            false
        })

        videoView.setOnPreparedListener({
            if (mPlaybackState === LeanbackPlaybackState.PLAYING) {
                videoView.start()
            }
        })

        videoView.setOnCompletionListener({ mPlaybackState = LeanbackPlaybackState.IDLE })
    }

    fun getPosition(): Int{
        return mPosition
    }

}
