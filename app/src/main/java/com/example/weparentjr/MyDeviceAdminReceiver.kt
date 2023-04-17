package com.example.weparentjr

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent

class MyDeviceAdminReceiver : DeviceAdminReceiver(){
    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        // Called when the device administrator is disabled
    }

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        // Called when the device administrator is enabled
    }
}