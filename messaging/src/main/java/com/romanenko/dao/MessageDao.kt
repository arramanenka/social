package com.romanenko.dao

import com.romanenko.io.PageQuery
import com.romanenko.model.Message
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface MessageDao {
    fun sendMessage(message: Message): Mono<Message>
    fun deleteMessage(message: Message): Mono<Message>
    fun getMessages(queryingPerson: String, userId: String, pageQuery: PageQuery): Flux<Message>
}