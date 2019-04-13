package com.shmrkm.queue

import com.typesafe.scalalogging.Logger

trait Register {
  def register(queue: Queue)
}

object KafkaProvider extends Register {
  val logger = Logger("queue")

  def register(queue: Queue): Unit = {
    logger.info("logging sample")
  }
}
