package com.harlie.batbot.util

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.harlie.batbot.model.BluetoothDeviceModel
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.harlie.batbot.databinding.BtItemListViewBinding
import com.harlie.batbot.ui.control.Bluetooth_ViewModel


class BluetoothRecyclerAdapter(bluetoothViewModel: Bluetooth_ViewModel) : RecyclerView.Adapter<BluetoothRecyclerAdapter.BtDeviceViewHolder>() {
    val TAG = "LEE: <" + BluetoothRecyclerAdapter::class.java.getName() + ">";

    var m_deviceCache: List<BluetoothDeviceModel> = emptyList<BluetoothDeviceModel>() as List<BluetoothDeviceModel>


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BtDeviceViewHolder {
        Log.d(TAG, "onCreateViewHolder: viewType=" + viewType)
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding = DataBindingUtil.inflate<BtItemListViewBinding>(
            layoutInflater, com.harlie.batbot.R.layout.bt_item_list_view, parent, false
        )
        return BtDeviceViewHolder(itemBinding as BtItemListViewBinding)
    }

    override fun onBindViewHolder(viewHolder: BtDeviceViewHolder, position: Int) = viewHolder.bind(m_deviceCache[position])

    override fun getItemCount(): Int = m_deviceCache?.size?:0

    inner class BtDeviceViewHolder(private val binding: BtItemListViewBinding) :
        RecyclerView.ViewHolder(binding.getRoot()) {

        fun bind(btDeviceModel: BluetoothDeviceModel) {
            Log.d(TAG, "bind: btDeviceModel=" + btDeviceModel.bt_name)
            binding.btDeviceModel = btDeviceModel
            binding.executePendingBindings()
        }
    }
}

