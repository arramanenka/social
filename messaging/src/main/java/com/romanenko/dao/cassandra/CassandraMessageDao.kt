package com.romanenko.dao.cassandra

import com.romanenko.dao.MessageDao
import com.romanenko.io.PageQuery
import com.romanenko.model.Message
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Component
@ConditionalOnProperty(name = ["message.storage"], havingValue = "cassandra")
class CassandraMessageDao(
        private val messageRepo: MessageRepo
) : MessageDao {

    override fun sendMessage(message: Message): Mono<Message> {
        return messageRepo.save(CassandraMessage(message)).map { it.toModel() }
    }

    override fun deleteMessage(message: Message): Mono<Message> {
        return messageRepo.deleteMessage(message.senderId!!, message.receiverId!!, UUID.fromString(message.messageId))
                .map { it.toModel() }
    }

    //todo continuously check spring updates on pagination. Right now the only option is to skip manually
    override fun getMessages(queryingPerson: String, userId: String, pageQuery: PageQuery, invertedQuerying: Boolean): Flux<Message> {
        val pageable = CassandraPageRequest.first(pageQuery.fullRawQueryAmount())
        return messageRepo.findMessagesBetweenUsers(queryingPerson, userId, pageable)
                .skip(pageQuery.calculateSkipAmount().toLong())
                .map { it.toModel() }
    }
}
