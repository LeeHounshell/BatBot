package com.harlie.batbot

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.harlie.batbot.model.RobotCommandModel
import com.harlie.batbot.ui.main.MainFragment
import com.harlie.batbot.ui.main.MainViewModel
import com.harlie.batbot.util.CacheManager


class MainActivity : AppCompatActivity() {
    val TAG = "LEE: <" + MainActivity::class.java.getName() + ">";

    private val REQUEST_CODE = 100
    private lateinit var mainViewModel : MainViewModel;
    private val cacheManager: CacheManager = CacheManager.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        this.let {
            mainViewModel = ViewModelProviders.of(it).get(MainViewModel::class.java)
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.getInstance())
                .commitNow()
        }
    }

    fun onClick(v: View) {
        Log.d(TAG, "onClick")
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        try {
            startActivityForResult(intent, REQUEST_CODE)
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "problem requesting translation: " + e);
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult")
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    Log.d(TAG, "result=" + result[0])
                    val robotCommand : RobotCommandModel = RobotCommandModel(result[0], "3")
                    mainViewModel.inputCommand.postValue(robotCommand)
                }
            }
        }
    }
}
