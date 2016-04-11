package pl.setblack.mb137.server


import java.util.Date

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.stage._
import pl.setblack.lsa.basic.PingMessage

import scala.concurrent.duration._

import akka.http.scaladsl.server.Directives
import akka.stream.{OverflowStrategy, Materializer}
import akka.stream.scaladsl.Flow

import upickle.default._

class Webservice(implicit fm: Materializer, system: ActorSystem) extends Directives {
  val theBackend = Backend.create(system)

  def route =
    get {
      pathSingleSlash {
        getFromResource("web/index.html")
      } ~ pathPrefix("scjs")(getFromResourceDirectory("")) ~
        pathPrefix("services") {
          path("socket") {
            parameter('name) { name ⇒
              handleWebsocketMessages(websocketMessagesFlow(sender = name))
            }
          } ~ {
            complete {
              "other services here...."
            }
          }
        }
    } ~
      getFromResourceDirectory("web")


  def websocketMessagesFlow(sender: String): Flow[Message, Message, Unit] =
    Flow[Message]
      .collect {
      case TextMessage.Strict(msg) => msg
    }.via(theBackend.theFlow(sender))
      .map {
      case msg:PingMessage=> {
        println(s"sennding ${msg}")
        TextMessage.Strict(write[PingMessage](msg))
      }
      case _ => {
        TextMessage.Strict(write("unknown message encountered"))
      }
    }.via(reportErrorsFlow)


  def reportErrorsFlow[T]: Flow[T, T, Unit] =
    Flow[T]
      .transform(() ⇒ new PushStage[T, T] {
      def onPush(elem: T, ctx: Context[T]): SyncDirective = ctx.push(elem)

      override def onUpstreamFailure(cause: Throwable, ctx: Context[T]): TerminationDirective = {
        println(s"WS stream failed with $cause")
        cause.printStackTrace()
        super.onUpstreamFailure(cause, ctx)
      }
    })
}


