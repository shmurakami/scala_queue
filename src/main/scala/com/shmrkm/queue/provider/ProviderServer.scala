package com.shmrkm.queue.provider

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object ProviderServer {

  def run(): Unit = {
    implicit val system: ActorSystem = ActorSystem("queue-provider")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val route =
      path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`application/json`, """{"say":"hello"}"""))
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 10080)
    println(s"http server online at http://localhost:10080")
    StdIn.readLine()

    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())

  }

}
