package com.romanenko.dao.impl.neo

import com.romanenko.connection.ConnectionType.BLACKLIST_NAME
import com.romanenko.connection.ConnectionType.FOLLOW_NAME
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

    @Query("""
match (initiator: $PRIMARY_LABEL {$ID_LABEL: $0}) with initiator
match (folowee: $PRIMARY_LABEL {$ID_LABEL: $1}) with folowee, initiator
optional match (initiator)-[bl:$BLACKLIST_NAME]->(folowee)
merge (initiator)-[con:$FOLLOW_NAME]->(folowee)
delete bl
return type(con)
""")
    fun follow(initiatorId: String, followingId: String): Mono<String>

    @Query("""
match (initiator: $PRIMARY_LABEL {$ID_LABEL: $0}) with initiator
match (blacklisted: $PRIMARY_LABEL {$ID_LABEL: $1}) with blacklisted, initiator
match (initiator)-[fl1:$FOLLOW_NAME]->(blacklisted)
match (initiator)<-[fl2:$FOLLOW_NAME]-(blacklisted)
delete fl1, fl2
merge (initiator)-[con:$BLACKLIST_NAME]->(blacklisted)
return type(con)
""")
    fun blacklist(initiatorId: String, blacklistedUser: String): Mono<String>

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
