package com.romanenko.dao;

import com.romanenko.io.PageQuery;
import com.romanenko.model.User;
import com.romanenko.model.UserRecommendation;
import com.romanenko.security.Identity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserDao {
    Mono<User> saveUser(User user);

    Mono<Boolean> deleteById(Identity identity);

    Mono<User> getUserById(Identity queryingIdentity, String id);

    Flux<User> getAllByNickBeginning(Identity queryingIdentity, String nickStart, PageQuery pageQuery);

    Flux<UserRecommendation> getRecommendations(Identity identity, PageQuery pageQuery);
}
