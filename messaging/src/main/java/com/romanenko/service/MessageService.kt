package com.romanenko.service

import com.romanenko.io.PageQuery
import com.romanenko.model.Message
import com.romanenko.security.Identity
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface MessageService {
    fun sendMessage(message: Mono<Message>): Mono<Message>
    fun deleteMessage(message: Message): Mono<Message>
    fun getMessages(identity: Identity, userId: String, pageQuery: PageQuery): Flux<Message>

    /**
     * @return unread messages with inverted sort (from latest to most recent)
     */
    fun getUnread(identity: Identity, userId: String, pageQuery: PageQuery): Flux<Message>
}
