package com.romanenko.dao.mongo

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@ConditionalOnProperty(name = ["message.storage"], havingValue = "mongo", matchIfMissing = true)
interface MessageRepo : ReactiveMongoRepository<MongoMessage, String> {
    fun deleteByMessageIdAndSenderIdAndReceiverId(messageId: String, senderId: String, receiverId: String): Mono<MongoMessage>

    @Query("{'\$or': [{'receiverId':?0, 'senderId':?1}, {'receiverId':?1, 'senderId':?0}]}")
    fun findAllMessagesBetweenUsers(user: String, secondUser: String, pageable: Pageable): Flux<MongoMessage>

}