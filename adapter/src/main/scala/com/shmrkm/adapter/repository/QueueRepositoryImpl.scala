package com.shmrkm.adapter.repository

import com.redis.RedisClient
import com.shmrkm.queue.domain.Queue
import com.shmrkm.queue.domain.repository.QueueProviderRepository

class QueueRepositoryImpl extends QueueProviderRepository {
  lazy val redisClient = new RedisClient()

  override def store(queue: Queue): Boolean = {
    getClient.set(RedisQueue.key, queue)
  }

  private def getClient: RedisClient = {
    if (!redisClient.connected) {
      redisClient.reconnect
    }
    redisClient
  }

  case object RedisQueue {
    def key = "test"
  }
}
