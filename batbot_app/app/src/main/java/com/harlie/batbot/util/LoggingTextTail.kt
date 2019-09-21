package com.harlie.batbot.util

import android.util.Log


public class LoggingTextTail {
    val TAG = "LEE: <" + LoggingTextTail::class.java.getName() + ">";

    val MAX_LOG_LINES = 9
    val MAX_LOG_DISPLAY_CONTENT_CHARS = 300;

    var content = mutableListOf<String>()

    fun append(log_text: String) {
        Log.d(TAG, "append: " + log_text)
        val lines = log_text.lines()
        lines.forEach {
            if (it.length > 0) {
                content.add(it + "\n")
            }
        }
    }

    // each time this is called, the first line gets deleted, until MAX_LOG_LINES remain
    fun content(): String {
        if (content.size > 0) {
            val sb = StringBuffer()
            for (i in 0..content.size - 1) {
                if (i >= MAX_LOG_LINES) {
                    break;
                }
                sb.append(content[i])
                if (sb.length >= MAX_LOG_DISPLAY_CONTENT_CHARS) {
                    break
                }
            }
            if (content.size > MAX_LOG_LINES) {
                content.removeAt(0)
            }
            return sb.toString()
        }
        return ""
    }

    fun clear() {
        Log.d(TAG, "clear")
        content = mutableListOf<String>()
    }
}
