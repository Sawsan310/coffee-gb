package eu.rekawek.coffeegb.swing.network

import java.io.InputStream
import java.io.OutputStream

class ClientConnection(
    private val inputStream: InputStream,
    private val outputStream: OutputStream,
    private val messageHandler: MessageHandler
) : Runnable {
    override fun run() {
        while (true) {
            val request = Message.readFromStream(inputStream) ?: break
            val response = messageHandler.handle(request)
            response.writeToStream(outputStream)
        }
    }

    fun close() {
        inputStream.close()
        outputStream.close()
    }
}
