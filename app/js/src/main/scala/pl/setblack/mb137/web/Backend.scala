package pl.setblack.mb137.web

import japgolly.scalajs.react.{ReactEventI, BackendScope}
import pl.setblack.lsa.basic.{PingMessage, PlayfieldState}

class Backend($: BackendScope[Unit, PlayfieldState]) {

  val server = new ServerConnection(this)

  def newMessage(msg: PingMessage) = {
    println(s"received ${msg}")
    $.modState( (s:PlayfieldState) => {
      println(s"received ${msg.msg}")
      msg.msg match {
        case "ping" =>
          println("adding ping")
          s.copy(pingMessages = (s.pingMessages :+ msg))
        case _ =>
          println("adding pong")
          s.copy(pongMessages = (s.pongMessages :+ msg))
      }
    }
     )
  }



  def onPingClick(e: ReactEventI) = {
    server.sendMessage( PingMessage( "ping", "someone"))


    println("ping")
  }

  def onPongClick(e: ReactEventI) = {
    server.sendMessage( PingMessage( "pong", "someone"))

    println("pong");
  }


}

