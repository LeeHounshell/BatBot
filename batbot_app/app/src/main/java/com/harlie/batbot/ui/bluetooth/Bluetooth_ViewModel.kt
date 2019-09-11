package com.harlie.batbot.ui.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.harlie.batbot.model.BluetoothDeviceModel
import com.harlie.batbot.util.CacheManager


class Bluetooth_ViewModel : ViewModel() {
    val TAG = "LEE: <" + Bluetooth_ViewModel::class.java.getName() + ">";

    val m_bluetoothDevicesList = MutableLiveData<MutableList<BluetoothDeviceModel>>()
    private val m_cacheManager: CacheManager = CacheManager.getInstance()

    lateinit var m_bluetoothAdapter: BluetoothAdapter
    private lateinit var m_pairedDevices: Set<BluetoothDevice>

    private val _selectedId = MutableLiveData<Int>()
    val selectedId: LiveData<Int> = _selectedId

    init {
        _selectedId.value = 0
    }

    fun toggle(selectedId: Int) {
        Log.d(TAG, "toggle(selectedId=" + selectedId)
        _selectedId.value = selectedId
    }


    fun initializeDeviceList(context: Context): Int {
        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (! m_bluetoothAdapter.isEnabled) {
            Log.i(TAG, "need to enable BlueTooth..")
            return 0
        }
        Log.i(TAG, "scanning BlueTooth devices..")
        pairedDeviceList(context)
        return 1
    }

    private fun pairedDeviceList(context: Context) {
        Log.d(TAG, "pairedDeviceList")
        var btDevicesList: ArrayList<BluetoothDeviceModel> = ArrayList()
        m_pairedDevices = m_bluetoothAdapter.bondedDevices
        if (! m_pairedDevices.isEmpty()) {
            var count = 0
            for (device: BluetoothDevice in m_pairedDevices) {
                ++count
                var btDeviceModel: BluetoothDeviceModel = BluetoothDeviceModel(device.name)
                btDevicesList.add(btDeviceModel)
                Log.i(TAG, "device" + count + "=" + btDeviceModel)
            }
            m_bluetoothDevicesList.setValue(btDevicesList)
        } else {
            Toast.makeText(context,  "no paired bluetooth devices found", Toast.LENGTH_LONG).show()
        }
    }
}
