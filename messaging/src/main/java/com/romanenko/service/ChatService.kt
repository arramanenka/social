package com.romanenko.service

import com.romanenko.model.Message
import com.romanenko.model.PrivateChat
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ChatService {
    fun getChats(ownerId: String): Flux<PrivateChat>
    fun addLastMessageInfo(message: Message): Mono<Void>
    fun clearUnread(id: String, userId: String): Mono<Void>
}
