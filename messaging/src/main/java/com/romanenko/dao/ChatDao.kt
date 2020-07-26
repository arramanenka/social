package com.romanenko.dao

import com.romanenko.model.Chat
import reactor.core.publisher.Mono

interface ChatDao {
    fun createChat(chat: Chat): Mono<Chat>
    fun updateChat(chat: Chat): Mono<Chat>
}