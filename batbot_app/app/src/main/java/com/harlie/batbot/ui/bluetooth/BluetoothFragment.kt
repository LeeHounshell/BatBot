package com.harlie.batbot.ui.bluetooth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.annotation.Nullable
import androidx.databinding.ObservableBoolean
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.harlie.batbot.ControlActivity
import com.harlie.batbot.ControlActivity.Companion.EXTRA_ADDRESS
import com.harlie.batbot.ControlActivity.Companion.EXTRA_DEVICE
import com.harlie.batbot.ControlActivity.Companion.EXTRA_NAME
import com.harlie.batbot.databinding.BluetoothFragmentBinding
import com.harlie.batbot.model.BluetoothDeviceModel
import com.harlie.batbot.util.BluetoothRecyclerAdapter


class BluetoothFragment : Fragment() {
    val TAG = "LEE: <" + BluetoothFragment::class.java.getName() + ">";

    companion object {
        val instance = BluetoothFragment()
        fun getInstance(): Fragment {
            return instance
        }
    }

    private val m_selected = ObservableBoolean(false)

    private lateinit var m_selectedDevice: BluetoothDeviceModel
    private lateinit var m_View: View
    private lateinit var m_BluetoothFragmentBinding: BluetoothFragmentBinding
    private lateinit var m_BluetoothViewModel: Bluetooth_ViewModel
    private lateinit var m_RecyclerView: RecyclerView
    private lateinit var m_BluetoothRecyclerAdapter: BluetoothRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView");
        m_BluetoothFragmentBinding = DataBindingUtil.inflate(
            inflater, com.harlie.batbot.R.layout.bluetooth_fragment, container, false
        )
        m_RecyclerView = m_BluetoothFragmentBinding.recyclerView
        m_BluetoothFragmentBinding.lifecycleOwner = viewLifecycleOwner
        m_View = m_BluetoothFragmentBinding.getRoot()
        return m_View
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            m_BluetoothViewModel = ViewModelProviders.of(it).get(Bluetooth_ViewModel::class.java)
        }

        m_BluetoothRecyclerAdapter = BluetoothRecyclerAdapter(m_BluetoothViewModel)
        m_RecyclerView.adapter = m_BluetoothRecyclerAdapter
        m_RecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        m_BluetoothFragmentBinding.selected = false
        m_BluetoothFragmentBinding.btFragment = this
        m_BluetoothFragmentBinding.recyclerView.adapter = m_BluetoothRecyclerAdapter

        m_BluetoothFragmentBinding.recyclerView.apply {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        m_BluetoothViewModel.m_bluetoothDevicesList.observe(this, Observer {
            Log.d(TAG, "observe: new m_bluetoothDevicesList content: it=" + it)
//            var count = 0
//            for (btDevice in it) {
//                Log.d(TAG, "observe: device" + count + "=" + btDevice.bt_name)
//            }
            m_BluetoothRecyclerAdapter.m_deviceCache = it
            m_BluetoothRecyclerAdapter.notifyDataSetChanged()
        })

        m_BluetoothViewModel.m_selectedDevice.observe(this, Observer {
            Log.d(TAG, "observe: the selected device=" + it)
            m_selectedDevice = it
            m_BluetoothFragmentBinding.selected = true
        })
    }

    fun onClickRefresh() {
        Log.d(TAG, "onClickRefresh")
        m_BluetoothFragmentBinding.selected = false
        m_BluetoothViewModel.initializeDeviceList(context!!)
    }

    fun onClickConnect() {
        Log.d(TAG, "onClickConnect")
        // Cancel discovery because it otherwise slows down the connection.
        m_BluetoothViewModel.m_BluetoothAdapter?.cancelDiscovery()
        gotoControlActivity(m_selectedDevice)
    }

    fun gotoControlActivity(btModel: BluetoothDeviceModel) {
        Log.d(TAG, "gotoControlActivity: btModel name=" + btModel.device.name + ", address=" + btModel.device.address)
        val controlIntent: Intent = Intent(context, ControlActivity::class.java)
        controlIntent.putExtra(EXTRA_NAME, btModel.device.name)
        controlIntent.putExtra(EXTRA_ADDRESS, btModel.device.address)
        controlIntent.putExtra(EXTRA_DEVICE, btModel.device)
        startActivity(controlIntent)
    }
}
