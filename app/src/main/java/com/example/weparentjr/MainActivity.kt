package com.example.weparentjr

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.example.weparentjr.data.response.BaseResponse
import com.example.weparentjr.model.AppInfo
import com.example.weparentjr.viewmodel.ApplicationViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var applicationViewModel: ApplicationViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        applicationViewModel = ViewModelProvider(this).get(ApplicationViewModel::class.java)

        val textView: TextView = findViewById(R.id.text_view)

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
            if (packageName == "com.example.mealmate.test") {
                blockApp(packageName)
                continue // Skip the blocked app
            }
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
        // Implement code to block the app
        // For example, you can disable the app by setting its enabled state to false
        val pm: PackageManager = packageManager
        pm.setApplicationEnabledSetting(
            packageName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }


    fun addApps(name:String,icon:String,packageName:String) {
        applicationViewModel.addApps(name = name, icon = icon, packageName = packageName,id="6400eb96a231e0530f0614fe")

    }



}
