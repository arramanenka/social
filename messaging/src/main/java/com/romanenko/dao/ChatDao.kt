package com.romanenko.dao

import com.romanenko.model.ChatsMetaInf
import com.romanenko.model.Message
import com.romanenko.model.PrivateChat
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ChatDao {
    fun getChats(ownerId: String): Flux<PrivateChat>
    fun addLastMessageInfo(message: Message): Mono<Void>
    fun clearUnread(id: String, userId: String): Mono<Void>
    fun clearUnread(id: String, userId: String, amount: Int): Mono<Long>
    fun getChat(id: String, interlocutorId: String): Mono<PrivateChat>
    fun getMetaInf(id: String): Mono<ChatsMetaInf>
}
