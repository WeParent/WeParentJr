package com.example.weparentjr.views

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.example.weparentjr.R

class SettingsFragment : PreferenceFragmentCompat() {





    fun goToSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", context.packageName, null)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val appInfo = context.packageManager.getApplicationInfo(context.packageName, 0)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.putExtra("app_package", context.packageName)
        intent.putExtra("app_uid", appInfo.uid)
        context.startActivity(intent)
    }

    fun showLocationPermissionDialog(context: Context?) {
        val alertDialog = AlertDialog.Builder(context)
            .setTitle("Location Permission Required")
            .setMessage("This app requires location permissions to function properly. Please go to settings and grant ALL THE TIME location permission then RESTART the app.")
            .setPositiveButton("Go to Settings") { dialog, which ->
                if (context != null) {
                    goToSettings(context)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                val location = findPreference<SwitchPreferenceCompat>("location")
                location?.isChecked = false
                dialog.dismiss()
            }

            .create()
        alertDialog.show()

    }
    fun showNotificationPermissionDialog(context: Context) {
        val alertDialog = AlertDialog.Builder(context)
            .setTitle("Notification Permission Required")
            .setMessage("This app requires notification permissions to function properly. Please go to settings and grant the notification permission then RESTART the app.")
            .setPositiveButton("Go to Settings") { dialog, which ->
                goToSettings(context)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                val location = findPreference<SwitchPreferenceCompat>("location")
                location?.isChecked = false
                dialog.dismiss()
            }
            .create()
        alertDialog.show()

    }
    fun areLocationPermissionsGranted(context: Context?): Boolean {
        val fineLocationPermission = context?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } == PackageManager.PERMISSION_GRANTED

        val coarseLocationPermission = context?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } == PackageManager.PERMISSION_GRANTED

        val backgroundLocationPermission = context?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } == PackageManager.PERMISSION_GRANTED

        // Return true if all three permissions are granted
        return fineLocationPermission && coarseLocationPermission && backgroundLocationPermission
    }

    fun areNotificationsPermissionsGranted(context: Context?): Boolean {
        val notificationPermission = context?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } == PackageManager.PERMISSION_GRANTED

        // Return true if all three permissions are granted
        return notificationPermission
    }

    override fun onResume() {
        super.onResume()
        val location = findPreference<SwitchPreferenceCompat>("location")
        val notification = findPreference<SwitchPreferenceCompat>("notification")

        location?.isChecked = areLocationPermissionsGranted(context);
        notification?.isChecked = areNotificationsPermissionsGranted(context);
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val location = findPreference<SwitchPreferenceCompat>("location")
        val notification = findPreference<SwitchPreferenceCompat>("notification")
        val darkmode = findPreference<SwitchPreferenceCompat>("darkMode")
        val sharedPreferences = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
        val isDarkMode = sharedPreferences?.getBoolean("DARKMODE", true)

        val currentmode = AppCompatDelegate.getDefaultNightMode()

        if( currentmode == AppCompatDelegate.MODE_NIGHT_YES)
        {
            Log.d("MODE NIGHT YES", darkmode?.isChecked.toString())
            darkmode?.isChecked = true
        }
        else {
            darkmode?.isChecked = false
        }


        location?.isChecked = areLocationPermissionsGranted(context);
        notification?.isChecked = areNotificationsPermissionsGranted(context);

        darkmode?.setOnPreferenceChangeListener { preference, newValue ->

            val isChecked = newValue as Boolean
            if(isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPreferences?.edit { putBoolean("DARKMODE", true) }
            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPreferences?.edit { putBoolean("DARKMODE", false) }
            }
             true
        }


        location?.setOnPreferenceChangeListener { preference, newValue ->
            val isChecked = newValue as Boolean
            if (isChecked) {
                showLocationPermissionDialog(context);
            } else {

            }
            true
        }
        notification?.setOnPreferenceChangeListener { preference, newValue ->
            val isChecked = newValue as Boolean
            if (isChecked) {
                context?.let { showNotificationPermissionDialog(it) };
            } else {

            }
            true
        }



    }
}