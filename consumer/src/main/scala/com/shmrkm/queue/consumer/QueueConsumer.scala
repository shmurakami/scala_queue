package com.shmrkm.queue.consumer

import scala.util.{Failure, Success}
import com.shmrkm.queue.consumer.repository.{QueueRepositoryImpl, RedisClientWrapper}
import com.shmrkm.queue.consumer.repository.QueueRepositoryImpl.RedisQueue

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

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

    Await.ready(subscribe, Duration.Inf)
  }

  def consume(): Unit = {
    val f = QueueRepositoryImpl.consume()
    f onComplete {
      case Success(q) => q match {
        // store to database
        case Some(queue) => println(queue)
        case None => println("no queue")
      }
      case Failure(e) => println(s"failed to fetch ${e.getMessage}")
    }
    Await.ready(f, Duration.Inf)
  }
}
