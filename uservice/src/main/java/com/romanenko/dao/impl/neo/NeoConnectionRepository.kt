package com.romanenko.dao.impl.neo

import com.romanenko.connection.ConnectionType.*
import com.romanenko.dao.impl.neo.model.NeoUser
import com.romanenko.dao.impl.neo.model.NeoUser.ID_LABEL
import com.romanenko.dao.impl.neo.model.NeoUser.PRIMARY_LABEL
import org.neo4j.springframework.data.repository.ReactiveNeo4jRepository
import org.neo4j.springframework.data.repository.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
interface NeoConnectionRepository : ReactiveNeo4jRepository<NeoUser, String> {
    //TODO needs rewriting
    @Query("""
match (initiator: $PRIMARY_LABEL {$ID_LABEL: $0}) with initiator
match (folowee: $PRIMARY_LABEL {$ID_LABEL: $1}) with folowee, initiator
merge (initiator)-[con:$CONNECTION_NAME]->(folowee)
on create set con.$CONNECTION_TYPE_LABEL="$FOLLOW_NAME"
on match set con.$CONNECTION_TYPE_LABEL="$FOLLOW_NAME"
""")
    fun follow(initiatorId: String, followingId: String): Mono<Void>

    //TODO needs rewriting
    @Query("""
match (initiator: $PRIMARY_LABEL {$ID_LABEL: $0}) with initiator
match (blacklisted: $PRIMARY_LABEL {$ID_LABEL: $1}) with blacklisted, initiator
merge (initiator)-[con:$CONNECTION_NAME]->(blacklisted)
on create set con.$CONNECTION_TYPE_LABEL="$BLACKLIST_NAME"
on match set con.$CONNECTION_TYPE_LABEL="$BLACKLIST_NAME"
""")
    fun blacklist(initiatorId: String, blacklistedUser: String): Mono<Void>

    @Query("""
match (initiator: $PRIMARY_LABEL {$ID_LABEL: $0}) with initiator
match (folowee: $PRIMARY_LABEL {$ID_LABEL: $1}) with folowee, initiator
match (initiator)-[con:$FOLLOW_NAME]->(folowee)
delete con
""")
    fun unfollow(initiatorId: String, followingId: String): Mono<Void>

    @Query("""
match (initiator: $PRIMARY_LABEL {$ID_LABEL: $0}) with initiator
match (blacklisted: $PRIMARY_LABEL {$ID_LABEL: $1}) with blacklisted, initiator
match (initiator)-[con:$BLACKLIST_NAME]->(blacklisted)
delete con
""")
    fun removeFromBlacklist(initiatorId: String, blacklistedUser: String): Mono<Void>

    @Query("""
match (initiator: $PRIMARY_LABEL {$ID_LABEL: $0}) with initiator
match (initiator)-[:$BLACKLIST_NAME]->(blacklisted:$PRIMARY_LABEL)
return blacklisted
skip $1 limit $2
""")
    fun getBlacklist(id: String, skipAmount: Int, amount: Int): Flux<NeoUser>

    @Query("""
match (initiator: $PRIMARY_LABEL {$ID_LABEL: $0}) with initiator
match (initiator)<-[:$FOLLOW_NAME]-(follower:$PRIMARY_LABEL)
return follower
skip $1 limit $2
""")
    fun getFollowers(id: String, skipAmount: Int, amount: Int): Flux<NeoUser>

    @Query("""
match (initiator: $PRIMARY_LABEL {$ID_LABEL: $0}) with initiator
match (initiator)-[:$FOLLOW_NAME]->(following:$PRIMARY_LABEL)
return following
skip $1 limit $2
""")
    fun getFollowing(id: String, skipAmount: Int, amount: Int): Flux<NeoUser>

    //TODO needs rewriting
    @Query("""
match (initiator: $PRIMARY_LABEL {$ID_LABEL: $0}) with initiator
match (followee: $PRIMARY_LABEL {$ID_LABEL: $1}) with followee, initiator
match (initiator)-[con]->(followee)
return con
""")
    fun getConnection(initiatorId: String, otherPersonId: String): Mono<String>
}
