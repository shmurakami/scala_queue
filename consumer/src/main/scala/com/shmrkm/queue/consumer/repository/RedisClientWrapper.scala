package com.shmrkm.queue.consumer.repository

import com.redis._

trait RedisKeyValue {
  def key: String
  def value: Any
}
trait RedisKey {
  def key: String
}

object RedisClientWrapper {

  lazy val redisClient = new RedisClient()

  def set(keyValue: RedisKeyValue): Boolean = {
    redisClient.set(keyValue.key, keyValue.value)
  }

  def get(k: RedisKey): Option[String] = {
    try {
      redisClient.reconnect
      redisClient.get[String](k.key)

    } finally {
      if (redisClient.connected) {
        redisClient.disconnect
      }
    }
  }

}
