package pl.setblack.mb137.web

import japgolly.scalajs.react.ReactComponentB
import pl.setblack.lsa.basic.{PingMessage, PlayfieldState}
import scala.scalajs.js.timers._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.document

class Playfield {

}


object Playfield {
  def initPlayfield() = {

    val backend = new Backend(_)

    val PingMessageComponent = ReactComponentB[PingMessage]("PingMessage")
      .render(message =>
        <.li(
          <.span(message.msg),
          <.span(" @ "),
          <.span( message.author)))
      .build



    val MessagesComponent = ReactComponentB[(Seq[PingMessage], String)]("Messages")
      .render( (tuple)  =>
          <.div(s"${tuple._2} Messages",
            <.ol(tuple._1.map(m => PingMessageComponent(m)))))
      .build

     val PlayFieldComponent = ReactComponentB [PlayfieldState] ("PlayFieldComponent")
     .render ( playfield =>
       <.section(^.className := "messages")
            ( MessagesComponent( (playfield.pingMessages, "ping")),
          MessagesComponent( (playfield.pongMessages,"pong" )))
     )
     .build


    val initialState = PlayfieldState(Seq(),Seq() )


    val PingPongApp = ReactComponentB[Unit]("PingPongApp")
      .initialState(initialState)
      .backend(backend)
      .render((_, S, B) =>
      <.div(
        <.h3("Ping / Pong"),
        PlayFieldComponent(S),
        <.div(
          <.button(^.onClick ==> B.onPingClick)("Ping"),
          <.button(^.onClick ==> B.onPongClick)("Pong")
        )
      )
      ).buildU



    React.render(PingPongApp(), document.getElementById("react"))

  }

}