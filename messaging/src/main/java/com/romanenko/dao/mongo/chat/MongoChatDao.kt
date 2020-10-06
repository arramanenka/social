package com.romanenko.dao.mongo.chat

import com.romanenko.dao.ChatDao
import com.romanenko.model.PrivateChat
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class MongoChatDao(
        private val chatRepo: ChatRepo
) : ChatDao {
    override fun getChats(ownerId: String): Flux<PrivateChat> {
        return chatRepo.findAllByOwnerId(ownerId)
                .map { it.toModel() }
    }
}
