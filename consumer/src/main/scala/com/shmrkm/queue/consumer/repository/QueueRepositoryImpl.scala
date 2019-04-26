package com.shmrkm.queue.consumer.repository

import com.shmrkm.queue.domain.Queue
import com.shmrkm.queue.domain.repository.QueueConsumerRepository

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

object QueueRepositoryImpl extends QueueConsumerRepository {
  def consume(): Future[Option[Queue]] = Future {
    Some(Queue("queue via Repository"))
  }

  def consume2(): Queue = {
    ???
  }
}
