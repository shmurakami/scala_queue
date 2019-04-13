package com.shmrkm.queue.consumer

import scala.util.{Failure, Success}

import scala.concurrent.ExecutionContext.Implicits.global

object QueueConsumer {

  def main(args: Array[String]): Unit = {
    println("consumer")

    /**
      * what consumer does?
      * - subscribe kafka topic
      * - fetch
      * - save to data store
      *   - first redis
      *   - second dynamodb
      */

    // subscribe
    val subscriber = new QueueSubscriber

    subscriber.subscribe.onComplete {
      case Success(queue) => println(queue.value)
      case Failure(exception) => println(exception.getMessage)
    }
  }

}
