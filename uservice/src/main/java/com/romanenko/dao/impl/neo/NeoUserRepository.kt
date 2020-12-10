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
interface NeoUserRepository : ReactiveNeo4jRepository<NeoUser, String> {

    @Query("""
match (u: $PRIMARY_LABEL {$ID_LABEL: $0})
detach delete u
    """)
    fun deleteByPuId(puId: String): Mono<Boolean>

    @Query("""
match (queryingPerson: $PRIMARY_LABEL {$ID_LABEL: $0}) with queryingPerson
match (person: $PRIMARY_LABEL{$ID_LABEL: $1})
with {
$AS_NESTED_LABEL: person,
$FOLLOWER_AMOUNT_LABEL: size((person)<-[:$FOLLOW_NAME]-()),
$FOLLOWING_AMOUNT_LABEL: size((person)-[:$FOLLOW_NAME]->()),

$META_BLACKLISTED_BY_QUERYING_LABEL: exists( (queryingPerson)-[:$BLACKLIST_NAME]->(person) ),
$META_BLACKLISTED_QUERYING_LABEL: exists( (queryingPerson)<-[:$BLACKLIST_NAME]-(person) ),
$META_FOLLOWED_BY_QUERYING_LABEL: exists( (queryingPerson)-[:$FOLLOW_NAME]->(person) ),
$META_FOLLOWS_QUERYING_LABEL: exists( (queryingPerson)<-[:$FOLLOW_NAME]-(person) )
} as personAcc
return  personAcc
""")
    fun findUserById(queryingId: String, id: String): Mono<MapValue>

    @Query("""
match (person: $PRIMARY_LABEL{$ID_LABEL: $0})
with {
$AS_NESTED_LABEL: person,
$FOLLOWER_AMOUNT_LABEL: size((person)<-[:$FOLLOW_NAME]-()),
$FOLLOWING_AMOUNT_LABEL: size((person)-[:$FOLLOW_NAME]->())
} as personAcc
return  personAcc
""")
    fun findSelf(id: String): Mono<MapValue>

    @Query("""
match (queryingPerson: $PRIMARY_LABEL {$ID_LABEL: $0}) with queryingPerson
match (person:$PRIMARY_LABEL) where person.$NAME_LABEL starts with $1
with {
$AS_NESTED_LABEL: person,

$META_BLACKLISTED_BY_QUERYING_LABEL: exists( (queryingPerson)-[:$BLACKLIST_NAME]->(person) ),
$META_BLACKLISTED_QUERYING_LABEL: exists( (queryingPerson)<-[:$BLACKLIST_NAME]-(person) ),
$META_FOLLOWED_BY_QUERYING_LABEL: exists( (queryingPerson)-[:$FOLLOW_NAME]->(person) ),
$META_FOLLOWS_QUERYING_LABEL: exists( (queryingPerson)<-[:$FOLLOW_NAME]-(person) )
} as personAcc
return personAcc
skip $2 
limit $3
""")
    fun findAllByNick(id: String, nickStart: String, skipAmount: Int, amount: Int): Flux<MapValue>

    @Query("""
match (queryingPerson: $PRIMARY_LABEL {puId: $0}) with queryingPerson
match (queryingPerson)-[:FOLLOW*2..3]->(person:$PRIMARY_LABEL)
where person.puId <> queryingPerson.puId and
not (person)<-[:$FOLLOW_NAME]-(queryingPerson)
and not (person)-[:$BLACKLIST_NAME]-(queryingPerson)
with distinct person, queryingPerson
skip $1
limit $2
with {
$AS_NESTED_LABEL: person,
$CONNECTION_DEPTH: LENGTH(shortestPath((person)<-[:$FOLLOW_NAME*..3]-(queryingPerson)))
} as personAcc
return personAcc
""")
    fun findRecommendations(id: String, skipAmount: Int, amount: Int): Flux<MapValue>
}
