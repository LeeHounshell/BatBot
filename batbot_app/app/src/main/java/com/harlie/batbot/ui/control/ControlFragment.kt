package com.harlie.batbot.ui.control

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.harlie.batbot.model.RobotCommandModel
import androidx.annotation.Nullable
import com.harlie.batbot.databinding.ControlFragmentBinding
import com.harlie.batbot.service.BluetoothChatService
import com.harlie.batbot.util.DynamicMatrix
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.widget.Toast
import com.harlie.batbot.ControlActivity
import kotlinx.android.synthetic.main.control_fragment.*


class ControlFragment : Fragment() {
    val TAG = "LEE: <" + ControlFragment::class.java.getName() + ">";

    companion object {
        val instance = ControlFragment()
        fun getInstance(): Fragment {
            return instance
        }
    }

    // Initialize the BluetoothChatService to perform bluetooth connections
    private var m_BluetoothChatService = BluetoothChatService()
    private var m_robotCommand = RobotCommandModel("", "")
    private lateinit var m_ControlFragBinding : ControlFragmentBinding
    private lateinit var m_ControlViewModel: Control_ViewModel
    private lateinit var m_BluetoothAdapter: BluetoothAdapter

    private var m_name: String? = null
    private var m_address: String? = null
    private var m_device: BluetoothDevice? = null
    private var m_uniqueId: String? = null

    private var last_x = 0.0
    private var last_y = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView");
        m_ControlFragBinding = DataBindingUtil.inflate(
            inflater, com.harlie.batbot.R.layout.control_fragment, container, false
        )
        m_ControlFragBinding.robotCommand = m_robotCommand
        m_ControlFragBinding.lifecycleOwner = viewLifecycleOwner
        val view = m_ControlFragBinding.getRoot()
        return view
    }

    fun setDeviceInfo(name: String, address: String, device: BluetoothDevice, uniqueId: String) {
        Log.d(TAG, "setDeviceInfo: name=" + name + ", address=" + address + ", uniqueId=" + uniqueId);
        this.m_name = name;
        this.m_address = address
        this.m_device = device
        this.m_uniqueId = uniqueId
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState)
        getViewModel()
        m_ControlViewModel.m_inputCommand.observe(this, Observer {
            it?.let {
                Log.d(TAG, "===> it.m_robotCommand=" + it.robotCommand)
                m_robotCommand.robotCommand = it.robotCommand
                m_robotCommand.commandPriority = it.commandPriority
                send(m_robotCommand.robotCommand) // send command to the robot
            }
        })
        connect()
    }

    private fun getViewModel(): Control_ViewModel {
        Log.d(TAG, "getViewModel");
        if (! ::m_ControlViewModel.isInitialized) {
            activity?.let {
                m_ControlViewModel = ViewModelProviders.of(it).get(Control_ViewModel::class.java)
            }
        }
        return m_ControlViewModel
    }

    fun connect() {
        Log.d(TAG, "connect")
        // we need to initialize the bluetooth adapter
        getViewModel()
        m_BluetoothAdapter = m_ControlViewModel.initDefaultAdapter()

        // Get the BluetoothDevice object
        //val device = mBluetoothAdapter.getRemoteDevice(activity)
        // Attempt to connect to the device

        msg("connecting..")

        m_BluetoothChatService.connect(m_device, true)

        msg("connected.")

        // Once connected setup the listener
        bluedot_matrix.setOnUseListener(object : DynamicMatrix.DynamicMatrixListener {
            override fun onPress(
                cell: DynamicMatrix.MatrixCell,
                pointerId: Int,
                actual_x: Float,
                actual_y: Float
            ) {
                Log.d(TAG, "onPress")
                val x = calcX(cell, actual_x)
                val y = calcY(cell, actual_y)
                send(buildMessage("1", x, y))
                last_x = x
                last_y = y
            }

            override fun onMove(
                cell: DynamicMatrix.MatrixCell,
                pointerId: Int,
                actual_x: Float,
                actual_y: Float
            ) {
                Log.d(TAG, "onMove");
                val x = calcX(cell, actual_x)
                val y = calcY(cell, actual_y)
                if (x != last_x || y != last_y) {
                    send(buildMessage("2", x, y))
                    last_x = x
                    last_y = y
                }
            }

            override fun onRelease(
                cell: DynamicMatrix.MatrixCell,
                pointerId: Int,
                actual_x: Float,
                actual_y: Float
            ) {
                Log.d(TAG, "onRelease");
                val x = calcX(cell, actual_x)
                val y = calcY(cell, actual_y)
                send(buildMessage("0", x, y))
                last_x = x
                last_y = y
            }
        })
    }

    private fun calcX(cell: DynamicMatrix.MatrixCell, actual_x: Float): Double {
        Log.d(TAG, "calcX")
        var relative_x = actual_x - cell.bounds.left
        relative_x = (relative_x - cell.width / 2) / (cell.width / 2)
        return Math.round(relative_x * 10000.0).toDouble() / 10000.0
    }

    private fun calcY(cell: DynamicMatrix.MatrixCell, actual_y: Float): Double {
        Log.d(TAG, "calcY")
        var relative_y = actual_y - cell.bounds.top
        relative_y = (relative_y - cell.height / 2) / (cell.height / 2) * -1
        return Math.round(relative_y * 10000.0).toDouble() / 10000.0
    }

    private fun buildMessage(operation: String, x: Double, y: Double): String {
        Log.d(TAG, "buildMessage");
        return "$operation,$x,$y\n"
    }

    fun send(message: String) {
        Log.d(TAG, "send: " + message);
        m_BluetoothChatService.send(message)
    }

    private fun disconnect() {
        Log.d(TAG, "disconnect");
        m_BluetoothChatService.stop()
    }

    private fun msg(message: String) {
        Log.d(TAG, "msg: " + message)
        status.text = message
    }

}
