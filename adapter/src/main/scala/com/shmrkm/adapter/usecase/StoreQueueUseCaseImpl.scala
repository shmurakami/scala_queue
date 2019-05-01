package com.shmrkm.adapter.usecase

import com.shmrkm.adapter.Request.StoreQueueRequest
import com.shmrkm.queue.domain.repository.QueueProviderRepository

class StoreQueueUseCaseImpl extends StoreQueueUseCase[StoreQueueRequest] {
  override def execute(request: StoreQueueRequest)(implicit repository: QueueProviderRepository): Boolean = {
    repository.store(request.queue)
  }

}
