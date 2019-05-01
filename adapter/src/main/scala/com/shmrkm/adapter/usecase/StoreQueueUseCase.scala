package com.shmrkm.adapter.usecase

import com.shmrkm.adapter.Request.{QueueRequest, StoreQueueRequest}
import com.shmrkm.queue.domain.repository.QueueProviderRepository

trait StoreQueueUseCase[req <: QueueRequest] {
  def execute(request: req)(implicit r: QueueProviderRepository): Boolean
}
