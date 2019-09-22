package com.harlie.batbot.ui.control

import android.bluetooth.BluetoothAdapter
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.harlie.batbot.model.RobotCommandModel


class Control_ViewModel : ViewModel() {
    val TAG = "LEE: <" + Control_ViewModel::class.java.getName() + ">";

    private lateinit var m_inputCommand: MutableLiveData<RobotCommandModel>
    private lateinit var m_starClicked: MutableLiveData<Boolean>
    private lateinit var m_okClicked: MutableLiveData<Boolean>
    private lateinit var m_sharpClicked: MutableLiveData<Boolean>
    private lateinit var m_bluetoothAdapter: BluetoothAdapter


    fun initLiveData() {
        Log.d(TAG, "initLiveData")
        // if bluetooth fails these need to be reset for the next activity
        m_inputCommand = MutableLiveData<RobotCommandModel>()
        m_starClicked = MutableLiveData<Boolean>()
        m_okClicked = MutableLiveData<Boolean>()
        m_sharpClicked = MutableLiveData<Boolean>()
    }

    fun getInputCommand(): LiveData<RobotCommandModel> = m_inputCommand
    fun getStarClicked(): LiveData<Boolean> = m_starClicked
    fun getOkClicked(): LiveData<Boolean> = m_okClicked
    fun getSharpClicked(): LiveData<Boolean> = m_sharpClicked

    fun initDefaultAdapter(): BluetoothAdapter {
        Log.d(TAG, "initDefaultAdapter")
        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return m_bluetoothAdapter
    }

    fun processAndDecodeMessage(robotCommand: RobotCommandModel) {
        Log.d(TAG, "processAndDecodeMessage: " + robotCommand.robotCommand + ", priority=" + robotCommand.commandPriority)
        // FIXME: analyze the command
        m_inputCommand.postValue(robotCommand)
    }

    fun doClickStar() {
        Log.d(TAG, "doClickStar")
        m_starClicked.postValue(true)
    }

    fun doClickOk() {
        Log.d(TAG, "doClickOk")
        m_okClicked.postValue(true)
    }

    fun doClickSharp() {
        Log.d(TAG, "doClickSharp")
        m_sharpClicked.postValue(true)
    }
}
