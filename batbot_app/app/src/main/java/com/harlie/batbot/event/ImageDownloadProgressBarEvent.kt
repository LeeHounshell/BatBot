// Copyright (c) 2019, Lee Hounshell. All rights reserved.

package com.harlie.batbot.event

import android.util.Log
import org.greenrobot.eventbus.EventBus


class ImageDownloadProgressBarEvent(val progress: Int, val max: Int) {
    private val TAG = "LEE: <" + ImageDownloadProgressBarEvent::class.java!!.getSimpleName() + ">"

    fun post() {
        Log.d(TAG, "post: progress=" + progress + ", max=" + max)
        EventBus.getDefault().post(this)
    }
}
