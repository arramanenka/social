package com.romanenko.dao.impl.neo;

import com.romanenko.dao.impl.connection.ConnectionType;
import com.romanenko.model.User;
import org.neo4j.springframework.data.repository.ReactiveNeo4jRepository;
import org.neo4j.springframework.data.repository.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NeoUserRepo extends ReactiveNeo4jRepository<NeoUser, String> {
    Mono<Boolean> deleteByPuId(String puId);

    @Query("match (queryingPerson: " + NeoUser.PRIMARY_LABEL + " {" + NeoUser.ID_LABEL + ": '$0'}) with queryingPerson\n" +
            "match (person:" + NeoUser.PRIMARY_LABEL + ") where person." + NeoUser.NAME_LABEL +
            " starts with '$1' AND (person)-[:" + ConnectionType.BLACKLIST_NAME + "*]-(queryingPerson)\n" +
            "return person")
    Flux<NeoUser> getAllByNickBeginning(String id, String nickStart);

    @Query("match (queryingPerson: " + NeoUser.PRIMARY_LABEL + " {" + NeoUser.ID_LABEL + ": '$0'}) with queryingPerson\n" +
            "match (person:" + NeoUser.PRIMARY_LABEL + " {" + NeoUser.ID_LABEL + ": '$1'}) where (person)-[:" + ConnectionType.BLACKLIST_NAME + "*]-(queryingPerson)\n" +
            "return person")
    Mono<User> findUserById(String queryingId, String id);
}