package com.romanenko.dao.impl.neo;

import com.romanenko.model.User;
import org.neo4j.springframework.data.repository.ReactiveNeo4jRepository;
import org.neo4j.springframework.data.repository.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.romanenko.dao.impl.connection.ConnectionType.*;
import static com.romanenko.dao.impl.neo.NeoUser.PRIMARY_LABEL;

public interface NeoConnectionRepo extends ReactiveNeo4jRepository<NeoUser, String> {

    @Query("match (initiator: " + PRIMARY_LABEL + ") where id(initiator) = $0 with initiator\n" +
            "match (folowee: " + PRIMARY_LABEL + ") where id(folowee) = $1 with folowee, initiator\n" +
            "merge (initiator)-[con:" + CONNECTION_NAME + "]->(folowee)\n" +
            "on create set con." + CONNECTION_TYPE_LABEL + "=\"" + FOLLOW_NAME + "\"\n" +
            "on match set con." + CONNECTION_TYPE_LABEL + "=\"" + FOLLOW_NAME + "\"")
    Mono<Void> follow(String initiatorId, String followingId);

    @Query("match (initiator: " + PRIMARY_LABEL + ") where id(initiator) = $0 with initiator\n" +
            "match (folowee: " + PRIMARY_LABEL + ") where id(folowee) = $1 with folowee, initiator\n" +
            "match (initiator)-[con:" + CONNECTION_NAME + "{type: " + FOLLOW_NAME + "}]->(folowee)\n" +
            "delete con")
    Mono<Void> unfollow(String initiatorId, String followingId);

    @Query("match (initiator: " + PRIMARY_LABEL + ") where id(initiator) = $0 with initiator\n" +
            "match (blacklisted: " + PRIMARY_LABEL + ") where id(blacklisted) = $1 with initiator, blacklisted\n" +
            "merge (initiator)-[con:" + CONNECTION_NAME + "]->(blacklisted)\n" +
            "on create set con." + CONNECTION_TYPE_LABEL + "=\"" + BLACKLIST_NAME + "\"\n" +
            "on match set con." + CONNECTION_TYPE_LABEL + "=\"" + BLACKLIST_NAME + "\"")
    Mono<Void> blacklist(String initiatorId, String blacklistedUser);

    @Query("match (initiator: " + PRIMARY_LABEL + ") where id(initiator) = $0 with initiator\n" +
            "match (blacklisted: " + PRIMARY_LABEL + ") where id(blacklisted) = $1 with blacklisted, initiator\n" +
            "match (initiator)-[con:" + CONNECTION_NAME + "{" + CONNECTION_TYPE_LABEL + ": " + BLACKLIST_NAME + "}]->(blacklisted)\n" +
            "delete con")
    Mono<Void> removeFromBlacklist(String initiatorId, String blacklistedUser);

    @Query("match (initiator: " + PRIMARY_LABEL + ") where id(initiator) = $0 with initiator\n" +
            "match (initiator)-[:" + CONNECTION_NAME + "{" + CONNECTION_TYPE_LABEL + ": " + BLACKLIST_NAME + "}]->(blacklisted:" + PRIMARY_LABEL + ")\n" +
            "return blacklisted")
    Flux<User> getBlacklist(String id);

    @Query("match (person: " + PRIMARY_LABEL + ") where id(person) = $0 with person" +
            "match (person)<-[:" + CONNECTION_NAME + "{" + CONNECTION_TYPE_LABEL + ": " + FOLLOW_NAME + "}]-(follower:" + PRIMARY_LABEL + ")\n" +
            "return follower")
    Flux<User> getFollowers(String id);

    @Query("match (person: " + PRIMARY_LABEL + ") where id(person) = $0 with person" +
            "match (person)-[:" + CONNECTION_NAME + "{" + CONNECTION_TYPE_LABEL + ": " + FOLLOW_NAME + "}]->(following:" + PRIMARY_LABEL + ")\n" +
            "return following")
    Flux<User> getFollowing(String id);

    @Query("match (initiator: " + PRIMARY_LABEL + ") where id(initiator) = $0 with initiator\n" +
            "match (followee: " + PRIMARY_LABEL + ") where id(followee) = $1 with followee, initiator\n" +
            "match (initiator)-[con:" + CONNECTION_NAME + "}]->(followee)\n" +
            "return con")
    Mono<String> getConnection(String initiatorId, String otherPersonId);
}
