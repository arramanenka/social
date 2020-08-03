package com.romanenko.handler

import com.romanenko.connection.Permission
import com.romanenko.connection.PermissionKey
import com.romanenko.io.ResponseSupplier
import com.romanenko.routing.ApiBuilder
import com.romanenko.routing.Routable
import com.romanenko.security.IdentityProvider
import com.romanenko.service.PermissionService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class PermissionHandler(
        private val identityProvider: IdentityProvider,
        private val permissionService: PermissionService,
        private val responseSupplier: ResponseSupplier
) : Routable {
    override fun declareRoute(builder: ApiBuilder) {
        builder
                .get("/permissions/{userId}/{permissionKey}", ::getPermission)
    }

    private fun getPermission(request: ServerRequest): Mono<ServerResponse> {
        val permission = identityProvider.getIdentity(request).flatMap {
            val userId = request.pathVariable("userId")
            val permissionKey = PermissionKey.forName(request.pathVariable("permissionKey"))
            if (it.id == userId) {
                return@flatMap Mono.just(Permission.GRANTED)
            } else if (permissionKey == null) {
                return@flatMap Mono.error<Permission>(HttpClientErrorException(HttpStatus.NOT_FOUND, "Permission key not found"))
            }
            permissionService.getPermission(it.id, userId, permissionKey)
        }
        return responseSupplier.ok(permission, Permission::class.java)
    }
}