package com.romanenko.dao.impl.neo;

import com.romanenko.dao.impl.neo.model.NeoUser;
import org.neo4j.springframework.data.repository.ReactiveNeo4jRepository;
import org.neo4j.springframework.data.repository.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.romanenko.connection.ConnectionType.BLACKLIST_NAME;
import static com.romanenko.dao.impl.neo.model.NeoUser.*;

public interface NeoUserRepo extends ReactiveNeo4jRepository<NeoUser, String> {
    Mono<Boolean> deleteByPuId(String puId);

    @Query("match (queryingPerson: " + PRIMARY_LABEL + " {" + ID_LABEL + ": $0}) with queryingPerson\n" +
            "match (person:" + PRIMARY_LABEL + ") where person." + NAME_LABEL +
            " starts with $1 AND (person)-[:" + BLACKLIST_NAME + "*]-(queryingPerson)\n" +
            "return person")
    Flux<NeoUser> getAllByNickBeginning(String id, String nickStart);

    @Query("match (queryingPerson: " + PRIMARY_LABEL + " {" + ID_LABEL + ": $0}) with queryingPerson\n" +
            "match (person:" + PRIMARY_LABEL + " {" + ID_LABEL + ": $1}) where (person)-[:" + BLACKLIST_NAME + "*]-(queryingPerson)\n" +
            "return person")
    Mono<NeoUser> findUserById(String queryingId, String id);

    @Query("match (person: " + PRIMARY_LABEL + "{" + ID_LABEL + ": $0}) return person")
    Mono<NeoUser> findSelf(String id);
}
