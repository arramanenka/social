package com.romanenko.dao

import com.romanenko.model.PrivateChat
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ChatDao {
    fun getChats(ownerId: String): Flux<PrivateChat>
    fun updateChatInfo(chat: PrivateChat): Mono<Void>
}
