package com.harlie.batbot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.concurrent.schedule


class SplashActivity : AppCompatActivity() {
    val TAG = "LEE: <" + SplashActivity::class.java.getName() + ">";

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        Timer().schedule(1000) {
            Log.d(TAG, "-Splash-")
            val blueIntent: Intent = Intent(this@SplashActivity, BluetoothActivity::class.java)
            startActivity(blueIntent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish()
        }
    }
}
