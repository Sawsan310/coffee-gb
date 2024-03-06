package eu.rekawek.coffeegb.swing.network

import eu.rekawek.coffeegb.swing.io.serial.ServerEventListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.Volatile

class SerialTcpServer : Runnable {
    @Volatile
    private var doStop = false
    private val listeners = CopyOnWriteArrayList<ServerEventListener>()
    private var connection: ServerConnection? = null
    private var serverSocket: ServerSocket? = null

    override fun run() {
        doStop = false
        serverSocket = ServerSocket(PORT)
        serverSocket?.use { serverSocket ->
            serverSocket.soTimeout = 100
            listeners.forEach { it.onServerStarted() }
            while (!doStop) {
                var socket: Socket
                try {
                    socket = serverSocket.accept()
                    LOG.info("Got new connection: {}", socket.inetAddress)
                    connection = ServerConnection(socket.getInputStream(), socket.getOutputStream()) {
                        listeners.forEach { it.onConnectionClosed() }
                    }
                    listeners.forEach { it.onNewConnection(socket.inetAddress.hostName, connection!!) }
                } catch (e: SocketTimeoutException) {
                    // do nothing
                } catch (e: IOException) {
                    LOG.error("Error in accepting connection", e)
                }
            }
        }
        listeners.forEach { it.onServerStopped() }
    }

    fun stop() {
        doStop = true
        if (connection != null) {
            connection?.close()
            connection = null
        }
        if (serverSocket != null) {
            serverSocket?.close()
            serverSocket = null
        }
    }

    fun registerListener(listener: ServerEventListener) {
        listeners.add(listener)
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(SerialTcpServer::class.java)
        const val PORT: Int = 6688
    }
}
