package com.romanenko.service

import com.romanenko.model.PrivateChat
import reactor.core.publisher.Flux

interface ChatService {
    fun getChats(ownerId: String): Flux<PrivateChat>
}
