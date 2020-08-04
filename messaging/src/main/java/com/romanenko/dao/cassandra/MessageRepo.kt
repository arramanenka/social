package com.romanenko.dao.cassandra

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.data.domain.Pageable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@ConditionalOnProperty(name = ["message.storage"], havingValue = "cassandra")
interface MessageRepo : ReactiveCassandraRepository<CassandraMessage, MessageKey> {

    fun deleteDistinctByMessageKeyAndMessageId(messageKey: MessageKey, messageId: UUID): Mono<CassandraMessage>

    fun findAllByMessageKeyOrMessageKey(messageKey: MessageKey, otherMessageKey: MessageKey, pageable: Pageable): Flux<CassandraMessage>
}