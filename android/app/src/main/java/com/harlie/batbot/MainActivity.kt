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
import com.harlie.batbot.ui.main.MainFragment
import com.harlie.batbot.ui.main.MainViewModel
import com.harlie.batbot.util.CacheManager


class MainActivity : AppCompatActivity() {
    val TAG = "LEE: <" + MainActivity::class.java.getName() + ">";

    private val REQUEST_CODE = 100
    private lateinit var mainViewModel : MainViewModel;
    private val cacheManager: CacheManager = CacheManager.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
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
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        try {
            startActivityForResult(intent, REQUEST_CODE)
        } catch (a: ActivityNotFoundException) {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    Log.d(TAG, "result=" + result[0])
                    mainViewModel.inputCommand.postValue(result[0])
                }
            }
        }
    }
}
