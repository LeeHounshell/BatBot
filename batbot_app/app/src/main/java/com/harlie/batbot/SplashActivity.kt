package com.harlie.batbot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class SplashActivity : AppCompatActivity() {
    val TAG = "LEE: <" + SplashActivity::class.java.getName() + ">";

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        val blueIntent: Intent = Intent(this, BluetoothActivity::class.java)
        startActivity(blueIntent)
        finish();
    }
}
