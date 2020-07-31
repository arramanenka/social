package com.romanenko.dao.mongo.repository

import com.romanenko.dao.mongo.model.MongoChat
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ChatRepo : ReactiveMongoRepository<MongoChat, String> {
    fun deleteByChatIdAndCreatorId(chatId: String, creatorId: String): Mono<MongoChat>
    fun findAllByMembersContainingOrCreatorId(memberId: String, creatorId: String): Flux<MongoChat>

    //todo change up to not repeat argument
    fun findByChatIdAndCreatorIdOrMembersContaining(chatId: String, queryingPerson: String, memberId: String): Mono<MongoChat>
}