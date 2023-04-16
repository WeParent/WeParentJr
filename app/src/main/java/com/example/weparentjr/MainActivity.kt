package com.example.weparentjr

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.UserManager
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.weparentjr.data.response.BaseResponse
import com.example.weparentjr.model.AppInfo
import com.example.weparentjr.utils.MyDeviceAdminReceiver
import com.example.weparentjr.viewmodel.ApplicationViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var componentName: ComponentName
    private lateinit var applicationViewModel: ApplicationViewModel

    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var isAdminPermission = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        componentName = ComponentName(this, MyDeviceAdminReceiver::class.java)
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
        startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN)


        applicationViewModel = ViewModelProvider(this).get(ApplicationViewModel::class.java)

        val textView: TextView = findViewById(R.id.text_view)
        val button: Button=findViewById(R.id.button)

        val pm: PackageManager = packageManager
        val packages: List<PackageInfo> = pm.getInstalledPackages(0)
        val appList = mutableListOf<AppInfo>()
        for (i in packages.indices) {
            val packageInfo = packages[i] // declare packageInfo inside the loop
            if ((packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0) {
                // Skip system apps
                continue
            }
            val appName: String = packageInfo.applicationInfo.loadLabel(pm).toString()
            val packageName: String = packageInfo.packageName

            val icon: Drawable = pm.getApplicationIcon(packageName)

            addApps(appName,icon.toString(),packageName)

            appList.add(AppInfo(appName, packageName, icon))
        }
        val appListString = StringBuilder()
        for (app in appList) {
            appListString.append(app.name).append("\n")
            appListString.append(app.packageName).append("\n\n")
            //appListString.append(ImageUtils.drawableToString(app.icon)).append("\n\n")
        }
        textView.text = appListString.toString()
        button.setOnClickListener {
            Log.d("MyApp", "Setting click listener on button"   )
            blockApp("com.aksantara.omah")
        }


        applicationViewModel.addAppsResult.observe(this, { response ->
            when (response) {
                is BaseResponse.Loading -> {
                    print("loading data")
                }
                is BaseResponse.Success -> {
                    val addAppsResponse = response.data // get the addAppsResponse object from the response
                }
                is BaseResponse.Error -> {
                    val errorMessage = response.msg // get the error message from the response
                }
            }
        })
    }

    fun blockApp(packageName: String) {
        Log.d("MyApp", "Blocking"   )
        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        componentName = ComponentName(this, MyDeviceAdminReceiver::class.java)

        // Check if your app is already a device admin
        if (!devicePolicyManager.isAdminActive(componentName)) {
            Log.d("MyApp", "App is not a device admin "   )
            // Ask the user to enable device admin for your app
           /* val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
            startActivityForResult(intent, REQUEST_ENABLE_ADMIN)*/
            Log.d("MyApp", "App is device admin now"   )
        } else {
            Log.d("MyApp", "Blocking is now"  )
            // Block the app you want to block
            val packageName = packageName
            val pm = packageManager
            val ai = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            pm.setApplicationEnabledSetting(packageName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0)

        }
    }


    fun addApps(name:String,icon:String,packageName:String) {
        applicationViewModel.addApps(name = name, icon = icon, packageName = packageName,id="6400eb96a231e0530f0614fe")

    }

    companion object {
        private const val REQUEST_CODE_ENABLE_ADMIN = 1
    }
    private fun requestPermission() {
        isAdminPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.BIND_DEVICE_ADMIN
        ) == PackageManager.PERMISSION_GRANTED
        
    }





}

