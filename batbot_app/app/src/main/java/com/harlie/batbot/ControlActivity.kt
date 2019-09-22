package com.harlie.batbot

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.harlie.batbot.model.RobotCommandModel
import com.harlie.batbot.ui.control.ControlFragment
import com.harlie.batbot.ui.control.Control_ViewModel
import java.util.*
import kotlin.concurrent.schedule


class ControlActivity : AppCompatActivity() {
    val TAG = "LEE: <" + ControlActivity::class.java.getName() + ">";

    companion object {
        lateinit var    m_name: String // name of the batbot machine to connect to
        lateinit var    m_address: String // address of the batbot machine to connect to
        lateinit var    m_device: BluetoothDevice // Bluetooth device info of the batbot machine
        val             EXTRA_NAME   : String = "Device_name"
        val             EXTRA_ADDRESS: String = "Device_address"
        val             EXTRA_DEVICE: String = "Device_data"
    }

    private val REQUEST_CODE = 100
    private val PREF_UNIQUE_ID = "PREF_UNIQUE_ID"

    private var m_uniqueId: String? = null
    private var m_robotCommand : RobotCommandModel? = null
    private lateinit var m_ControlViewModel : Control_ViewModel
    private lateinit var m_ControlFragment: ControlFragment


    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_activity)
        this.let {
            m_ControlViewModel = ViewModelProviders.of(it).get(Control_ViewModel::class.java)
        }
        m_ControlViewModel.initLiveData()

        m_name = intent.getStringExtra(ControlActivity.EXTRA_NAME)
        m_address = intent.getStringExtra(ControlActivity.EXTRA_ADDRESS)
        m_device = intent.getParcelableExtra(ControlActivity.EXTRA_DEVICE)
        Log.d(TAG, "selected name=" + m_name + ", address=" + m_address)

        m_uniqueId = id(this)
        Log.d(TAG, "phone has m_uniqueId=" + m_uniqueId)

        if (savedInstanceState == null) {
            m_ControlFragment = ControlFragment.getInstance() as ControlFragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, m_ControlFragment)
                .commitNow()

            Log.d(TAG, "creating Connection to batbot..")
            m_ControlFragment.setDeviceInfo(m_name, m_address, m_device, m_uniqueId!!)
        }
    }

    @Synchronized
    fun id(context: Context): String {
        if (m_uniqueId == null) {
            val sharedPrefs = context.getSharedPreferences(
                PREF_UNIQUE_ID, Context.MODE_PRIVATE
            )
            m_uniqueId = sharedPrefs.getString(PREF_UNIQUE_ID, null)
            if (m_uniqueId == null) {
                m_uniqueId = UUID.randomUUID().toString()
                val editor = sharedPrefs.edit()
                editor.putString(PREF_UNIQUE_ID, m_uniqueId)
                editor.commit()
            }
        }
        return m_uniqueId as String
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
    }

    fun onClickButtonStar(v: View) {
        Log.d(TAG, "onClickButtonStar")
        m_ControlViewModel.doClickStar()
    }

    fun onClickButtonOk(v: View?) {
        Log.d(TAG, "onClickButtonOk")
        m_ControlViewModel.doClickOk()
    }

    fun onClickButtonSharp(v: View) {
        Log.d(TAG, "onClickButtonSharp")
        m_ControlViewModel.doClickSharp()
    }

    fun onClickTranslateSpeech(v: View) {
        Log.d(TAG, "onClickTranslateSpeech")
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
                    Log.d(TAG, "===> got speech translation result=" + result[0])
                    m_robotCommand = RobotCommandModel(result[0], "3")
                    m_ControlViewModel.processAndDecodeMessage(m_robotCommand!!)
                    Timer().schedule(9000) {
                        Log.d(TAG, "One-Shot-Timer: -Check-The-Log-")
                        m_ControlFragment?.send("")
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed")
        super.onBackPressed()
        gotoBluetoothActivity()
        finish()
    }

    fun gotoBluetoothActivity() {
        Log.d(TAG, "gotoBluetoothActivity")
        val controlIntent: Intent = Intent(this, BluetoothActivity::class.java)
        controlIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(controlIntent)
        overridePendingTransition(0, R.anim.fade_out);
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }
}
