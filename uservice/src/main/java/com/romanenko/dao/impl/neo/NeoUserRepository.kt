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

    fun deleteByPuId(puId: String): Mono<Boolean>

    @Query("""
match (queryingPerson: $PRIMARY_LABEL {$ID_LABEL: $0}) with queryingPerson
match (person: $PRIMARY_LABEL{$ID_LABEL: $1})
where not (person)-[:$BLACKLIST_NAME]-(queryingPerson)
with {
$AS_NESTED_LABEL: person,
$FOLLOWER_AMOUNT_LABEL: size((person)-[:$FOLLOW_NAME]->()),
$FOLLOWING_AMOUNT_LABEL: size((person)<-[:$FOLLOW_NAME}]-())
} as personAcc
return  personAcc
""")
    fun findUserById(queryingId: String, id: String): Mono<MapValue>

    @Query("""
match (person: $PRIMARY_LABEL{$ID_LABEL: $0})
with {
$AS_NESTED_LABEL: person,
$FOLLOWER_AMOUNT_LABEL: size((person)-[:$FOLLOW_NAME]->()),
$FOLLOWING_AMOUNT_LABEL: size((person)<-[$FOLLOW_NAME]-())
} as personAcc
return  personAcc
""")
    fun findSelf(id: String): Mono<MapValue>

    @Query("""
match (queryingPerson: $PRIMARY_LABEL {$ID_LABEL: $0}) with queryingPerson
match (person:$PRIMARY_LABEL) where person.$NAME_LABEL starts with $1 AND not (person)-[:$BLACKLIST_NAME]-(queryingPerson)
return person
skip $2 
limit $3
""")
    fun getAllByNickBeginning(id: String, nickStart: String, skipAmount: Int, amount: Int): Flux<NeoUser>
}
