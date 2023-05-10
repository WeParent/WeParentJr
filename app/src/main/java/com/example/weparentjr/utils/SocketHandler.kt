package com.example.weparentjr.utils
import android.os.Build
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException
import android.util.Log

object SocketHandler {

    lateinit var mSocket: Socket

    @Synchronized
    fun setSocket() {
        try {
            val options = IO.Options()
            options.query = "buildId=${Build.ID}"
            options.reconnection = true
            options.forceNew = true
            mSocket = IO.socket("http://192.168.1.124:9090",options)
        } catch (e: URISyntaxException) {
            Log.d("Socket init/syntax err",e.toString())
        }
    }

    @Synchronized
    fun getSocket(): Socket {
        return mSocket
    }

    @Synchronized
    fun establishConnection() {
        mSocket.connect()
    }

    @Synchronized
    fun closeConnection() {
        mSocket.disconnect()
    }
}