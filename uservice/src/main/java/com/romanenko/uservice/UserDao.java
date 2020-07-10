package com.romanenko.uservice;

import com.romanenko.security.Identity;
import com.romanenko.uservice.model.User;
import reactor.core.publisher.Mono;

public interface UserDao {
    Mono<Boolean> deleteById(Identity identity);

    Mono<User> saveUser(User user);
}
