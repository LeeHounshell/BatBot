package com.harlie.batbot

import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.harlie.batbot.model.RobotCommandModel
import com.harlie.batbot.ui.control.ControlFragment
import com.harlie.batbot.ui.control.Control_ViewModel
import java.io.IOException
import java.util.*

class ControlActivity : AppCompatActivity() {
    val TAG = "LEE: <" + ControlActivity::class.java.getName() + ">";

    companion object {
        var             m_myUUID: UUID = UUID.fromString("01010101-0A0B-0C0D-9310-0123456789AB")
        var             m_bluetoothSocket: BluetoothSocket? = null
        lateinit var    m_progress: ProgressDialog
        lateinit var    m_bluetoothAdapter: BluetoothAdapter
        var             m_isConnected: Boolean = false
        lateinit var    m_name: String
        lateinit var    m_address: String
        val             EXTRA_NAME   : String = "Device_name"
        val             EXTRA_ADDRESS: String = "Device_address"
    }

    private val REQUEST_CODE = 100

    private lateinit var m_controlViewModel : Control_ViewModel;


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_activity)
        this.let {
            m_controlViewModel = ViewModelProviders.of(it).get(Control_ViewModel::class.java)
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ControlFragment.getInstance())
                .commitNow()
        }

        m_name = intent.getStringExtra(ControlActivity.EXTRA_NAME)
        m_address = intent.getStringExtra(ControlActivity.EXTRA_ADDRESS)
        Log.d(TAG, "selected device name=" + m_name + ", address=" + m_address)

        Connection(this).execute()
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
                    m_controlViewModel.m_inputCommand.postValue(robotCommand)
                }
            }
        }
    }


    private fun send(input: String) {
        if (m_bluetoothSocket != null) {
            try{
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch(e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun disconnect() {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        finish()
    }

    private class Connection(c: Context) : AsyncTask<Void, Void, String>() {
        val TAG = "LEE: <" + Connection::class.java.getName() + ">";

        private val context: Context
        private var success: Boolean = true

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Connecting...", "please wait")
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (m_bluetoothSocket == null || !m_isConnected) {
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()
                }
            } catch (e: IOException) {
                success = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            Log.d(TAG, "onPostExecute")
            super.onPostExecute(result)
            m_isConnected = success
            Log.i(TAG, "connection status=" + m_isConnected);
            m_progress.dismiss()
        }
    }
}
