package com.shmrkm.queue

trait Register {
  def register(queue: Queue)
}

object KafkaProvider extends Register {
  def register(queue: Queue): Unit = {
    println("registered!")
    ???
  }
}
