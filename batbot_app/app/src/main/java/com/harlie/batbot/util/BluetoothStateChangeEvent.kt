package com.harlie.batbot.util

import android.os.Bundle
import android.util.Log
import org.greenrobot.eventbus.EventBus


class BluetoothStateChangeEvent(val whatChanged: Int, val theState: Int, val extra: Bundle) {
    private val TAG = "LEE: <" + BluetoothStateChangeEvent::class.java!!.getSimpleName() + ">"

    fun post() {
        //Log.d(TAG, "post: whatChanged=" + whatChanged + ", theState=" + theState)
        EventBus.getDefault().post(this)
    }
}
