package eu.rekawek.coffeegb.swing.network

import eu.rekawek.coffeegb.swing.emulator.ConnectionController
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.OutputStream

class ServerConnection(
    private val inputStream: InputStream,
    private val outputStream: OutputStream,
    private val closeListener: () -> Unit
) {
    fun send(msg: Message): Message? {
        val response = try {
            msg.writeToStream(outputStream)
            Message.readFromStream(inputStream)
        } catch (e: Exception) {
            LOG.error("Exception in sending message", e)
            null
        }
        if (response == null) {
            close()
        }
        return response
    }

    fun close() {
        closeListener()
        inputStream.close()
        outputStream.close()
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(ServerConnection::class.java)
    }
}
