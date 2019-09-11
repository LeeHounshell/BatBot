package com.harlie.batbot.ui.control

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.harlie.batbot.model.RobotCommandModel

class Control_ViewModel : ViewModel() {
    val TAG = "LEE: <" + Control_ViewModel::class.java.getName() + ">";

    val m_inputCommand = MutableLiveData<RobotCommandModel>()
}
