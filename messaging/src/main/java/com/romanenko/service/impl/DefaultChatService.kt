package com.romanenko.service.impl

import com.romanenko.dao.ChatDao
import com.romanenko.model.Message
import com.romanenko.model.PrivateChat
import com.romanenko.service.ChatService
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class DefaultChatService(
        private val chatDao: ChatDao
) : ChatService {

    override fun getChats(ownerId: String): Flux<PrivateChat> {
        return chatDao.getChats(ownerId)
    }

    override fun addLastMessageInfo(message: Message): Mono<Void> {
        return chatDao.addLastMessageInfo(message)
    }

    override fun clearUnread(id: String, userId: String): Mono<Void> {
        return chatDao.clearUnread(id, userId)
    }
}
