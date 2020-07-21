package com.romanenko.uservice.dao.impl.user.neo;

import com.romanenko.uservice.model.User;
import org.neo4j.springframework.data.repository.ReactiveNeo4jRepository;
import org.neo4j.springframework.data.repository.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.romanenko.uservice.dao.impl.user.neo.NeoUser.*;

public interface NeoUserRepo extends ReactiveNeo4jRepository<NeoUser, String> {
    Mono<Boolean> deleteByPuId(String puId);

    @Query("match (queryingPerson: " + PRIMARY_LABEL + ") where id(queryingPerson) = ?0 with queryingPerson" +
            "match (person:" + PRIMARY_LABEL + ") where " + NAME_LABEL + " starts with ?1 AND (person)-[:blacklist*]-(queryingPerson)" +
            "return person")
    Flux<NeoUser> getAllByNickBeginning(String id, String nickStart);

    @Query("match (queryingPerson: " + PRIMARY_LABEL + ") where id(queryingPerson) = ?0 with queryingPerson" +
            "match (person:" + PRIMARY_LABEL + ") where id(person) = ?1 AND (person)-[:blacklist*]-(queryingPerson)" +
            "return person")
    Mono<User> findUserById(String queryingId, String id);

}
