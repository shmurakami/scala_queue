package com.shmrkm.adapter

import com.shmrkm.queue.domain.Queue

object Request {

  sealed trait QueueRequest

  case class StoreQueueRequest(queue: Queue) extends QueueRequest

}
