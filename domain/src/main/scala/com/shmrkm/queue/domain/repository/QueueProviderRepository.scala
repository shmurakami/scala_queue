package com.shmrkm.queue.domain.repository

import com.shmrkm.queue.domain.Queue

import scala.concurrent.Future

trait QueueProviderRepository {
  def store(queue: Queue): Boolean
}

trait QueueConsumerRepository {
  def consume(): Future[Option[Queue]]
}
