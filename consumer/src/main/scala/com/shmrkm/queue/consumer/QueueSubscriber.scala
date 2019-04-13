package com.shmrkm.queue.consumer

import com.shmrkm.queue.domain.Queue

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Subscriber {
  def subscribe: Future[Queue]
}

class QueueSubscriber extends Subscriber {

  override def subscribe: Future[Queue] = Future {
    Queue("foobar")
  }
}
