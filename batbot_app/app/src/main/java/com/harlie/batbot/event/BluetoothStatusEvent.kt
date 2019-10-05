// Copyright (c) 2019, Lee Hounshell. All rights reserved.

package com.harlie.batbot.event

import android.util.Log
import org.greenrobot.eventbus.EventBus


class BluetoothStatusEvent(val message: String) {
    private val TAG = "LEE: <" + BluetoothStatusEvent::class.java!!.getSimpleName() + ">"

    fun post() {
        Log.d(TAG, "post: message=" + message)
        EventBus.getDefault().post(this)
    }
}
