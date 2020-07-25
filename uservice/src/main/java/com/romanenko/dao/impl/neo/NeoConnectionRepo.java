package com.romanenko.dao.impl.neo;

import com.romanenko.model.User;
import org.neo4j.springframework.data.repository.ReactiveNeo4jRepository;
import org.neo4j.springframework.data.repository.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.romanenko.connection.ConnectionType.*;
import static com.romanenko.dao.impl.neo.NeoUser.*;

public interface NeoConnectionRepo extends ReactiveNeo4jRepository<NeoUser, String> {

    @Query("match (initiator: " + PRIMARY_LABEL + " {" + ID_LABEL + ": $0}) with initiator\n" +
            "match (folowee: " + PRIMARY_LABEL + " {" + ID_LABEL + ": $1}) with folowee, initiator\n" +
            "merge (initiator)-[con:" + CONNECTION_NAME + "]->(folowee)\n" +
            "on create set con." + CONNECTION_TYPE_LABEL + "=\"" + FOLLOW_NAME + "\"\n" +
            "on match set con." + CONNECTION_TYPE_LABEL + "=\"" + FOLLOW_NAME + "\"")
    Mono<Void> follow(String initiatorId, String followingId);

    @Query("match (initiator: " + PRIMARY_LABEL + " {" + ID_LABEL + ": $0}) with initiator\n" +
            "match (folowee: " + PRIMARY_LABEL + " {" + ID_LABEL + ": $1}) with folowee, initiator\n" +
            "match (initiator)-[con:" + CONNECTION_NAME + "{type: " + FOLLOW_NAME + "}]->(folowee)\n" +
            "delete con")
    Mono<Void> unfollow(String initiatorId, String followingId);

    @Query("match (initiator: " + PRIMARY_LABEL + " {" + ID_LABEL + ": $0}) with initiator\n" +
            "match (blacklisted: " + PRIMARY_LABEL + " {" + ID_LABEL + ": $1}) with blacklisted, initiator\n" +
            "merge (initiator)-[con:" + CONNECTION_NAME + "]->(blacklisted)\n" +
            "on create set con." + CONNECTION_TYPE_LABEL + "=\"" + BLACKLIST_NAME + "\"\n" +
            "on match set con." + CONNECTION_TYPE_LABEL + "=\"" + BLACKLIST_NAME + "\"")
    Mono<Void> blacklist(String initiatorId, String blacklistedUser);

    @Query("match (initiator: " + PRIMARY_LABEL + " {" + ID_LABEL + ": $0}) with initiator\n" +
            "match (blacklisted: " + PRIMARY_LABEL + " {" + ID_LABEL + ": $1}) with blacklisted, initiator\n" +
            "match (initiator)-[con:" + CONNECTION_NAME + "{" + CONNECTION_TYPE_LABEL + ": \"" + BLACKLIST_NAME + "\"}]->(blacklisted)\n" +
            "delete con")
    Mono<Void> removeFromBlacklist(String initiatorId, String blacklistedUser);

    @Query("match (initiator: " + PRIMARY_LABEL + " {" + ID_LABEL + ": $0}) with initiator\n" +
            "match (initiator)-[:" + CONNECTION_NAME + "{" + CONNECTION_TYPE_LABEL + ": \"" + BLACKLIST_NAME + "\"}]->(blacklisted:" + PRIMARY_LABEL + ")\n" +
            "return blacklisted")
    Flux<User> getBlacklist(String id);

    @Query("match (initiator: " + PRIMARY_LABEL + " {" + ID_LABEL + ": $0}) with initiator\n" +
            "match (initiator)<-[:" + CONNECTION_NAME + "{" + CONNECTION_TYPE_LABEL + ": \"" + FOLLOW_NAME + "\"}]-(follower:" + PRIMARY_LABEL + ")\n" +
            "return follower")
    Flux<User> getFollowers(String id);

    @Query("match (initiator: " + PRIMARY_LABEL + " {" + ID_LABEL + ": $0}) with initiator\n" +
            "match (initiator)-[:" + CONNECTION_NAME + "{" + CONNECTION_TYPE_LABEL + ": \"" + FOLLOW_NAME + "\"}]->(following:" + PRIMARY_LABEL + ")\n" +
            "return following")
    Flux<User> getFollowing(String id);

    @Query("match (initiator: " + PRIMARY_LABEL + " {" + ID_LABEL + ": $0}) with initiator\n" +
            "match (followee: " + PRIMARY_LABEL + " {" + ID_LABEL + ": $1}) with followee, initiator\n" +
            "match (initiator)-[con:" + CONNECTION_NAME + "]->(followee)\n" +
            "return con.type")
    Mono<String> getConnection(String initiatorId, String otherPersonId);
}
