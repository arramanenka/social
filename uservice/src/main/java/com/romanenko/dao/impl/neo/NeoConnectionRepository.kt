package com.romanenko.dao.impl.neo

import com.romanenko.connection.ConnectionType
import com.romanenko.dao.impl.neo.model.NeoUser
import org.neo4j.springframework.data.repository.ReactiveNeo4jRepository
import org.neo4j.springframework.data.repository.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
interface NeoConnectionRepository : ReactiveNeo4jRepository<NeoUser, String> {
    @Query("""
match (initiator: ${NeoUser.PRIMARY_LABEL} {${NeoUser.ID_LABEL}: $0}) with initiator
match (folowee: ${NeoUser.PRIMARY_LABEL} {${NeoUser.ID_LABEL}: $1}) with folowee, initiator
merge (initiator)-[con:${ConnectionType.CONNECTION_NAME}]->(folowee)
on create set con.${ConnectionType.CONNECTION_TYPE_LABEL}="${ConnectionType.FOLLOW_NAME}"
on match set con.${ConnectionType.CONNECTION_TYPE_LABEL}="${ConnectionType.FOLLOW_NAME}"
""")
    fun follow(initiatorId: String, followingId: String): Mono<Void>

    @Query("""
match (initiator: ${NeoUser.PRIMARY_LABEL} {${NeoUser.ID_LABEL}: $0}) with initiator
match (folowee: ${NeoUser.PRIMARY_LABEL} {${NeoUser.ID_LABEL}: $1}) with folowee, initiator
match (initiator)-[con:${ConnectionType.CONNECTION_NAME}{type: ${ConnectionType.FOLLOW_NAME}}]->(folowee)
delete con
""")
    fun unfollow(initiatorId: String, followingId: String): Mono<Void>

    @Query("""
match (initiator: ${NeoUser.PRIMARY_LABEL} {${NeoUser.ID_LABEL}: $0}) with initiator
match (blacklisted: ${NeoUser.PRIMARY_LABEL} {${NeoUser.ID_LABEL}: $1}) with blacklisted, initiator
merge (initiator)-[con:${ConnectionType.CONNECTION_NAME}]->(blacklisted)
on create set con.${ConnectionType.CONNECTION_TYPE_LABEL}="${ConnectionType.BLACKLIST_NAME}"
on match set con.${ConnectionType.CONNECTION_TYPE_LABEL}="${ConnectionType.BLACKLIST_NAME}"
""")
    fun blacklist(initiatorId: String, blacklistedUser: String): Mono<Void>

    @Query("""
match (initiator: ${NeoUser.PRIMARY_LABEL} {${NeoUser.ID_LABEL}: $0}) with initiator
match (blacklisted: ${NeoUser.PRIMARY_LABEL} {${NeoUser.ID_LABEL}: $1}) with blacklisted, initiator
match (initiator)-[con:${ConnectionType.CONNECTION_NAME}{${ConnectionType.CONNECTION_TYPE_LABEL}: "${ConnectionType.BLACKLIST_NAME}"}]->(blacklisted)
delete con
""")
    fun removeFromBlacklist(initiatorId: String, blacklistedUser: String): Mono<Void>

    @Query("""
match (initiator: ${NeoUser.PRIMARY_LABEL} {${NeoUser.ID_LABEL}: $0}) with initiator
match (initiator)-[:${ConnectionType.CONNECTION_NAME}{${ConnectionType.CONNECTION_TYPE_LABEL}: "${ConnectionType.BLACKLIST_NAME}"}]->(blacklisted:${NeoUser.PRIMARY_LABEL})
return blacklisted
skip $1 limit $2
""")
    fun getBlacklist(id: String, skipAmount: Int, amount: Int): Flux<NeoUser>

    @Query("""
match (initiator: ${NeoUser.PRIMARY_LABEL} {${NeoUser.ID_LABEL}: $0}) with initiator
match (initiator)<-[:${ConnectionType.CONNECTION_NAME}{${ConnectionType.CONNECTION_TYPE_LABEL}: "${ConnectionType.FOLLOW_NAME}"}]-(follower:${NeoUser.PRIMARY_LABEL})
return follower
skip $1 limit $2
""")
    fun getFollowers(id: String, skipAmount: Int, amount: Int): Flux<NeoUser>

    @Query("""
match (initiator: ${NeoUser.PRIMARY_LABEL} {${NeoUser.ID_LABEL}: $0}) with initiator
match (initiator)-[:${ConnectionType.CONNECTION_NAME}{${ConnectionType.CONNECTION_TYPE_LABEL}: "${ConnectionType.FOLLOW_NAME}"}]->(following:${NeoUser.PRIMARY_LABEL})
return following
skip $1 limit $2
""")
    fun getFollowing(id: String, skipAmount: Int, amount: Int): Flux<NeoUser>

    @Query("""
match (initiator: ${NeoUser.PRIMARY_LABEL} {${NeoUser.ID_LABEL}: $0}) with initiator
match (followee: ${NeoUser.PRIMARY_LABEL} {${NeoUser.ID_LABEL}: $1}) with followee, initiator
match (initiator)-[con:${ConnectionType.CONNECTION_NAME}]->(followee)
return con.type
""")
    fun getConnection(initiatorId: String, otherPersonId: String): Mono<String>
}
