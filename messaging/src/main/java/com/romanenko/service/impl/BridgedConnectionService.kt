package com.romanenko.service.impl

import com.romanenko.connection.ConnectionType
import com.romanenko.connection.UserConnectionCache
import com.romanenko.service.ConnectionService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class BridgedConnectionService(
        private val userConnectionCache: UserConnectionCache,
        @Value("\${uservice.host}")
        private val userviceHost: String
) : ConnectionService {
    private val webclient: WebClient = WebClient.create(userviceHost)

    override fun getConnectionType(initiatorId: String, otherPersonId: String): Mono<ConnectionType> {
        return userConnectionCache.getCachedConnectionType(initiatorId, otherPersonId)
                .switchIfEmpty(Mono.defer { getConnectionTypeFromUservice(initiatorId, otherPersonId) })
    }

    private fun getConnectionTypeFromUservice(initiatorId: String, otherPersonId: String): Mono<ConnectionType> {
        return webclient.get()
                .uri("users/connection/{initiatorId}/{otherPersonId}", initiatorId, otherPersonId)
                .exchange()
                .flatMap { it.bodyToMono(ConnectionType::class.java) }
    }
}