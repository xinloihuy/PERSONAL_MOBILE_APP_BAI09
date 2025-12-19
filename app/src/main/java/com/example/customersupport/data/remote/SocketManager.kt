package com.example.customersupport.data.remote

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.net.URISyntaxException

object SocketManager {
    private var socket: Socket? = null
    private const val SERVER_URL = "http://10.0.2.2:9092"

    fun connect() {
        if (socket != null && socket!!.connected()) {
            return
        }
        try {
            val opts = IO.Options().apply {
                transports = arrayOf("polling", "websocket") 
                timeout = 20000
                reconnection = true
                forceNew = true 
            }
            socket = IO.socket(SERVER_URL, opts)
            socket?.on(Socket.EVENT_CONNECT) { Log.d("SocketManager", "✅ Connected!") }
            socket?.on(Socket.EVENT_DISCONNECT) { args -> Log.w("SocketManager", "❌ Disconnected: ${args.getOrElse(0) { "" }}") }
            socket?.on(Socket.EVENT_CONNECT_ERROR) { args -> Log.e("SocketManager", "🚫 Connection Error: ${args.getOrElse(0) { "" }}") }
            socket?.connect()
        } catch (e: URISyntaxException) {
            Log.e("SocketManager", "URI Syntax Error: ", e)
        }
    }

    fun on(event: String, listener: Emitter.Listener) {
        socket?.on(event, listener)
    }

    fun once(event: String, listener: Emitter.Listener) {
        socket?.once(event, listener)
    }

    fun off(event: String) {
        socket?.off(event)
    }

    fun emit(event: String, vararg args: Any) {
        socket?.emit(event, *args)
    }

    fun disconnect() {
        socket?.disconnect()
        socket = null
    }

    fun isConnected(): Boolean {
        return socket?.connected() ?: false
    }
}
