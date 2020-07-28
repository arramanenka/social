package com.romanenko.dao.mongo

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ChatRepo : ReactiveMongoRepository<MongoChat, String> {
    fun deleteByChatIdAndCreatorId(chatId: String, creatorId: String): Mono<Void>
    fun findAllByMembersContaining(memberId: String): Flux<MongoChat>

    //todo change up to not repeat argument
    fun findByChatIdAndCreatorIdOrMembersContaining(chatId: String, queryingPerson: String, memberId: String): Mono<MongoChat>
}