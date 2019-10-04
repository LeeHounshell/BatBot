package com.harlie.batbot.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import org.greenrobot.eventbus.EventBus


class BluetoothCaptureImageEvent(val name: String, val size: Int) {
    val TAG = "LEE: <" + BluetoothCaptureImageEvent::class.java.getName() + ">"

    var bitMap: Bitmap? = null

    fun parseImage(buffer: ByteArray, bytesRead: Int): Boolean {
        Log.d(TAG, "parseImage: bytesRead=" + bytesRead)
        try {
            bitMap = BitmapFactory.decodeByteArray(buffer, 0, bytesRead);
            return true;
        }
        catch (e: Exception) {
            Log.e(TAG, "parseImage: *** FAILED to decodeByteArray e=" + e)
            return false;
        }
    }

    fun post() {
        Log.d(TAG, "post: image name=" + name + ", size=" + size)
        EventBus.getDefault().post(this)
    }
}