package eu.rekawek.coffeegb.swing.network

import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer

data class Message(internal val buffer: ByteArray, private val length: Int = buffer.size) {
    init {
        check(buffer.size >= length)
    }

    fun getByteBuffer() = ByteBuffer.wrap(buffer, 0, length)

    internal fun writeToStream(outputStream: OutputStream) {
        outputStream.write(0xFF);
        outputStream.write(0xFF);

        val lengthBuf = ByteArray(4)
        ByteBuffer.wrap(lengthBuf).putInt(length)
        outputStream.write(lengthBuf)

        outputStream.write(buffer, 0, length)
    }

    companion object {
        internal fun readFromStream(inputStream: InputStream): Message? {
            val header = inputStream.readNBytes(6)
            if (header.size < 6) {
                return null
            }
            check(header[0] == 0xFF.toByte() && header[1] == 0xFF.toByte())

            val lengthBuf = ByteBuffer.wrap(header, 2, 4)
            val length = lengthBuf.getInt()

            val buffer = inputStream.readNBytes(length)
            if (buffer.size < length) {
                return null
            }
            return Message(buffer)
        }
    }
}