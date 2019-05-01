package com.shmrkm.queue.provider

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import com.shmrkm.adapter.Request.StoreQueueRequest
import com.shmrkm.queue.domain.Queue
import com.shmrkm.adapter.repository._
import com.shmrkm.adapter.usecase.StoreQueueUseCaseImpl
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.io.StdIn

final case class SuccessResponse(success: Boolean)

final case class QueueRequest(value: String) {
  def queue: Queue = Queue(value)
}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val success: RootJsonFormat[SuccessResponse] = jsonFormat1(SuccessResponse)
  implicit val queueFormat: RootJsonFormat[QueueRequest] = jsonFormat1(QueueRequest)
}


object ProviderServer extends JsonSupport {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  implicit val queueRepositoryImpl = new QueueRepositoryImpl

  def apply(): Unit = {

    val route =
      post {
        path("queue") {
          entity(as[QueueRequest]) { request =>
            val useCase = new StoreQueueUseCaseImpl()
            if (useCase.execute(StoreQueueRequest(request.queue))) {
              complete(SuccessResponse(true))
            } else {
              complete(SuccessResponse(false))
            }
//            KafkaProvider.register(Queue(queue.value))
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
