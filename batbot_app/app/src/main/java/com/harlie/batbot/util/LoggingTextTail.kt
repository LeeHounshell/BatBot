package com.harlie.batbot.util

import android.util.Log


public class LoggingTextTail {
    val TAG = "LEE: <" + LoggingTextTail::class.java.getName() + ">";

    val MAX_LOG_LINES = 10
    val MAX_LOG_DISPLAY_CONTENT_CHARS = 333;

    private var content = mutableListOf<String>()
    private var m_lastLogReceiptTime = System.currentTimeMillis()


    fun append(log_text: String) {
        Log.d(TAG, "append: " + log_text)
        val lines = log_text.lines()
        lines.forEach {
            if (it.trim().length > 0) {
                m_lastLogReceiptTime = System.currentTimeMillis()
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

    fun lastLogTime(): Long {
        Log.d(TAG, "lastLogTime: " + m_lastLogReceiptTime)
        return m_lastLogReceiptTime
    }

    fun clear() {
        Log.d(TAG, "clear")
        content = mutableListOf<String>()
    }
}
