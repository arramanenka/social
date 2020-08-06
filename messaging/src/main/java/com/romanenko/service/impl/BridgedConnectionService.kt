package com.romanenko.service.impl

import com.romanenko.connection.Permission
import com.romanenko.connection.PermissionKey
import com.romanenko.connection.UserConnectionCache
import com.romanenko.service.ConnectionService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class BridgedConnectionService(
        private val userConnectionCache: UserConnectionCache,
        @Value("\${uservice.host}")
        private val userviceHost: String,
        @Value("\${uservice.port}")
        private val uservicePort: String
) : ConnectionService {
    private val webclient: WebClient = WebClient.create("http://$userviceHost:$uservicePort")

    override fun getPermission(initiatorId: String, otherPersonId: String, permissionKey: PermissionKey): Mono<Permission> {
        return userConnectionCache.getPermission(initiatorId, otherPersonId, permissionKey)
                .switchIfEmpty(Mono.defer { getPermissionUservice(initiatorId, otherPersonId, permissionKey) })
    }

    private fun getPermissionUservice(initiatorId: String, otherPersonId: String, permissionKey: PermissionKey): Mono<Permission> {
        return webclient.get()
                .uri("/permissions/{queryingUserId}/{userId}/{permissionKey}", initiatorId, otherPersonId, permissionKey.key)
                .exchange()
                .flatMap {
                    val statusCode = it.statusCode()
                    if (statusCode.isError) {
                        return@flatMap Mono.error<Permission>(HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR))
                    }
                    it.bodyToMono(Permission::class.java)
                }
    }
}