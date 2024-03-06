package eu.rekawek.coffeegb.swing.io.serial

import eu.rekawek.coffeegb.swing.network.ServerConnection

interface ServerEventListener {
    fun onServerStarted() {}
    fun onServerStopped() {}
    fun onNewConnection(host: String?, connection: ServerConnection) {}
    fun onConnectionClosed() {}
}
