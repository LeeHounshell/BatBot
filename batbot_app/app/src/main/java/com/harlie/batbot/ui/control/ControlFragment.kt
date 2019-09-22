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
import android.content.Intent
import android.widget.Toast
import com.harlie.batbot.service.Constants
import com.harlie.batbot.util.BluetoothStateChangeEvent
import com.harlie.batbot.util.BluetoothStatusEvent
import com.harlie.batbot.util.LoggingTextTail
import kotlinx.android.synthetic.main.control_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import androidx.databinding.ObservableBoolean
import com.harlie.batbot.BluetoothActivity
import com.harlie.batbot.R
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.schedule
import kotlin.math.exp


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
    val m_robotConnection = ObservableBoolean(false)
    private var m_robotCommand = RobotCommandModel("", "")
    private var m_logging = LoggingTextTail()
    private var m_timer_ok = true
    private var m_havePing = false
    private var m_expectRobotCommand = 0
    private var m_haveRobotCommand = false
    private var m_pingStartTime = System.currentTimeMillis();

    private lateinit var m_ControlFragBinding : ControlFragmentBinding
    private lateinit var m_ControlViewModel: Control_ViewModel
    private lateinit var m_BluetoothAdapter: BluetoothAdapter
    private lateinit var m_fixedTimer: Timer

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
        m_ControlFragBinding.robotConnection = m_robotConnection
        m_ControlFragBinding.ctFragment = this
        m_ControlFragBinding.lifecycleOwner = viewLifecycleOwner
        m_ControlFragBinding.invalidateAll()

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
        m_ControlViewModel.getInputCommand().observe(this, Observer {
            it?.let {
                Log.d(TAG, "observe getInputCommand() ===> it.m_robotCommand=" + it.robotCommand)
                m_haveRobotCommand = true
                m_robotCommand.robotCommand = it.robotCommand
                m_robotCommand.commandPriority = it.commandPriority
                send(m_robotCommand.robotCommand) // send command to the robot
            }
        })
        m_ControlViewModel.getStarClicked().observe(this, Observer {
            it?.let {
                Log.d(TAG, "observe getStarClicked()===> * clicked=" + it)
                send("click: *")
            }
        })
        m_ControlViewModel.getOkClicked().observe(this, Observer {
            it?.let {
                Log.d(TAG, "observe getOkClicked()===> ok clicked=" + it)
                send("click: ok")
            }
        })
        m_ControlViewModel.getSharpClicked().observe(this, Observer {
            it?.let {
                Log.d(TAG, "obsere getSharpClicked()===> # clicked=" + it)
                send("click: #")
            }
        })
        disableButtons()
        connect()
    }

    fun expectRobotCommand(expecting: Boolean) {
        Log.d(TAG, "expectRobotCommand: " + expecting)
        if (expecting) {
            m_expectRobotCommand += 1
        }
        else {
            m_expectRobotCommand -= 1
        }
    }

    fun validateRobotCommand() {
        Log.d(TAG, "validateRobotCommand: m_expectRobotCommand=" + m_expectRobotCommand + ", m_haveRobotCommand=" + m_haveRobotCommand)
        if (m_expectRobotCommand > 0 && ! m_haveRobotCommand) {
            Log.e(TAG, "*** DATA BINDING ERROR ***")
            // try to touch View of UI thread
            activity?.runOnUiThread(java.lang.Runnable {
                Toast.makeText(context, "*** EXITING: FATAL ERROR ***", Toast.LENGTH_LONG).show()
            })
            Log.d(TAG, "FLUSH LOG")
            send("")
            Timer().schedule(3000) {
                Log.e(TAG, "*** SHUT DOWN APP ***")
                disconnect()
                activity?.onBackPressed()
                activity?.finishAndRemoveTask()
            }
        }
        else {
            Log.d(TAG, "validateRobotCommand: OK")
            m_expectRobotCommand -= 1;
            m_haveRobotCommand = false;
            Timer().schedule(5000) {
                Log.d(TAG, "One-Shot-Timer: FLUSH LOG")
                send("")
            }
        }
    }

    fun runFixedRateTimer() {
        Log.d(TAG, "runFixedRateTimer")
        m_fixedTimer = fixedRateTimer("timer", false, 0L, 100) {
            if (m_timer_ok) {
                activity!!.runOnUiThread {
                    if (logging != null) {
                        logging.text = m_logging.content()
                    }
                }
            }
        }
    }

    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
        EventBus.getDefault().register(this);
        runFixedRateTimer()
    }

    @Subscribe(threadMode = ThreadMode.MAIN) // from notify
    fun onBluetoothStateChangeEvent(bt_event: BluetoothStateChangeEvent) {
        Log.d(TAG, "onBluetoothStateChangeEvent: theState=" + bt_event.theState + ", whatChanged=" + bt_event.whatChanged)
        when(bt_event.whatChanged) {
            Constants.MESSAGE_STATE_CHANGE -> {
                when (bt_event.theState) {
                    BluetoothChatService.STATE_CONNECTING -> {
                        Log.d("status", "connecting")
                    }
                    BluetoothChatService.STATE_CONNECTED -> {
                        Log.d("status", "connected")
                    }
                    BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_NONE -> {
                        Log.d("status", "not connected")
                    }
                }
            }
            Constants.MESSAGE_WRITE -> {
                val writeBuf = bt_event.extra.getByteArray(Constants.DATA) as ByteArray
                val bytesSent = bt_event.extra.getInt(Constants.SIZE)
                // construct a string from the buffer
                val writeMessage = String(writeBuf)
                Log.d(TAG, "--> SENT $bytesSent BYTES: $writeMessage")
            }
            Constants.MESSAGE_READ -> {
                val readBuf = bt_event.extra.getByteArray(Constants.DATA) as ByteArray
                val bytesRead = bt_event.extra.getInt(Constants.SIZE)
                // construct a string from the valid bytes in the buffer
                val readData = String(readBuf, 0, bytesRead)
                // message received
                Log.d(TAG, "--> READ $bytesRead BYTES: $readData")
                m_logging.append(readData)
                if (readData.length > 0) {
                    logging.text = m_logging.content()

                    if (m_havePing == false) {
                        if (readData.contains("ping")) {
                            m_havePing = true
                            Log.d(TAG, "--> got ping reply <--")
                        } else {
                            Log.d(TAG, "no ping reply yet")
                            val now = System.currentTimeMillis();
                            if ((m_pingStartTime - now) > 10000) {
                                activity?.runOnUiThread(java.lang.Runnable {
                                    Toast.makeText(context, "ping failed", Toast.LENGTH_LONG).show()
                                });
                                disconnect()
                                gotoBluetoothActivity()
                            }
                        }
                    }
                }
            }
            Constants.MESSAGE_DEVICE_NAME -> {
                // save the connected device's name
                val connectedDeviceName = bt_event.extra.getString(Constants.DEVICE_NAME)
                Log.d(TAG, "===> connectedDeviceName=" + connectedDeviceName!!)
                enableButtons()
                m_ControlViewModel.doClickOk()
            }
            Constants.MESSAGE_TOAST -> {
                val message = bt_event.extra.getString(Constants.TOAST)
                Log.d(TAG, "===> TOAST message=" + message!!)
                activity?.runOnUiThread(java.lang.Runnable {
                    Toast.makeText(context,  message, Toast.LENGTH_LONG).show()
                });
            }
        }
    }

    private fun disableButtons() {
        Log.d(TAG, "disableButtons")
        m_robotConnection.set(false)
    }

    private fun enableButtons() {
        Log.d(TAG, "enableButtons")
        clearTextViews()
        m_robotConnection.set(true)
    }

    fun clearTextViews() {
        Log.d(TAG, "initializeTextViews")
        if (textOutput != null) {
            textOutput.text = ""
            Log.d(TAG, "textOutput is empty")
        }
        if (logging != null) {
            logging.text = ""
            Log.d(TAG, "logging is empty")
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) // from notify
    fun onBluetoothStatusEvent(bt_status_event: BluetoothStatusEvent) {
        Log.d(TAG, "onBluetoothStatusEvent: message=" + bt_status_event.message)
        msg(bt_status_event.message)
        if (bt_status_event.message.equals(Constants.DISCONNECT)) {
            activity?.runOnUiThread(java.lang.Runnable {
                Toast.makeText(context, "DISCONNECTED.", Toast.LENGTH_LONG).show()
            });
            disconnect()
            gotoBluetoothActivity()
        }
        else if (bt_status_event.message.equals(Constants.CONNECTION_FAILED)) {
            activity?.runOnUiThread(java.lang.Runnable {
                Toast.makeText(context, "CONNECTION FAILED.", Toast.LENGTH_LONG).show()
            });
            disconnect()
            gotoBluetoothActivity()
        }
        else if (bt_status_event.message.equals(Constants.CONNECTION_LOST)) {
            activity?.runOnUiThread(java.lang.Runnable {
                Toast.makeText(context, "BLUETOOTH FAILED.", Toast.LENGTH_LONG).show()
            });
            disconnect()
            gotoBluetoothActivity()
            //
            // NOTE: this is a problem with the Android bluetooth functionality and databinding
            // (it happens when the python bluez batbot server stops during an active session)
            // the only way i have found to fix it is to manually kill the Android BatBot app
            // and then restart it.   --- the root cause seems to be a databinding problem/confusion
            //
            // the 'validateRobotCommand' method detects this failure and resets the app.
        }
        else if (bt_status_event.message.equals(Constants.INITIALIZING)) {
            Log.d(TAG, "===> INITIALIZING <===")
        }
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        super.onStop()
        EventBus.getDefault().unregister(this);
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

        msg("please wait..")

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
        return "$operation,$x,$y"
    }

    fun send(message: String) {
        Log.d(TAG, "send: " + message);
        m_BluetoothChatService.send(message + '\n')
    }

    private fun msg(message: String) {
        Log.d(TAG, "msg: " + message)
        status.text = message
    }

    fun onClickTextOutput() {
        Log.d(TAG, "onClickTextOutput")
        m_robotCommand = RobotCommandModel(textOutput.text.toString(), "3")
        m_ControlViewModel.processAndDecodeMessage(m_robotCommand!!)
    }

    fun gotoBluetoothActivity() {
        Log.d(TAG, "gotoBluetoothActivity")
        m_timer_ok = false
        val controlIntent: Intent = Intent(activity, BluetoothActivity::class.java)
        controlIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(controlIntent)
        activity!!.overridePendingTransition(0, R.anim.fade_out);
    }

    private fun disconnect() {
        Log.d(TAG, "disconnect");
        m_BluetoothChatService.stop()
    }

}
