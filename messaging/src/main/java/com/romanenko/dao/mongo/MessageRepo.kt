package com.romanenko.dao.mongo

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface MessageRepo : ReactiveMongoRepository<MongoMessage, String> {
    fun deleteByMessageIdAndSenderIdAndReceiverId(messageId: String, senderId: String, receiverId: String): Mono<MongoMessage>
}