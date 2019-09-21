// adapted from code by: Martin O'Hanlon
// from: https://github.com/martinohanlon/BlueDot/blob/master/clients/android/app/src/main/java/com/stuffaboutcode/bluedot/BluetoothChatService.java
// this code is changed to use Green Robot events instead of Handler
//
// Thanks Martin!  your BlueDot rocks!!   github.com/martinohanlon/BlueDot

package com.harlie.batbot.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;

import com.harlie.batbot.util.BluetoothStateChangeEvent;
import com.harlie.batbot.util.BluetoothStatusEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BluetoothChatService {
    private final static String TAG = "LEE: <" + BluetoothChatService.class.getSimpleName() + ">";

    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    // Member fields
    private final BluetoothAdapter mAdapter;
    private AcceptThread mSecureAcceptThread;
    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;
    private int mNewState;
    private int mCount = 0;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    /**
     * Constructor. Prepares a new BluetoothChat session.
     */
    public BluetoothChatService() {
        Log.d(TAG, "BluetoothChatService");
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mNewState = mState;
    }

    private void notifyStateChange(int whatChanged, int theState, Bundle extra) {
        Log.d(TAG, "notifyStateChange: whatChanged=" + whatChanged + ", theState=" + theState);
        switch (whatChanged) {
            case Constants.MESSAGE_STATE_CHANGE:
                switch (theState) {
                    case BluetoothChatService.STATE_CONNECTED:
                        Log.d("status","connected");
                        sendProtocolVersion();
                        break;
                    case BluetoothChatService.STATE_CONNECTING:
                        Log.d("status","connecting");
                        break;
                    case BluetoothChatService.STATE_LISTEN:
                    case BluetoothChatService.STATE_NONE:
                        Log.d("status","not connected");
                        if (++mCount > 3) {
                            disconnect();
                        }
                        break;
                }
                break;
            case Constants.MESSAGE_WRITE: // NOTE: message already sent, just a notify here
                byte[] writeBuf = (byte[]) extra.getByteArray(Constants.DATA);
                int bytesSent = extra.getInt(Constants.SIZE);
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                Log.d(TAG, "--> SENT: " + writeMessage);
                break;
            case Constants.MESSAGE_READ:
                byte[] readBuf = (byte[]) extra.getByteArray(Constants.DATA);
                int bytesRead = extra.getInt(Constants.SIZE);
                // construct a string from the valid bytes in the buffer
                String readData = new String(readBuf, 0, bytesRead);
                // message received
                Log.d(TAG, "--> READ: " + readData);
                break;
            case Constants.MESSAGE_DEVICE_NAME:
                // save the connected device's name
                String connectedDeviceName = extra.getString(Constants.DEVICE_NAME);
                Log.d(TAG, "===> connectedDeviceName=" + connectedDeviceName);
                sendProtocolVersion();
                break;
            case Constants.MESSAGE_TOAST:
                String message = extra.getString(Constants.TOAST);
                Log.d(TAG, "===> TOAST message=" + message);
                break;
        }

        BluetoothStateChangeEvent bt_event = new BluetoothStateChangeEvent(whatChanged, theState, extra);
        bt_event.post();
    }

    private void sendProtocolVersion() {
        Log.d(TAG, "send the protocol version to the server");
        send("3," + Constants.PROTOCOL_VERSION + "," + Constants.CLIENT_NAME + "\n");
        send("ping\n");
    }

    private synchronized void notifyBluetoothStatus(String status) {
        mState = getState();
        Log.d(TAG, "notifyBluetoothStatus: " + mNewState + " -> " + mState); // really old state here
        mNewState = mState;
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DATA, status);
        BluetoothStatusEvent bt_status_event = new BluetoothStatusEvent(status);
        bt_status_event.post();
    }

    private void disconnect() {
        Log.d(TAG, "disconnect");
        mCount = 0;
        stop();
        notifyBluetoothStatus(Constants.DISCONNECT);
    }

    public void send(String message) {
        Log.d(TAG, "===> send: message=" + message);
        // Check that we're actually connected before trying anything
        if (getState() != BluetoothChatService.STATE_CONNECTED) {
            Log.e(TAG, "===> UNABLE TO SEND message - NOT CONNECTED <===");
            if (++mCount > 3) {
                disconnect();
            }
        }
        else {
            // Check that there's something to send
            if (message.length() > 0) {
                // Get the message bytes and tell the BluetoothChatService to write
                byte[] bytearray = message.getBytes();
                write(bytearray);
                mCount = 0;
            }
        }
    }

    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        Log.d(TAG, "getState");
        return mState;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {
        Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (mSecureAcceptThread == null) {
            mSecureAcceptThread = new AcceptThread(true);
            mSecureAcceptThread.start();
        }
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread(false);
            mInsecureAcceptThread.start();
        }

        notifyBluetoothStatus(Constants.INITIALIZING);
    }

    public synchronized void cancelAll(BluetoothDevice device, boolean secure) {
        Log.d(TAG, "cancelAll");

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    public synchronized void connect(BluetoothDevice device, boolean secure) {
        Log.d(TAG, "connect to: " + device);
        cancelAll(device, secure);

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device, secure);
        mConnectThread.start();

        notifyBluetoothStatus("connecting.. (please wait)");
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device, final String socketType) {
        Log.d(TAG, "connected, Socket Type:" + socketType);

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }
        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket, socketType);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, device.getName());
        notifyStateChange(Constants.MESSAGE_DEVICE_NAME, mState, bundle);

        notifyBluetoothStatus("connected: " + device.getName());
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        Log.d(TAG, "stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }
        mState = STATE_NONE;

        notifyBluetoothStatus("stopped.");
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        Log.d(TAG, "write: out=" + out);
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }

        // the write is not synchronized
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        Log.d(TAG, "connectionFailed");
        // Send a failure message back to the Activity
        mState = STATE_NONE;

        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Unable to connect");
        notifyStateChange(Constants.MESSAGE_TOAST, mState, bundle);
        notifyBluetoothStatus(Constants.CONNECTION_FAILED);
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        Log.d(TAG, "connectionLost");
        // Send a failure message back to the Activity
        mState = STATE_NONE;

        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Connection lost");
        notifyStateChange(Constants.MESSAGE_TOAST, mState, bundle);
        notifyBluetoothStatus(Constants.CONNECTION_LOST);
    }


    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        private final String TAG = "LEE: <" + AcceptThread.class.getSimpleName() + ">";

        // The local server socket
        private final BluetoothServerSocket mmServerSocket;
        private String mSocketType;

        public AcceptThread(boolean secure) {
            Log.d(TAG, "AcceptThread");
            BluetoothServerSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            // Create a new listening server socket
            try {
                if (secure) {
                    tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, MY_UUID_SECURE);
                } else {
                    tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord( NAME_INSECURE, MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
            }
            mmServerSocket = tmp;
            mState = STATE_LISTEN;
        }

        public void run() {
            Log.d(TAG, "run: mSocketType=" + mSocketType + "BEGIN mAcceptThread" + this);
            setName("AcceptThread" + mSocketType);

            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket Type: " + mSocketType + "accept() failed", e);
                    disconnect();
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothChatService.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice(), mSocketType);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            Log.i(TAG, "END mAcceptThread, socket Type: " + mSocketType);
        }

        public void cancel() {
            Log.d(TAG, "cancel: mSocketType=" + mSocketType + "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Socket Type" + mSocketType + "close() of server failed", e);
            }
        }
    }


    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final String TAG = "LEE: <" + ConnectedThread.class.getSimpleName() + ">";

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            Log.d(TAG, "ConnectThread");
            mmDevice = device;
            BluetoothSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                if (secure) {
                    tmp = device.createRfcommSocketToServiceRecord( MY_UUID_SECURE);
                } else {
                    tmp = device.createInsecureRfcommSocketToServiceRecord( MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
            }
            mmSocket = tmp;
            mState = STATE_CONNECTING;
        }

        public void run() {
            Log.i(TAG, "run: BEGIN mConnectThread mSocketType=" + mSocketType);
            setName("ConnectThread" + mSocketType);

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " + mSocketType + " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothChatService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice, mSocketType);
        }

        public void cancel() {
            Log.d(TAG, "cancel");
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
            }
        }
    }


    /**
     * This thread runs during a connection with a remote device.
     * It processes all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final String TAG = "LEE: <" + ConnectedThread.class.getSimpleName() + ">";

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            Log.d(TAG, "ConnectedThread: socketType=" + socketType);
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            mState = STATE_CONNECTED;
        }

        public void run() {
            Log.i(TAG, "run: BEGIN mConnectedThread");

            // Keep listening to the InputStream while connected
            while (mState == STATE_CONNECTED) {
                try {
                    byte[] buffer = new byte[1024];
                    int bytes;
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    if (bytes == -1) {
                        connectionLost();
                    }
                    else {
                        // Send the obtained bytes to the UI Activity
                        Bundle bundle = new Bundle();
                        bundle.putByteArray(Constants.DATA, buffer);
                        bundle.putInt(Constants.SIZE, bytes);
                        notifyStateChange(Constants.MESSAGE_READ, mState, bundle);
                    }

                } catch (IOException e) {
                    Log.e(TAG, "Disconnected", e);
                    disconnect();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            Log.d(TAG, "write: buffer=" + buffer);
            try {
                mmOutStream.write(buffer);
                // Share the sent message back to the UI Activity
                Bundle bundle = new Bundle();
                bundle.putByteArray(Constants.DATA, buffer);
                bundle.putInt(Constants.SIZE, buffer.length);
                notifyStateChange(Constants.MESSAGE_WRITE, mState, bundle);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            Log.d(TAG, "cancel");
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}

