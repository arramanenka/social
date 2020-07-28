package com.romanenko.dao.mongo

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface MessageRepo : ReactiveMongoRepository<MongoMessage, String> {
    fun deleteAllByChatId(chatId: String): Mono<Void>
    fun deleteByChatIdAndMessageIdAndSenderId(chatId: String, messageId: String, senderId: String): Mono<MongoMessage>
    fun findAllByChatIdOrderByCreatedAtDesc(chatId: String): Flux<MongoMessage>
}