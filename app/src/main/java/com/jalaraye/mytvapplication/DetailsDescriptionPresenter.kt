package com.jalaraye.mytvapplication

import android.annotation.SuppressLint
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter
import com.jalaraye.mytvapplication.model.Video

/**
 * Created by jessicaaraye on 28/07/17.
 */
class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(vh: ViewHolder?, item: Any?) {

        if(item is Video){
            val video : Video? = item
            vh?.title?.text = video?.title
        }
    }
}