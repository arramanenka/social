package com.romanenko.service

import com.romanenko.connection.ConnectionType
import reactor.core.publisher.Mono

interface ConnectionService {
    fun getConnectionType(initiatorId: String, otherPersonId: String): Mono<ConnectionType>
}