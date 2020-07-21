package com.romanenko.uservice.dao;

import com.romanenko.security.Identity;
import com.romanenko.uservice.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserDao {
    Mono<User> saveUser(User user);

    Mono<Boolean> deleteById(Identity identity);

    Mono<User> getUserById(Identity queryingIdentity, String id);

    Flux<User> getAllByNickBeginning(Identity queryingIdentity, String nickStart);
}
