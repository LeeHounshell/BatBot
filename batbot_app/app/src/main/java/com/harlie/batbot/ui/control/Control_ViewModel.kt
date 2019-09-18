package com.harlie.batbot.ui.control

import android.bluetooth.BluetoothAdapter
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.harlie.batbot.model.RobotCommandModel


class Control_ViewModel : ViewModel() {
    val TAG = "LEE: <" + Control_ViewModel::class.java.getName() + ">";

    val m_inputCommand = MutableLiveData<RobotCommandModel>()
    val m_starClicked = MutableLiveData<Boolean>()
    val m_okClicked = MutableLiveData<Boolean>()
    val m_sharpClicked = MutableLiveData<Boolean>()

    lateinit var m_bluetoothAdapter: BluetoothAdapter

    fun initDefaultAdapter(): BluetoothAdapter {
        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return m_bluetoothAdapter
    }

    fun processAndDecodeMessage(robotCommand: RobotCommandModel) {
        Log.d(TAG, "processAndDecodeMessage: " + robotCommand.robotCommand + ", priority=" + robotCommand.commandPriority)
        // FIXME: analyze the command
        robotCommand.robotCommand = robotCommand.robotCommand + "\n"
        m_inputCommand.postValue(robotCommand)
    }

    fun doClickStar() {
        Log.d(TAG, "doClickStar")
        m_starClicked.postValue(true)
    }

    fun doClickOk() {
        Log.d(TAG, "doClickOk")
        m_starClicked.postValue(true)
    }

    fun doClickSharp() {
        Log.d(TAG, "doClickSharp")
        m_starClicked.postValue(true)
    }
}
