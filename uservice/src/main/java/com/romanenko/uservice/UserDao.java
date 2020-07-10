package com.romanenko.uservice;

import com.romanenko.security.Identity;
import com.romanenko.uservice.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserDao {
    Mono<Boolean> deleteById(Identity identity);

    Mono<User> saveUser(User user);

    Flux<User> getAllByNickBeginning(String nickStart);

    Mono<User> getUserById(String id);
}
