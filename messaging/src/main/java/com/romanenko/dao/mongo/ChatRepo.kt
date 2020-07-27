package com.romanenko.dao.mongo

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface ChatRepo : ReactiveMongoRepository<MongoChat, Int> {
    fun deleteByChatIdAndCreatorId(chatId: Int, creatorId: String): Mono<Void>
}