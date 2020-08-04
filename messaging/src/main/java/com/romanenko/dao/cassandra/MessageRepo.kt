package com.romanenko.dao.cassandra

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository

@ConditionalOnProperty(name = ["message.storage"], havingValue = "cassandra")
interface MessageRepo : ReactiveCassandraRepository<CassandraMessage, MessageKey>