package com.romanenko.dao.mongo

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface MessageRepo : ReactiveMongoRepository<MongoMessage, Int> {
    fun deleteAllByChatId(chatId: Int): Mono<Void>
    fun deleteByChatIdAndMessageIdAndSenderId(chatId: Int, messageId: Int, senderId: String): Mono<MongoMessage>
    fun findAllByChatIdOrderByCreatedAtDesc(chatId: Int): Flux<MongoMessage>
}