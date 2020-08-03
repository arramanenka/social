package com.romanenko.service

import com.romanenko.connection.Permission
import com.romanenko.connection.PermissionKey
import reactor.core.publisher.Mono

interface ConnectionService {
    fun getPermission(initiatorId: String, otherPersonId: String, permissionKey: PermissionKey): Mono<Permission>
}