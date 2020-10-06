package com.romanenko.service.impl

import com.romanenko.dao.ChatDao
import com.romanenko.model.PrivateChat
import com.romanenko.service.ChatService
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class DefaultChatService(
        private val chatDao: ChatDao
) : ChatService {

    override fun getChats(ownerId: String): Flux<PrivateChat> {
        return chatDao.getChats(ownerId)
    }
}
