package com.harlie.batbot.util

import android.util.Log


public class LoggingTextTail {
    val TAG = "LEE: <" + LoggingTextTail::class.java.getName() + ">";

    val MAX_LOG_LINES = 3
    var content = mutableListOf<String>()

    fun append(log_text: String) {
        Log.d(TAG, "append: " + log_text)
        content.add(log_text)
        if (content.size > MAX_LOG_LINES) {
            content.removeAt(0)
        }
    }

    fun content(): String {
        val sb = StringBuffer()
        for (i in 0..content.size-1) {
            sb.append(content[i])
        }
        Log.d(TAG, "content: " + sb.toString())
        return sb.toString()
    }
}
