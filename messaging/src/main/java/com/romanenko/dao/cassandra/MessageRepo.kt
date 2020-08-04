package com.romanenko.dao.cassandra

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@ConditionalOnProperty(name = ["message.storage"], havingValue = "cassandra")
interface MessageRepo : ReactiveCassandraRepository<CassandraMessage, MessageKey> {

    @Query("delete from cassandramessage where receiverid = ?0 and senderid  = ?1 and messageid=?2")
    fun deleteMessage(senderId: String, receiverId: String, messageId: UUID): Mono<CassandraMessage>

    @Query("select * from cassandramessage where receiverid in (?0, ?1) and senderid in (?0, ?1)")
    fun findMessagesBetweenUsers(queryingUser: String, userId: String, pageable: CassandraPageRequest): Flux<CassandraMessage>
}