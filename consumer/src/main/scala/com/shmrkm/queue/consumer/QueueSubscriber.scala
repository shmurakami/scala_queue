package com.shmrkm.queue.consumer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

trait Subscriber {
  def subscribe: Future[Success[Boolean]]
}

class QueueSubscriber extends Subscriber {

  override def subscribe: Future[Success[Boolean]] = Future {
    new Success[Boolean](true)
  }
}
