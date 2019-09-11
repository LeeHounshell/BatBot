package com.harlie.batbot.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.harlie.batbot.BR
import kotlin.properties.Delegates

class BluetoothDeviceModel(_name: String, _type: String, _uuids: String) : BaseObservable() {
    @get:Bindable var bt_name: String by Delegates.observable(_name)
    { prop, old, new ->
        notifyPropertyChanged(BR.btDeviceModel)
    }
    @get:Bindable var bt_type: String by Delegates.observable(_type)
    { prop, old, new ->
        notifyPropertyChanged(BR.btDeviceModel)
    }
    @get:Bindable var bt_uuids: String by Delegates.observable(_uuids)
    { prop, old, new ->
        notifyPropertyChanged(BR.btDeviceModel)
    }
}
