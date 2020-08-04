package com.romanenko.dao.cassandra

import com.romanenko.dao.MessageDao
import com.romanenko.io.PageQuery
import com.romanenko.model.Message
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.cassandra.config.EnableCassandraAuditing
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
@ConditionalOnProperty(name = ["message.storage"], havingValue = "cassandra")
@EnableCassandraAuditing
class CassandraMessageDao(
        private val messageRepo: MessageRepo
) : MessageDao {

    override fun sendMessage(message: Message): Mono<Message> {
        return messageRepo.save(CassandraMessage(
                MessageKey(senderId = message.senderId!!, receiverId = message.receiverId!!),
                message.text
        )).map { it.toModel() }
    }

    override fun deleteMessage(message: Message): Mono<Message> {
        TODO("Not yet implemented")
    }

    override fun getMessages(queryingPerson: String, userId: String, pageQuery: PageQuery): Flux<Message> {
        TODO("Not yet implemented")
    }
}