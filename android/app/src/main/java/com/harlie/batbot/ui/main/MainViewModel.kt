package com.harlie.batbot.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.harlie.batbot.model.RobotCommandModel

class MainViewModel : ViewModel() {
    val TAG = "LEE: <" + MainViewModel::class.java.getName() + ">";

    val inputCommand = MutableLiveData<RobotCommandModel>()
}
