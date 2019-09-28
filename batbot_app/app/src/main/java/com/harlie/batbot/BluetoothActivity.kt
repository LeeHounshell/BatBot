package com.harlie.batbot

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.harlie.batbot.ui.bluetooth.BluetoothFragment
import com.harlie.batbot.ui.bluetooth.Bluetooth_ViewModel


class BluetoothActivity : AppCompatActivity() {
    val TAG = "LEE: <" + BluetoothActivity::class.java.getName() + ">";

    val REQUEST_ENABLE_BLUETOOTH = 1

    private var m_BluetoothViewModel: Bluetooth_ViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bluetooth_activity)

        m_BluetoothViewModel = ViewModelProviders.of(this).get(Bluetooth_ViewModel::class.java)
        m_BluetoothViewModel!!.initDefaultAdapter()

        if (0 <= m_BluetoothViewModel!!.initializeDeviceList(this)) {
            Log.d(TAG, "*** ENABLING BLUETOOTH ***")
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (m_BluetoothViewModel?.m_BluetoothAdapter!!.isEnabled) {
                    Log.i(TAG, "onActivityResult: Bluetooth Enabled")

                    //--------------------------------------------------
                    Log.i(TAG, "--> load the BluetoothFragment <--")
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, BluetoothFragment())
                        .commitNow()
                    //--------------------------------------------------

                } else {
                    Log.i(TAG, "onActivityResult: Bluetooth is Disabled")
                    Toast.makeText(this@BluetoothActivity, "Bluetooth is Disabled", Toast.LENGTH_LONG).show()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "onActivityResult: Bluetooth Canceled")
                Toast.makeText(this@BluetoothActivity, "BlueTooth Canceled", Toast.LENGTH_LONG).show()
            } else {
                Log.w(TAG, "onActivityResult: Bluetooth resultCode=" + resultCode)
                Toast.makeText(this@BluetoothActivity, "BlueTooth Problem", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed")
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
        m_BluetoothViewModel = null
    }

}
