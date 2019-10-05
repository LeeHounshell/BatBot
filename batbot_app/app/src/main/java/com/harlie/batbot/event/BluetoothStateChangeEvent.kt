// Copyright (c) 2019, Lee Hounshell. All rights reserved.

package com.harlie.batbot.event

import android.os.Bundle
import org.greenrobot.eventbus.EventBus


class BluetoothStateChangeEvent(val whatChanged: Int, val theState: Int, val extra: Bundle) {
    private val TAG = "LEE: <" + BluetoothStateChangeEvent::class.java!!.getSimpleName() + ">"

    fun post() {
        //Log.d(TAG, "post: whatChanged=" + whatChanged + ", theState=" + theState)
        EventBus.getDefault().post(this)
    }
}
