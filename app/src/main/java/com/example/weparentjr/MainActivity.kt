package com.example.weparentjr

import android.annotation.SuppressLint
import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import io.socket.client.IO
import io.socket.client.Socket

class MainActivity : AppCompatActivity() {

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var componentNamee: ComponentName
    private lateinit var RemainingProgress: ProgressBar
    private lateinit var socket: Socket
    private val REQUEST_CODE_ENABLE_ADMIN = 1
    private var dataSec: Int = 0
    private val handler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RemainingProgress = findViewById(R.id.RemainingProgress)
        val lockNowButton = findViewById<Button>(R.id.button)
        lockNowButton.setOnClickListener {
            devicePolicyManager.lockNow()
        }

        // Initialize DevicePolicyManager and ComponentName
        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        componentNamee = ComponentName(this, DeviceAdminReceiver::class.java)

        try {
            val opts = IO.Options()
            opts.forceNew = true
            socket = IO.socket("http://172.16.1.45:9090", opts)
            socket.connect()
            socket.on(Socket.EVENT_CONNECT) {
                println("Socket Connected")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        socket.on("remaining_time") { args ->
            val data = args[0] as Int
            dataSec=data * 60
            runOnUiThread {
                // Do something with the data here
                // For example, update the UI
                println(dataSec)
                startDeviceUsageLimit(dataSec)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
    }

    private fun startDeviceUsageLimit(timeLimitSeconds: Int) {
        Log.d("MainActivity", "startDeviceUsageLimit() called with timeLimitSeconds = $timeLimitSeconds")

        // Check if the app is a device administrator
        if (!devicePolicyManager.isAdminActive(componentNamee)) {
            // Prompt the user to enable device administrator
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentNamee)
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Your administrator explanation")
            startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN)
            return
        }

        // Set the maximum time limit for device usage in seconds
        devicePolicyManager.setMaximumTimeToLock(componentNamee, (timeLimitSeconds * 1000).toLong())

        // Post a delayed task to lock the device after the specified time limit
        handler.postDelayed({
            // Lock the device after the specified time limit
            devicePolicyManager.lockNow()
        }, timeLimitSeconds.toLong() * 1000)
    }


}
