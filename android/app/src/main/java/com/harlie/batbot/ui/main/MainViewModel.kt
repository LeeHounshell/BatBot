package com.harlie.batbot.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val TAG = "LEE: <" + MainViewModel::class.java.getName() + ">";

    val inputCommand = MutableLiveData<String>()
}
