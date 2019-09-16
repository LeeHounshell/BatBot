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
import kotlinx.android.synthetic.main.control_fragment.*


class ControlFragment : Fragment() {
    val TAG = "LEE: <" + ControlFragment::class.java.getName() + ">";

    companion object {
        val instance = ControlFragment()
        fun getInstance(): Fragment {
            return instance
        }
    }

    private var m_robotCommand = RobotCommandModel("", "")
    private lateinit var m_ControlFragBinding : ControlFragmentBinding
    private lateinit var m_ControlViewModel: Control_ViewModel
    private lateinit var m_BluetoothAdapter: BluetoothAdapter
    private lateinit var m_BluetoothChatService: BluetoothChatService
    private lateinit var m_outStringBuffer: StringBuffer
    private lateinit var m_inStringBuffer: StringBuffer

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            m_ControlViewModel = ViewModelProviders.of(it).get(Control_ViewModel::class.java)
        }
        m_ControlViewModel.m_inputCommand.observe(this, Observer {
            it?.let {
                Log.d(TAG, "it.m_robotCommand=" + it.robotCommand)
                m_robotCommand.robotCommand = it.robotCommand
                send(m_robotCommand.robotCommand) // send command to the robot
            }
        })

        // we need to initialize the bluetooth adapter
        m_BluetoothAdapter = m_ControlViewModel.initDefaultAdapter()

        // Initialize the BluetoothChatService to perform bluetooth connections
        m_BluetoothChatService = BluetoothChatService()

        // Get the BluetoothDevice object
        //val device = mBluetoothAdapter.getRemoteDevice(activity)
        // Attempt to connect to the device

        m_BluetoothChatService.connect(m_device, true)


        val out: String = "Hello, BatBot!  i am your friend."
        val charset = Charsets.UTF_8
        val outBytes = out.toByteArray(charset)

        m_BluetoothChatService.write(outBytes)

        // Initialize the buffer for outgoing messages
        m_outStringBuffer = StringBuffer("this is a test of the outgoing buffer")
        // Initialize the buffer for incoming messages
        m_inStringBuffer = StringBuffer("")

        // Once connected setup the listener
        bluedot_matrix.setOnUseListener(object : DynamicMatrix.DynamicMatrixListener {
            override fun onPress(
                cell: DynamicMatrix.MatrixCell,
                pointerId: Int,
                actual_x: Float,
                actual_y: Float
            ) {
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
                val x = calcX(cell, actual_x)
                val y = calcY(cell, actual_y)
                send(buildMessage("0", x, y))
                send(out)
                last_x = x
                last_y = y
            }

        })

    }

    fun setDeviceInfo(name: String?, address: String?, device: BluetoothDevice?, uniqueId: String) {
        Log.d(TAG, "setDeviceInfo: name=" + name + ", address=" + address + ", uniqueId=" + uniqueId)
        m_name = name;
        m_address = address
        m_device = device
        m_uniqueId = uniqueId
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
        return "$operation,$x,$y\n"
    }

    fun send(message: String) {
        // Check that we're actually connected before trying anything
        if (m_BluetoothChatService.getState() !== BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(context, "cant send message - not connected", Toast.LENGTH_SHORT).show()
            return
        }

        // Check that there's actually something to send
        if (message.length > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            val send = message.toByteArray()
            m_BluetoothChatService.write(send)

            // Reset out string buffer to zero and clear the edit text field
            m_outStringBuffer.setLength(0)
        }
    }

    private fun disconnect() {
        if (m_BluetoothChatService != null) {
            m_BluetoothChatService.stop()
        }
    }

    private fun msg(message: String) {
        status.text = message
    }
}
