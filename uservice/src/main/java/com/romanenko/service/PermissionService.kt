package com.romanenko.service

import com.romanenko.connection.Permission
import com.romanenko.connection.PermissionKey
import reactor.core.publisher.Mono

interface PermissionService {
    fun getPermission(id: String, userId: String, permissionKey: PermissionKey): Mono<Permission>
}