package com.example.weparentjr.utils
import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.weparentjr.Location
import com.google.android.gms.location.*


class BackgroundService : Service() {
    private val Service_ID = 1
    private var clickTimeStamps = mutableListOf<Long>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback


    //Receiver eli yokeed yestana screen locked
    private val Receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            val mSocket = SocketHandler.getSocket()


           if (action == Intent.ACTION_BATTERY_CHANGED) {
               val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
               val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
               val batteryLevel = level!! * 100 / scale!!.toFloat()
               mSocket.emit("battery",batteryLevel.toString())
               Log.d("BATTERY",batteryLevel.toString())
           }

            // ken lka screen tsakret
            if (action == Intent.ACTION_SCREEN_OFF) {
                // ken screen off izid el timestamp mta screen off lel lista bech icompareha baad
                clickTimeStamps.add(System.currentTimeMillis())
                Log.d("Timestamps mta clicks",clickTimeStamps.toString())
                // Check if the last 5 screen off events have a time difference of more than 10 seconds
                if (clickTimeStamps.size >= 5) {
                    val lastFiveClicks = clickTimeStamps.takeLast(5)
                    val timeDiff = lastFiveClicks.last() - lastFiveClicks.first()
                    Log.d("El wakt bin clicks",timeDiff.toString())
                    if (timeDiff < 10000) {

                        Toast.makeText(context, "Screen lock button pressed 5 consecutive times !", Toast.LENGTH_SHORT).show()

                        mSocket.emit("panic","message")
                        clickTimeStamps.clear()
                    }
                    else {
                        clickTimeStamps.clear()
                    }
                }
            }
            // ken tel tsaker w taawed thal
            else if (action == Intent.ACTION_BOOT_COMPLETED) {
                // ken tel tsaker w taawed thal lezm naawdo start service bech yokeed dima yekhdem service
                val serviceIntent = Intent(context, BackgroundService::class.java)
                context?.startService(serviceIntent)
            }
        }
    }


    fun LocationManager() {

        val mSocket = SocketHandler.getSocket()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("Error", "Missing permissions")
        }
        val locationRequest = LocationRequest.create()?.apply {
            interval = 5000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                val location = Location(
                    p0.lastLocation?.latitude.toString(),
                    p0.lastLocation?.longitude.toString()
                )
                Log.d("Location", location.toString())
                mSocket.emit("location", location)

            }
        }
        if (locationRequest != null) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        //Send location in background to socket with buildID
        SocketHandler.setSocket()
        SocketHandler.establishConnection()


        //Notification
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            val channelId = "BackgroundService"
            val channel = NotificationChannel(channelId,"Default",NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            val notification =  Notification.Builder(this,channelId).apply {
                setContentTitle("We Parent junior is running in the background")
                setContentText("Press the volume up button 5 times if you're in danger.")

            }.build()
            startForeground(Service_ID,notification)
        }
        //Register el receiver bech yestana el "Screen off"
        val screenOffFilter = IntentFilter("android.intent.action.SCREEN_OFF")
        registerReceiver(Receiver, screenOffFilter)


        //Register el receiver bech yestana el reboot mta tel
        val rebootFilter = IntentFilter(Intent.ACTION_BOOT_COMPLETED)
        registerReceiver(Receiver, rebootFilter)

        val batteryFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(Receiver, batteryFilter)


    }




    override fun onDestroy() {
        super.onDestroy()
        // Unregister el receiver kif service yekef
        unregisterReceiver(Receiver)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED){

            println("Permissions granted")




                LocationManager()

        }
        else
        {
            println("No permissions")
        }
        return START_STICKY
    }
}