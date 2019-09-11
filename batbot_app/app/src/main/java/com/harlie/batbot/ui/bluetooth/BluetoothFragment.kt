package com.harlie.batbot.ui.bluetooth

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.harlie.batbot.databinding.BluetoothFragmentBinding
import com.harlie.batbot.util.BluetoothRecyclerAdapter


class BluetoothFragment : Fragment() {
    val TAG = "LEE: <" + BluetoothFragment::class.java.getName() + ">";

    companion object {
        val instance = BluetoothFragment()
        fun getInstance(): Fragment {
            return instance
        }
    }

    private lateinit var m_view: View
    private lateinit var m_bluetoothFragmentBinding: BluetoothFragmentBinding
    private lateinit var m_bluetoothViewModel: Bluetooth_ViewModel
    private lateinit var m_recyclerView: RecyclerView
    private lateinit var m_recyclerAdapter: BluetoothRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView");
        m_bluetoothFragmentBinding = DataBindingUtil.inflate(
            inflater, com.harlie.batbot.R.layout.bluetooth_fragment, container, false
        )
        m_bluetoothFragmentBinding.lifecycleOwner = viewLifecycleOwner
        m_recyclerView = m_bluetoothFragmentBinding.recyclerView

        m_view = m_bluetoothFragmentBinding.getRoot()
        return m_view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            m_bluetoothViewModel = ViewModelProviders.of(it).get(Bluetooth_ViewModel::class.java)
        }

        m_recyclerAdapter = BluetoothRecyclerAdapter(m_bluetoothViewModel)
        m_recyclerView.adapter = m_recyclerAdapter
        m_bluetoothFragmentBinding.recyclerView.adapter = m_recyclerAdapter
        m_recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        m_bluetoothFragmentBinding.recyclerView.apply {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        m_bluetoothViewModel.m_bluetoothDevicesList.observe(this, Observer {
            Log.d(TAG, "observe: new m_bluetoothDevicesList content: it=" + it)
            var count = 0
            for (btDevice in it) {
                Log.d(TAG, "observe: device" + count + "=" + btDevice.bt_name)
            }
            m_recyclerAdapter.m_deviceCache = it
            m_recyclerAdapter.notifyDataSetChanged()
        })

        m_bluetoothViewModel.selectedId.observe(this, Observer { Log.d(TAG, "NEW selectedId=" + it) })
    }
}
