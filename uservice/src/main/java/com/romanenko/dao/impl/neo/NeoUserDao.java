package com.romanenko.dao.impl.neo;

import com.romanenko.dao.UserDao;
import com.romanenko.security.Identity;
import com.romanenko.model.User;
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
                .map(NeoUser::toSimpleModel);
    }

    @Override
    public Mono<Boolean> deleteById(Identity identity) {
        return userRepo.deleteByPuId(identity.getId());
    }

    @Override
    public Flux<User> getAllByNickBeginning(Identity queryingIdentity, String nickStart) {
        return userRepo.getAllByNickBeginning(queryingIdentity.getId(), nickStart)
                .map(NeoUser::toSimpleModel);
    }

    @Override
    public Mono<User> getUserById(Identity queryingIdentity, String id) {
        String queryingUserId = queryingIdentity.getId();
        if (queryingUserId.equals(id)){
            return userRepo.findSelf(id)
                    .map(NeoUser::toSimpleModel);
        }
        return userRepo.findUserById(queryingUserId, id)
                .map(NeoUser::toSimpleModel);
    }
}
