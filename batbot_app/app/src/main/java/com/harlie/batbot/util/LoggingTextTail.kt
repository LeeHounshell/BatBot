package com.harlie.batbot.util

import android.util.Log


public class LoggingTextTail {
    val TAG = "LEE: <" + LoggingTextTail::class.java.getName() + ">";

    val MAX_LOG_LINES = 5
    val MAX_LOG_DISPLAY_CONTENT_CHARS = 200;

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
        val content = sb.toString()
        var index = content.length-MAX_LOG_DISPLAY_CONTENT_CHARS
        if (index < 0) {
            index = 0
        }
        return content.substring(index)
    }
}
