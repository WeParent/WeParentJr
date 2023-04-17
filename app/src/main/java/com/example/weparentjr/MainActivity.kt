package com.example.weparentjr
import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {



    fun goToSettings(context: Context) {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + context.packageName)
        context.startActivity(intent)
    }

    fun showLocationPermissionDialog(context: Context) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Location Permission Required")
            .setMessage("This app requires location permissions to function properly. Please go to settings and grant ALL THE TIME location permission then RESTART the app.")
            .setPositiveButton("Go to Settings") { dialog, which ->
                goToSettings(this)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .create()
            alertDialog.show()

    }

    fun areLocationPermissionsGranted(context: Context): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val backgroundLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // Return true if all three permissions are granted
        return fineLocationPermission && coarseLocationPermission && backgroundLocationPermission
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val intent = Intent(this, BackgroundService::class.java)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Demarrage mta service lel android eli baad android OREO khater mch kif
            startForegroundService(intent)
        }
        else {
            //Demarrage mta service lel android eli kbal android OREO
            startService(intent)
        }
        val handler = Handler(Looper.getMainLooper())
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
          if (!areLocationPermissionsGranted(this)){
              showLocationPermissionDialog(this)
          }
        }, 8000)


    }

}