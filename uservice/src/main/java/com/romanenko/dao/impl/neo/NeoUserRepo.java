package com.romanenko.dao.impl.neo;

import com.romanenko.dao.impl.connection.ConnectionType;
import com.romanenko.model.User;
import org.neo4j.springframework.data.repository.ReactiveNeo4jRepository;
import org.neo4j.springframework.data.repository.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NeoUserRepo extends ReactiveNeo4jRepository<NeoUser, String> {
    Mono<Boolean> deleteByPuId(String puId);

    @Query("match (queryingPerson: " + NeoUser.PRIMARY_LABEL + ") where id(queryingPerson) = $0 with queryingPerson" +
            "match (person:" + NeoUser.PRIMARY_LABEL + ") where " + NeoUser.NAME_LABEL + " starts with $1 AND (person)-[:" + ConnectionType.BLACKLIST_NAME + "*]-(queryingPerson)" +
            "return person")
    Flux<NeoUser> getAllByNickBeginning(String id, String nickStart);

    @Query("match (queryingPerson: " + NeoUser.PRIMARY_LABEL + ") where id(queryingPerson) = $0 with queryingPerson" +
            "match (person:" + NeoUser.PRIMARY_LABEL + ") where id(person) = $1 AND (person)-[:" + ConnectionType.BLACKLIST_NAME + "*]-(queryingPerson)" +
            "return person")
    Mono<User> findUserById(String queryingId, String id);

    @Query("create constraint user_name_constraint on (u:" + NeoUser.PRIMARY_LABEL + ") assert u." + NeoUser.NAME_LABEL + " is unique")
    Mono<Void> createIndexOnName();

    @Query("create constraint user_id_constraint on (u:" + NeoUser.PRIMARY_LABEL + ") assert u." + NeoUser.ID_LABEL + " is unique")
    Mono<Void> createIndexOnId();
}
