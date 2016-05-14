package pl.setblack.mb137.server

import akka.actor._
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Keep, Source, Sink, Flow}
import pl.setblack.lsa.basic.PingMessage


import upickle.default._


trait Backend {
  def theFlow(sender: String): Flow[String, PingMessage, Unit]

  def injectMessage(message: BoardEvent): Unit
}

object Backend {
  def create(system: ActorSystem): Backend = {

    val boardActor:ActorRef =
      system.actorOf(Props(new Actor {
        var subscribers = Set.empty[ActorRef]
        var cnt = 0
        def receive: Receive = {
          case NewParticipant(name, subscriber) ⇒
            context.watch(subscriber)
            subscribers += subscriber
            println(s"${name} joined")

          case msg: ReceivedMessage    ⇒ {
            println("received from "+msg.sender)
            println(s"----message-----\b${msg.message}\n-----------------------")
            val clientMessage  = read[PingMessage](msg.message)
            val answer =  PingMessage ( msg =  clientMessage.msg match  {
              case "ping" => "pong"
              case _ => "ping"
            }, author =  clientMessage.author)
            subscribers.foreach( s => s ! answer)
            cnt = cnt + 1

          }
          case ParticipantLeft(person) ⇒ println(s"{person} left")
          case Terminated(sub)         ⇒ subscribers -= sub // clean up dead subscribers
        }

      }),name = "backendActor")


    def backendInSink(sender: String) = Sink.actorRef[BoardEvent](boardActor, ParticipantLeft(sender))

    new Backend {
      def theFlow(sender: String): Flow[String, PingMessage, Unit] = {
        println("sender is:"+sender)
        val in =
          Flow[String]
            .map(ReceivedMessage(sender, _))
            .to(backendInSink(sender))
        val out =
          Source.actorRef[PingMessage](10, OverflowStrategy.fail)
            .mapMaterializedValue(boardActor ! NewParticipant(sender, _))
        Flow.fromSinkAndSource(in, out)
      }

      def injectMessage(message: BoardEvent): Unit = boardActor ! message // non-streams interface
    }

  }


}

sealed trait BoardEvent
case class NewParticipant(name: String, subscriber: ActorRef) extends BoardEvent
case class ParticipantLeft(name: String) extends BoardEvent
case class ReceivedMessage(sender: String, message: String) extends BoardEvent {
}
