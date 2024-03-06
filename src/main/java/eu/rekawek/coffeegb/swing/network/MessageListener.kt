package eu.rekawek.coffeegb.swing.network

interface MessageHandler {
    fun handle(req: Message): Message
}