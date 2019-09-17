package com.harlie.batbot.ui.control

import android.bluetooth.BluetoothAdapter
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.harlie.batbot.model.RobotCommandModel

class Control_ViewModel : ViewModel() {
    val TAG = "LEE: <" + Control_ViewModel::class.java.getName() + ">";

    val m_inputCommand = MutableLiveData<RobotCommandModel>()

    lateinit var m_bluetoothAdapter: BluetoothAdapter

    fun initDefaultAdapter(): BluetoothAdapter {
        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return m_bluetoothAdapter
    }

    fun processMessage(robotCommand: RobotCommandModel) {
        Log.d(TAG, "processMessage: " + robotCommand.robotCommand + ", priority=" + robotCommand.commandPriority)
        // FIXME: analyze the command
        m_inputCommand.postValue(robotCommand)
    }
}
