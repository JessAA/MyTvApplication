package com.jalaraye.mytvapplication

import android.media.session.PlaybackState
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v17.leanback.app.PlaybackSupportFragment
import android.support.v17.leanback.widget.*
import android.support.v17.leanback.widget.ListRow
import com.jalaraye.mytvapplication.model.Video


/**
 * Created by jessicaaraye on 26/07/17.
 */
class PlaybackOverlayFragment : android.support.v17.leanback.app.PlaybackFragment(), android.support.v17.leanback.widget.OnActionClickedListener {


    var mPlaybackControlsRow: PlaybackControlsRow? = null
    lateinit var mPrimaryActionAdapter: ArrayObjectAdapter
    var mCurrentPlaybackState: Int = 0
    val SIMULATED_BUFFERED_TIME = 10000
    val DEFAULT_UPDATE_PERIOD = 1000
    val UPDATE_PERIOD = 16
    var mHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backgroundType = PlaybackSupportFragment.BG_LIGHT
        isControlsOverlayAutoHideEnabled = true

        mHandler = Handler(Looper.getMainLooper())

        setUpRows()
    }

    fun setUpRows() {
        val ps = ClassPresenterSelector()
        val playbackControlsRowPresenter: PlaybackControlsRowPresenter = PlaybackControlsRowPresenter(DetailsDescriptionPresenter())

        ps.addClassPresenter(PlaybackControlsRow::class.java, playbackControlsRowPresenter)
        ps.addClassPresenter(ListRow::class.java, ListRowPresenter())

        val mRowsAdapter = ArrayObjectAdapter(ps)

        /*
         * Add PlaybackControlsRow to mRowsAdapter, which makes video control UI.
         * PlaybackControlsRow is supposed to be first Row of mRowsAdapter.
         */
        addPlaybackControlsRow(mRowsAdapter)

        /*
       * on Click()
       * */
        playbackControlsRowPresenter.onActionClickedListener = this
        adapter = mRowsAdapter
    }

    override fun onActionClicked(action: Action?) {
        when (action?.id) {
            mPlayPauseAction.id -> {
                print("PlayPause action")/* PlayPause action */
                togglePlayback(mPlayPauseAction.index == PlaybackControlsRow.PlayPauseAction.INDEX_PLAY)
            }
            mFastForwardAction.id -> {
                print("FastForward action")
                fastforward()
            }
            mRewindAction.id -> {
                print("Rewind action")
                rewind()
            }
        }

        if (action is PlaybackControlsRow.MultiAction) {
            /* Following action is subclass of MultiAction
                     * - PlayPauseAction
                     * - FastForwardAction
                     * - RewindAction
                     * - ThumbsAction
                     * - RepeatAction
                     * - ShuffleAction
                     * - HighQualityAction
                     * - ClosedCaptioningAction
                     */
            notifyChanged(action)
        }
    }

    lateinit var mPlayPauseAction: PlaybackControlsRow.PlayPauseAction
    lateinit var mFastForwardAction: PlaybackControlsRow.FastForwardAction
    lateinit var mRewindAction: PlaybackControlsRow.RewindAction

    fun addPlaybackControlsRow(mRowsAdapter: ArrayObjectAdapter) {
        var v: Video = Video()
        v.title = "Title"
        v.videoId = "O"
        v.watId = "0"

        mPlaybackControlsRow = PlaybackControlsRow(v) //PlaybackControlsRow can take a video in parameter
        mRowsAdapter.add(mPlaybackControlsRow)


        val presenterSelector = ControlButtonPresenterSelector()
        mPrimaryActionAdapter = ArrayObjectAdapter(presenterSelector)
        mPlaybackControlsRow?.primaryActionsAdapter = mPrimaryActionAdapter

        val activity = activity

        mPlayPauseAction = PlaybackControlsRow.PlayPauseAction(activity)
        mFastForwardAction = PlaybackControlsRow.FastForwardAction(activity)
        mRewindAction = PlaybackControlsRow.RewindAction(activity)

        /* PrimaryAction setting */
        mPrimaryActionAdapter.add(mRewindAction)
        mPrimaryActionAdapter.add(mPlayPauseAction)
        mPrimaryActionAdapter.add(mFastForwardAction)

    }

    fun notifyChanged(action: Action) {
        val adapter = mPrimaryActionAdapter
        if (adapter.indexOf(action) >= 0) {
            adapter.notifyArrayItemRangeChanged(adapter.indexOf(action), 1)
            return
        }
    }

    fun togglePlayback(playPause: Boolean) {
        (activity as PlaybackOverlayActivity).playPause(playPause)
        /* UI control part */
        playbackStateChanged()
    }

    fun playbackStateChanged() {
        if (mCurrentPlaybackState != PlaybackState.STATE_PLAYING) {
            mCurrentPlaybackState = PlaybackState.STATE_PLAYING
            startProgressAutomation()
            isControlsOverlayAutoHideEnabled = true
            mPlayPauseAction.index = PlaybackControlsRow.PlayPauseAction.INDEX_PAUSE
            mPlayPauseAction.icon = mPlayPauseAction.getDrawable(PlaybackControlsRow.PlayPauseAction.INDEX_PAUSE)
            notifyChanged(mPlayPauseAction)
        } else if (mCurrentPlaybackState != PlaybackState.STATE_PAUSED) {
            mCurrentPlaybackState = PlaybackState.STATE_PAUSED
            stopProgressAutomation()
            mPlayPauseAction.index = PlaybackControlsRow.PlayPauseAction.INDEX_PLAY
            mPlayPauseAction.icon = mPlayPauseAction.getDrawable(PlaybackControlsRow.PlayPauseAction.INDEX_PLAY)
            notifyChanged(mPlayPauseAction)
        }

        var currentTime = (activity as PlaybackOverlayActivity).getPosition()
        mPlaybackControlsRow?.currentPosition = currentTime.toLong()
        mPlaybackControlsRow?.bufferedPosition = (currentTime + SIMULATED_BUFFERED_TIME).toLong()
    }

    fun fastforward() {
        /* Video control part */
        (activity as PlaybackOverlayActivity).fastForward()

        /* UI part */
        var currentTime: Int = (activity as PlaybackOverlayActivity).getPosition()
        mPlaybackControlsRow?.currentPosition = currentTime.toLong()
        mPlaybackControlsRow?.bufferedPosition = (currentTime + SIMULATED_BUFFERED_TIME).toLong()

    }

    fun rewind() {
        (activity as PlaybackOverlayActivity).rewind()
        var currentTime: Int = (activity as PlaybackOverlayActivity).getPosition()
        mPlaybackControlsRow?.currentPosition = currentTime.toLong()
        mPlaybackControlsRow?.bufferedPosition = (currentTime + SIMULATED_BUFFERED_TIME).toLong()
    }

    private var mRunnable: Runnable? = null

    fun startProgressAutomation() {
        if (mRunnable == null) {
            mRunnable = object : Runnable {
                override fun run() {
                    val updatePeriod = getUpdatePeriod()
                    val currentTime = mPlaybackControlsRow?.currentPosition?.plus(updatePeriod)
                    val totalTime = mPlaybackControlsRow?.duration
                    mPlaybackControlsRow?.currentPosition = currentTime as Long
                    mPlaybackControlsRow?.bufferedPosition = currentTime + SIMULATED_BUFFERED_TIME

                    if (totalTime != null) {
                        if (totalTime > 0 && totalTime <= currentTime) {
                            stopProgressAutomation()
                        } else {
                            mHandler?.postDelayed(this, updatePeriod.toLong())
                        }
                    }
                }
            }
            mHandler?.postDelayed(mRunnable, getUpdatePeriod().toLong())
        }
    }

    fun getUpdatePeriod(): Int {
        if(mPlaybackControlsRow != null){
            if (view == null || mPlaybackControlsRow.duration <= 0) return DEFAULT_UPDATE_PERIOD
        }

        return Math.max(UPDATE_PERIOD, (mPlaybackControlsRow?.duration?.div(view.width)) as Int)
    }


    fun stopProgressAutomation() {
        if (mHandler != null && mRunnable != null) {
            mHandler?.removeCallbacks(mRunnable)
            mRunnable = null
        }
    }
}