package com.romanenko.dao

import com.romanenko.model.Chat
import com.romanenko.security.Identity
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ChatDao {
    fun createChat(chat: Chat): Mono<Chat>
    fun updateChat(chat: Chat): Mono<Chat>
    fun deleteChat(identity: Identity, chatId: Int): Mono<Void>
    fun addMember(identity: Identity, chatId: Int, userId: String): Mono<Void>
    fun removeMember(identity: Identity, chatId: Int, userId: String): Mono<Void>
    fun getOwnChats(identity: Identity): Flux<Chat>
}