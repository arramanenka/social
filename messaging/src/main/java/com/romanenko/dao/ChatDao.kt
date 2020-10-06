package com.romanenko.dao

import com.romanenko.model.PrivateChat
import reactor.core.publisher.Flux

interface ChatDao {
    fun getChats(ownerId: String): Flux<PrivateChat>
}
