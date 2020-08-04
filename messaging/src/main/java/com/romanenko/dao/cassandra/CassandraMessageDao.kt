package com.romanenko.dao.cassandra

import com.romanenko.dao.MessageDao
import com.romanenko.io.PageQuery
import com.romanenko.model.Message
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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
        return messageRepo.deleteDistinctByMessageKeyAndMessageId(MessageKey(message), UUID.fromString(message.messageId))
                .map { it.toModel() }
    }

    override fun getMessages(queryingPerson: String, userId: String, pageQuery: PageQuery): Flux<Message> {
        val pageable = PageRequest.of(pageQuery.page, pageQuery.pageSize, Sort.by(Sort.Direction.DESC, "createdAt"))
        return messageRepo.findAllByMessageKeyOrMessageKey(
                MessageKey(queryingPerson, userId),
                MessageKey(userId, queryingPerson),
                pageable
        ).map { it.toModel() }
    }
}