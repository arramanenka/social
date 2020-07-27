package com.romanenko.dao.mongo

import com.romanenko.dao.ChatDao
import com.romanenko.model.Chat
import com.romanenko.security.Identity
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class MongoChatDao(
        private val chatRepo: ChatRepo
) : ChatDao {

    override fun createChat(chat: Chat): Mono<Chat> {
        return chatRepo.save(MongoChat(chat)).map { it.toModel() }
    }

    override fun updateChat(chat: Chat): Mono<Chat> {
        TODO("Not yet implemented")
    }

    override fun deleteChat(identity: Identity, chatId: Int): Mono<Void> {
        return chatRepo.deleteByChatIdAndCreatorId(chatId, identity.id)
                .switchIfEmpty(Mono.error<Void>(HttpClientErrorException(HttpStatus.NOT_FOUND)))
    }

    override fun addMember(identity: Identity, chatId: Int, userId: String): Mono<Void> {
        TODO("Not yet implemented")
    }

    override fun removeMember(identity: Identity, chatId: Int, userId: String): Mono<Void> {
        TODO("Not yet implemented")
    }

    override fun getOwnChats(identity: Identity): Flux<Chat> {
        TODO("Not yet implemented")
    }
}