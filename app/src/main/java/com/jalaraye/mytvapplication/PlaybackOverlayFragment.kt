package com.jalaraye.mytvapplication

import android.os.Bundle
import android.support.v17.leanback.app.PlaybackSupportFragment
import android.support.v17.leanback.widget.*
import android.support.v17.leanback.widget.ListRow
import com.jalaraye.mytvapplication.model.Video

/**
 * Created by jessicaaraye on 26/07/17.
 */
class PlaybackOverlayFragment : android.support.v17.leanback.app.PlaybackFragment() {

    var mPlaybackControlsRow: PlaybackControlsRow? = null
    lateinit var mPrimaryActionAdapter: ArrayObjectAdapter
    //lateinit var  mSecondaryActionAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backgroundType = PlaybackSupportFragment.BG_LIGHT
        isControlsOverlayAutoHideEnabled = true

        setUpRows()
    }

    fun setUpRows(){
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
        adapter = mRowsAdapter

    }

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

        val mPlayPauseAction = PlaybackControlsRow.PlayPauseAction(activity)
        val mSkipNextAction = PlaybackControlsRow.SkipNextAction(activity)
        val mSkipPreviousAction = PlaybackControlsRow.SkipPreviousAction(activity)
        val mFastForwardAction = PlaybackControlsRow.FastForwardAction(activity)
        val mRewindAction = PlaybackControlsRow.RewindAction(activity)

        /* PrimaryAction setting */
        mPrimaryActionAdapter.add(mSkipPreviousAction);
        mPrimaryActionAdapter.add(mRewindAction);
        mPrimaryActionAdapter.add(mPlayPauseAction);
        mPrimaryActionAdapter.add(mFastForwardAction);
        mPrimaryActionAdapter.add(mSkipNextAction);

    }
}