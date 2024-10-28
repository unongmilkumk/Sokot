package com.sokot.game

import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class GameSokot(private val port: Int) {
    private val clients = mutableListOf<Socket>()

    fun start() {
        val serverSocket = ServerSocket(port)

        while (true) {
            val clientSocket = serverSocket.accept()
            println("Client connected: ${clientSocket.inetAddress.hostAddress}")
            clients.add(clientSocket)
            thread { handleClient(clientSocket) }
        }
    }

    private fun handleClient(socket: Socket, joinMessage : String? = "") {
        val reader = socket.getInputStream().bufferedReader()
        val writer = socket.getOutputStream().bufferedWriter()

        try {
            writer.write("$joinMessage\n")
            writer.flush()

            while (true) {
                val message = reader.readLine() ?: break
                broadcast(message, socket)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            clients.remove(socket)
            socket.close()
        }
    }

    @Synchronized
    private fun broadcast(message: String, senderSocket: Socket) {
        for (client in clients) {
            if (client != senderSocket) {
                try {
                    val writer = client.getOutputStream().bufferedWriter()
                    writer.write("$message\n")
                    writer.flush()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}