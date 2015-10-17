package pl.setblack.mb137.web

import org.scalajs.dom
import org.scalajs.dom.raw.{WebSocket, Document}
import pl.setblack.lsa.basic.PingMessage
import upickle.default._

class ServerConnection(val backend: Backend) {

  val serverConnection = startWs()

  def sendMessage( msg :PingMessage) = {
    serverConnection.send( write[PingMessage](msg))
  }

  private def startWs() = {
    val connection = new WebSocket(getWebsocketUri(dom.document, "anonymous user"))

    connection.onopen = { (event: org.scalajs.dom.raw.Event) ⇒
      println("connection done");

      event
    }
    connection.onerror = { (event: org.scalajs.dom.raw.ErrorEvent) ⇒

    }
    connection.onmessage = { (event: org.scalajs.dom.raw.MessageEvent) ⇒
      val msg = read[PingMessage](event.data.toString)
      println("received message:" + msg.toString)

      backend.newMessage(msg)
    }
    connection.onclose = { (event: org.scalajs.dom.raw.Event) ⇒
    }
    connection
  }

  private def getWebsocketUri(document: Document, nameOfChatParticipant: String): String = {
    val wsProtocol = if (dom.document.location.protocol == "https:") "wss" else "ws"
    s"$wsProtocol://${dom.document.location.host}/services/socket?name=$nameOfChatParticipant"
  }
}

