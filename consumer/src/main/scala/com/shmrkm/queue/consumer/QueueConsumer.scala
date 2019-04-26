package com.shmrkm.queue.consumer

import scala.util.{Failure, Success}
import com.shmrkm.queue.consumer.repository.QueueRepositoryImpl

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object QueueConsumer {

  def main(args: Array[String]): Unit = {
    /**
      * what consumer does?
      * - subscribe kafka topic
      * - fetch
      * - save to data store
      *   - first redis
      *   - second dynamodb
      */

    val subscriber = new QueueSubscriber
    val subscribe = subscriber.subscribe

    subscribe.onComplete {
      case Success(success) => consume()
      case Failure(exception) => println(exception.getMessage)
    }

    Await.ready(subscribe, Duration.fromNanos(1 * 1000 * 1000))
  }

  def consume(): Unit = {
    QueueRepositoryImpl.consume().onComplete {
      case Success(q) => q match {
        // store to database
        case Some(queue) => println(queue.value)
        case None => println("no queue")
      }
      case Failure(e) => println(s"failed to fetch ${e.getMessage}")
    }
  }
}
