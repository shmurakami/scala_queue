package com.shmrkm.queue.provider

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import com.shmrkm.queue.{KafkaProvider, Queue}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.io.StdIn

final case class SuccessResponse(success: Boolean)

final case class QueueRequest(value: String)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val success: RootJsonFormat[SuccessResponse] = jsonFormat1(SuccessResponse)
  implicit val queueFormat: RootJsonFormat[QueueRequest] = jsonFormat1(QueueRequest)
}


object ProviderServer extends JsonSupport {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def apply(): Unit = {
    val route =
      put {
        path("register") {
          entity(as[QueueRequest]) { queue =>
            KafkaProvider.register(Queue(queue.value))

            complete(SuccessResponse(true))
          }
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8081)
    println("running...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

}
