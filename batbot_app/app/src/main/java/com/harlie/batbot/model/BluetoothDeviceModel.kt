package com.harlie.batbot.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.harlie.batbot.BR
import kotlin.properties.Delegates

class BluetoothDeviceModel(_name: String) : BaseObservable() {
    @get:Bindable var bt_name: String by Delegates.observable(_name)
    { prop, old, new ->
        notifyPropertyChanged(BR.btDeviceModel)
    }
}
