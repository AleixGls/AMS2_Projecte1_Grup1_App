package grup1.app

import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class ClientWS(serverUri: URI) : WebSocketClient(serverUri, Draft_6455(), null, 5000) {

    override fun onOpen(handshakedata: ServerHandshake?) {
        Connexio.onConnectat?.invoke()
    }

    override fun onMessage(message: String?) {
        if (message != null) {
            Connexio.onMissatge?.invoke(message)
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Connexio.onError?.invoke("Connexió tancada: " + reason)
    }

    override fun onError(ex: Exception?) {
        Connexio.onError?.invoke("Error de connexió: " + ex?.message)
    }
}

object Connexio {
    var client: ClientWS? = null

    var onConnectat: (() -> Unit)? = null
    var onMissatge: ((String) -> Unit)? = null
    var onError: ((String) -> Unit)? = null

    fun connectar(url: String) {
        var adreca = url
        if (!adreca.startsWith("ws://") && !adreca.startsWith("wss://")) {
            adreca = "wss://" + adreca
        }
        client = ClientWS(URI(adreca))
        client?.connect()
    }

    fun enviar(text: String) {
        if (client != null && client!!.isOpen) {
            client?.send(text)
        }
    }

    fun estaConnectat(): Boolean {
        return client != null && client!!.isOpen
    }
}