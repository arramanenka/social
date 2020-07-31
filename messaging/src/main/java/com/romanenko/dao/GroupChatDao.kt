package com.romanenko.dao

import com.romanenko.model.GroupChat
import com.romanenko.security.Identity
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface GroupChatDao {
    fun findChat(userId: String, chatId: String): Mono<GroupChat>
    fun createChat(groupChat: GroupChat): Mono<GroupChat>
    fun updateChat(groupChat: GroupChat): Mono<GroupChat>
    fun deleteChat(identity: Identity, chatId: String): Mono<Void>
    fun inviteMember(identity: Identity, chatId: String, userId: String): Mono<Void>
    fun removeInvitation(identity: Identity, chatId: String, userId: String): Mono<Void>
    fun getOwnChats(identity: Identity): Flux<GroupChat>
}