package com.harlie.batbot.event

import org.greenrobot.eventbus.EventBus


class BluetoothMessageEvent(val message: String) {
    private val TAG = "LEE: <" + BluetoothMessageEvent::class.java!!.getSimpleName() + ">"

    fun post() {
        //Log.d(TAG, "post: message=" + message)
        EventBus.getDefault().post(this)
    }
}
