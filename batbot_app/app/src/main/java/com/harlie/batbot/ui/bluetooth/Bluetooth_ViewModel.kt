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


class Bluetooth_ViewModel : ViewModel() {
    val TAG = "LEE: <" + Bluetooth_ViewModel::class.java.getName() + ">";

    private val m_bluetoothDevicesList = MutableLiveData<MutableList<BluetoothDeviceModel>>()
    private val m_selectedDevice = MutableLiveData<BluetoothDeviceModel>()

    // encapsulate access to mutable LiveData through getter
    fun getBluetoothDevicesList(): LiveData<MutableList<BluetoothDeviceModel>> = m_bluetoothDevicesList
    fun getSelectedDevice(): LiveData<BluetoothDeviceModel> = m_selectedDevice

    private lateinit var m_BluetoothAdapter: BluetoothAdapter
    private lateinit var m_pairedDevices: Set<BluetoothDevice>


    fun getDevice(selectionId: Int): BluetoothDeviceModel {
        Log.d(TAG, "getDevice(" + selectionId + ")")
        return m_bluetoothDevicesList.value!!.get(selectionId)
    }

    fun selectDevice(btDeviceModel: BluetoothDeviceModel) {
        Log.d(TAG, "selectDevice(name=" + btDeviceModel.bt_name + ", address=" + btDeviceModel.device.address)
        for (device in m_bluetoothDevicesList.value!!) {
            if (device != btDeviceModel) {
                device.selectDevice(false)
            }
        }
        btDeviceModel.selectDevice(true)
        m_selectedDevice.value = btDeviceModel
    }

    fun initDefaultAdapter(): BluetoothAdapter {
        Log.d(TAG, "initialize")
        m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return m_BluetoothAdapter
    }

    fun isAdapterInitialized(): Boolean {
        val didInit = ::m_BluetoothAdapter.isInitialized
        Log.d(TAG, "isAdapterInitialied: " + didInit)
        return didInit
    }

    fun initializeDeviceList(context: Context): Int {
        Log.d(TAG, "initializeDeviceList")
        if (! isAdapterInitialized()) {
            Log.i(TAG, "BlueTooth adapter not initialized yet..")
            return 0
        }
        if (! m_BluetoothAdapter.isEnabled) {
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
        m_pairedDevices = m_BluetoothAdapter.bondedDevices
        if (! m_pairedDevices.isEmpty()) {
            var count = 0
            for (device: BluetoothDevice in m_pairedDevices) {
                ++count
                var btDeviceModel: BluetoothDeviceModel = BluetoothDeviceModel(device.name, device)
                btDevicesList.add(btDeviceModel)
                Log.i(TAG, "device" + count + "=" + btDeviceModel)
            }
            m_bluetoothDevicesList.setValue(btDevicesList)
        } else {
            Toast.makeText(context,  "no paired bluetooth devices found", Toast.LENGTH_LONG).show()
        }
    }

    fun cancelDiscovery() {
        Log.d(TAG, "cancelDiscovery")
        if (::m_BluetoothAdapter.isInitialized) {
            m_BluetoothAdapter.cancelDiscovery()
        }
    }

}
