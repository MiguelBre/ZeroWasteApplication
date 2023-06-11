package br.senai.jandira.sp.zerowastetest

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class ChatHandler {
    lateinit var mChat: Socket

    @Synchronized
    fun setSocketChat(authToken: String) {
        try {
            val options = IO.Options()
            options.auth = mapOf("token" to authToken)
            mChat = IO.socket("https://zero-waste-chat.azurewebsites.net", options)
        } catch (e: URISyntaxException) {

        }
    }

    @Synchronized
    fun getSocketChat(): Socket {
        return mChat
    }

    @Synchronized
    fun establishConnectionChat() {
        mChat.connect()
    }

    @Synchronized
    fun closeConnectionChat() {
        mChat.disconnect()
    }
}