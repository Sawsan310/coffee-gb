package eu.rekawek.coffeegb.swing.network

import eu.rekawek.coffeegb.swing.io.serial.ClientEventListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.Socket
import java.util.concurrent.CopyOnWriteArrayList

class SerialTcpClient(private val host: String, private val messageHandler: MessageHandler) : Runnable {
    private var clientSocket: Socket? = null
    private var connection: ClientConnection? = null
    private val listeners = CopyOnWriteArrayList<ClientEventListener>()

    override fun run() {
        try {
            clientSocket = Socket(host, SerialTcpServer.PORT)
            LOG.info("Connected to {}", clientSocket!!.inetAddress)
            connection =
                ClientConnection(clientSocket!!.getInputStream(), clientSocket!!.getOutputStream(), messageHandler)
            listeners.forEach { it.onConnectedToServer() }
            connection!!.run()
        } catch (e: IOException) {
            LOG.error("Error in making connection", e)
        }
        listeners.forEach { it.onDisconnectedFromServer() }
    }

    fun stop() {
        connection?.close()
        try {
            clientSocket?.close()
        } catch (e: IOException) {
            LOG.error("Error in closing client socket", e)
        }
    }

    fun registerListener(listener: ClientEventListener) {
        listeners.add(listener)
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(SerialTcpServer::class.java)
    }
}
