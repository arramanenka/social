package com.romanenko.handler

import com.romanenko.connection.Permission
import com.romanenko.connection.PermissionKey
import com.romanenko.io.ResponseSupplier
import com.romanenko.routing.ApiBuilder
import com.romanenko.routing.Routable
import com.romanenko.service.PermissionService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class PermissionHandler(
        private val permissionService: PermissionService,
        private val responseSupplier: ResponseSupplier
) : Routable {
    override fun declareRoute(builder: ApiBuilder) {
        builder
                .get("/permissions/{queryingUserId}/{userId}/{permissionKey}", ::getPermission)
    }

    private fun getPermission(request: ServerRequest): Mono<ServerResponse> {
        val queryingUserId = request.pathVariable("queryingUserId")
        val userId = request.pathVariable("userId")
        val permissionKey = PermissionKey.forName(request.pathVariable("permissionKey"))
        if (queryingUserId == userId) {
            return responseSupplier.ok(Mono.just(Permission.GRANTED), Permission::class.java)
        } else if (permissionKey == null) {
            return responseSupplier.notFound("Permission key not found")
        }
        val permission = permissionService.getPermission(queryingUserId, userId, permissionKey)
        return responseSupplier.ok(permission, Permission::class.java)
    }
}