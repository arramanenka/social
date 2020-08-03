package com.romanenko.service.neo

import com.romanenko.connection.ConnectionType
import com.romanenko.connection.Permission
import com.romanenko.connection.PermissionKey
import com.romanenko.connection.UserConnectionCache
import com.romanenko.dao.DirectConnectionDao
import com.romanenko.service.PermissionService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
class NeoPermissionService(
        private val directConnectionDao: DirectConnectionDao,
        private val connectionCache: UserConnectionCache
) : PermissionService {

    override fun getPermission(id: String, userId: String, permissionKey: PermissionKey): Mono<Permission> {
        return connectionCache.getPermission(id, userId, permissionKey)
                .switchIfEmpty(getNonCachedPermission(id, userId, permissionKey))
    }

    private fun getNonCachedPermission(id: String, userId: String, permissionKey: PermissionKey): Mono<Permission> {
        return directConnectionDao.getRelations(id, userId)
                // in real world scenario, users have different settings for different types of activities.
                // For now lets assume that you just don't have to be blocked for any type of permission key
                .map {
                    println("searching")
                    if (it == ConnectionType.BLACKLIST) {
                        return@map Permission.BLOCKED
                    }
                    Permission.GRANTED
                }.doOnSuccess {
                    connectionCache.savePermission(id, userId, permissionKey, it).subscribeOn(Schedulers.parallel()).subscribe()
                }
    }
}