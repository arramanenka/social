package com.romanenko.uservice.dao.impl.neo;

import com.romanenko.security.Identity;
import com.romanenko.uservice.dao.UserDao;
import com.romanenko.uservice.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class NeoUserDao implements UserDao {

    private final NeoUserRepo userRepo;

    @Override
    public Mono<User> saveUser(User user) {
        return userRepo.save(new NeoUser(user))
                .map(NeoUser::toModel);
    }

    @Override
    public Mono<Boolean> deleteById(Identity identity) {
        return userRepo.deleteByPuId(identity.getId());
    }

    @Override
    public Flux<User> getAllByNickBeginning(Identity queryingIdentity, String nickStart) {
        return userRepo.getAllByNickBeginning(queryingIdentity.getId(), nickStart)
                .map(NeoUser::toModel);
    }

    @Override
    public Mono<User> getUserById(Identity queryingIdentity, String id) {
        return userRepo.findUserById(queryingIdentity.getId(), id);
    }
}
