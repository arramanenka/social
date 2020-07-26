package com.romanenko.dao.mongo

import com.romanenko.dao.MessageDao
import com.romanenko.model.Message
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MongoMessageDao : MessageDao {
    override fun saveMessage(message: Message): Mono<Message> {
        TODO("Not yet implemented")
    }
}