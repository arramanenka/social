package com.romanenko.dao.cassandra

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository

interface MessageRepo : ReactiveCassandraRepository<CassandraMessage, MessageKey>