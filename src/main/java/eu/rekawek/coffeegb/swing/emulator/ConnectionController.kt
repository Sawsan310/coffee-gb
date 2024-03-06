package eu.rekawek.coffeegb.swing.emulator

import eu.rekawek.coffeegb.swing.io.serial.*
import eu.rekawek.coffeegb.swing.network.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ConnectionController {

    private var client: SerialTcpClient? = null
    private var server: SerialTcpServer? = null
    private val serverListeners = mutableListOf<ServerEventListener>()
    private val clientListeners = mutableListOf<ClientEventListener>()

    fun startServer() {
        stop()
        server = SerialTcpServer()
        serverListeners.forEach { server!!.registerListener(it) }
        server!!.registerListener(object : ServerEventListener {
            override fun onNewConnection(host: String?, connection: ServerConnection) {
                val resp = connection.send(Message("Ping".encodeToByteArray()))
                LOG.info("Received response {}", String(resp!!.getByteBuffer().array()))
            }
        })
        Thread(server).start()
    }

    fun startClient(host: String) {
        stop()
        client = SerialTcpClient(host, object : MessageHandler {
            override fun handle(req: Message): Message {
                LOG.info("Received request {}", String(req.getByteBuffer().array()))
                return Message("Pong".encodeToByteArray())
            }
        })
        clientListeners.forEach { client!!.registerListener(it) }
        Thread(client).start()
    }

    fun stop() {
        client?.stop()
        client = null

        server?.stop()
        server = null
    }

    fun registerServerListener(listener: ServerEventListener) {
        serverListeners.add(listener)
        server?.registerListener(listener)
    }

    fun registerClientListener(listener: ClientEventListener) {
        clientListeners.add(listener)
        client?.registerListener(listener)
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(ConnectionController::class.java)
    }
}
