package com.shmrkm.queue.consumer.repository

import com.shmrkm.queue.domain.Queue
import com.shmrkm.queue.domain.repository.QueueConsumerRepository

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

object QueueRepositoryImpl extends QueueConsumerRepository {
  def consume(): Future[Option[Queue]] = Future {
    RedisClientWrapper.get(RedisQueue) match {
      case Some(q) => Some(Queue(q))
      case None    => None
    }
  }

  case object RedisQueue extends RedisKey {
    def key = "test"
  }
}
