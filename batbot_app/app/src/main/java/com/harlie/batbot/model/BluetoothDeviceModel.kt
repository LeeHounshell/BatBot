// Copyright (c) 2019, Lee Hounshell. All rights reserved.

package com.harlie.batbot.model

import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.harlie.batbot.BR
import com.harlie.batbot.databinding.BtItemListViewBinding
import kotlin.properties.Delegates


class BluetoothDeviceModel(_name: String, device: BluetoothDevice) : BaseObservable() {
    val TAG = "LEE: <" + BluetoothDeviceModel::class.java.getName() + ">"

    val device: BluetoothDevice
    lateinit var binding: BtItemListViewBinding

    init {
        this.device = device
    }

    fun selectDevice(selected: Boolean) {
        Log.d(TAG, "selectDevice: name=" + bt_name + ", selected=" + selected)
        binding.selected = selected
        notifyPropertyChanged(BR.selected)
    }

    @get:Bindable
    var bt_name: String by Delegates.observable(_name)
    { prop, old, new ->
        notifyPropertyChanged(BR.btDeviceModel)
    }

    override fun toString(): String {
        return "BluetoothDeviceModel(name="+bt_name+", device=$device)"
    }
}
