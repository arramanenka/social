package com.romanenko.dao.impl.neo

import com.romanenko.connection.ConnectionType.BLACKLIST_NAME
import com.romanenko.connection.ConnectionType.FOLLOW_NAME
import com.romanenko.dao.impl.neo.model.NeoUser
import com.romanenko.dao.impl.neo.model.NeoUser.*
import org.neo4j.driver.internal.value.MapValue
import org.neo4j.springframework.data.repository.ReactiveNeo4jRepository
import org.neo4j.springframework.data.repository.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
interface NeoConnectionRepository : ReactiveNeo4jRepository<NeoUser, String> {

    @Query("""
match (initiator: $PRIMARY_LABEL {$ID_LABEL: $0})
match (folowee: $PRIMARY_LABEL {$ID_LABEL: $1})
optional match (initiator)-[bl:$BLACKLIST_NAME]->(folowee)
merge (initiator)-[con:$FOLLOW_NAME]->(folowee)
delete bl
return type(con)
""")
    fun follow(initiatorId: String, followingId: String): Mono<String>

    @Query("""
match (initiator: $PRIMARY_LABEL {$ID_LABEL: $0})
match (blacklisted: $PRIMARY_LABEL {$ID_LABEL: $1})
optional match (initiator)-[fl1:$FOLLOW_NAME]->(blacklisted)
optional match (initiator)<-[fl2:$FOLLOW_NAME]-(blacklisted)
delete fl1, fl2
merge (initiator)-[con:$BLACKLIST_NAME]->(blacklisted)
return type(con)
""")
    fun blacklist(initiatorId: String, blacklistedUser: String): Mono<String>

    @Query("""
match (initiator: $PRIMARY_LABEL {$ID_LABEL: $0})
match (folowee: $PRIMARY_LABEL {$ID_LABEL: $1})
match (initiator)-[con:$FOLLOW_NAME]->(folowee)
delete con
""")
    fun unfollow(initiatorId: String, followingId: String): Mono<Void>

    @Query("""
match (initiator: $PRIMARY_LABEL {$ID_LABEL: $0})
match (blacklisted: $PRIMARY_LABEL {$ID_LABEL: $1})
match (initiator)-[con:$BLACKLIST_NAME]->(blacklisted)
delete con
""")
    fun removeFromBlacklist(initiatorId: String, blacklistedUser: String): Mono<Void>

    @Query("""
match (initiator: $PRIMARY_LABEL {$ID_LABEL: $0})
match (initiator)-[:$BLACKLIST_NAME]->(blacklisted:$PRIMARY_LABEL)
with {
$AS_NESTED_LABEL: blacklisted,
$META_BLACKLISTED_BY_QUERYING_LABEL: true,
$META_BLACKLISTED_QUERYING_LABEL: exists( (initiator)<-[:$BLACKLIST_NAME]-(blacklisted) ),
$META_FOLLOWED_BY_QUERYING_LABEL: exists( (initiator)-[:$FOLLOW_NAME]->(blacklisted) ),
$META_FOLLOWS_QUERYING_LABEL: exists( (initiator)<-[:$FOLLOW_NAME]-(blacklisted) )
} as blacklistedAcc
return blacklistedAcc
skip $1 limit $2
""")
    fun getBlacklist(id: String, skipAmount: Int, amount: Int): Flux<MapValue>

    @Query("""
match (initiator: $PRIMARY_LABEL {$ID_LABEL: $0})
match (initiator)<-[:$FOLLOW_NAME]-(follower:$PRIMARY_LABEL)
with {
$AS_NESTED_LABEL: follower,
$META_FOLLOWED_BY_QUERYING_LABEL: exists( (initiator)-[:$FOLLOW_NAME]->(follower) ),
$META_FOLLOWS_QUERYING_LABEL: true
} as followerAcc
return followerAcc
skip $1 limit $2
""")
    fun getOwnFollowers(id: String, skipAmount: Int, amount: Int): Flux<MapValue>

    @Query("""
match (queryingPerson: $PRIMARY_LABEL {$ID_LABEL: $0})
match (queryedPerson: $PRIMARY_LABEL {$ID_LABEL: $1})<-[:$FOLLOW_NAME]-(follower:$PRIMARY_LABEL)
with {
$AS_NESTED_LABEL: follower,
$META_BLACKLISTED_BY_QUERYING_LABEL: exists( (queryingPerson)-[:$BLACKLIST_NAME]->(follower) ),
$META_BLACKLISTED_QUERYING_LABEL: exists( (queryingPerson)<-[:$BLACKLIST_NAME]-(follower) ),
$META_FOLLOWED_BY_QUERYING_LABEL: exists( (queryingPerson)-[:$FOLLOW_NAME]->(follower) ),
$META_FOLLOWS_QUERYING_LABEL: exists( (queryingPerson)<-[:$FOLLOW_NAME]-(follower) )
} as followerAcc
return followerAcc
skip $2 limit $3
""")
    fun getFollowers(queryingPerson: String, id: String, skipAmount: Int, amount: Int): Flux<MapValue>

    @Query("""
match (queryingPerson: $PRIMARY_LABEL {$ID_LABEL: $0})
match (queryingPerson)-[:$FOLLOW_NAME]->(following:$PRIMARY_LABEL)
with {
$AS_NESTED_LABEL: following,
$META_FOLLOWED_BY_QUERYING_LABEL: true,
$META_FOLLOWS_QUERYING_LABEL: exists( (queryingPerson)<-[:$FOLLOW_NAME]-(following) )
} as followingAcc
return followingAcc
skip $1 limit $2
""")
    fun getOwnFollowing(id: String, skipAmount: Int, amount: Int): Flux<MapValue>

    @Query("""
match (queryingPerson: $PRIMARY_LABEL {$ID_LABEL: $0})
match (queryedPerson: $PRIMARY_LABEL {$ID_LABEL: $1})-[:$FOLLOW_NAME]->(following:$PRIMARY_LABEL)
with {
$AS_NESTED_LABEL: following,
$META_BLACKLISTED_BY_QUERYING_LABEL: exists( (queryingPerson)-[:$BLACKLIST_NAME]->(following) ),
$META_BLACKLISTED_QUERYING_LABEL: exists( (queryingPerson)<-[:$BLACKLIST_NAME]-(following) ),
$META_FOLLOWED_BY_QUERYING_LABEL: exists( (queryingPerson)-[:$FOLLOW_NAME]->(following) ),
$META_FOLLOWS_QUERYING_LABEL: exists( (queryingPerson)<-[:$FOLLOW_NAME]-(following) )
} as followingAcc
return followingAcc
skip $2 limit $3
""")
    fun getFollowing(queryingPerson: String, id: String, skipAmount: Int, amount: Int): Flux<MapValue>

    @Query("""
match (initiator: $PRIMARY_LABEL {$ID_LABEL: $0})
match (followee: $PRIMARY_LABEL {$ID_LABEL: $1})
match (initiator)-[con]->(followee)
return type(con)
""")
    fun getConnection(initiatorId: String, otherPersonId: String): Mono<String>
}
