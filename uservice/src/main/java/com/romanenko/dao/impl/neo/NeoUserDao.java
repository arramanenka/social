package com.romanenko.dao.impl.neo;

import com.romanenko.dao.UserDao;
import com.romanenko.dao.impl.neo.model.NeoUser;
import com.romanenko.io.PageQuery;
import com.romanenko.model.User;
import com.romanenko.model.UserRecommendation;
import com.romanenko.security.Identity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class NeoUserDao implements UserDao {

    private final NeoUserRepository userRepo;

    @Override
    public Mono<User> saveUser(User user) {
        return userRepo.save(new NeoUser(user))
                .map(NeoUser::toFullProfileModel);
    }

    @Override
    public Mono<Boolean> deleteById(Identity identity) {
        return userRepo.deleteByPuId(identity.getId());
    }

    @Override
    public Flux<User> getAllByNickBeginning(Identity queryingIdentity, String nickStart, PageQuery pageQuery) {
        return userRepo.findAllByNick(queryingIdentity.getId(), nickStart, pageQuery.calculateSkipAmount(), pageQuery.amount)
                .map(NeoUser::convertSimpleProfile);
    }

    @Override
    public Flux<UserRecommendation> getRecommendations(Identity identity, PageQuery pageQuery) {
        return userRepo.findRecommendations(identity.getId(), pageQuery.skipAmount, pageQuery.amount)
                .map(NeoUser::convertUserRecommendation);
    }

    @Override
    public Mono<User> getUserById(Identity queryingIdentity, String id) {
        String queryingUserId = queryingIdentity.getId();
        if (queryingUserId.equals(id)) {
            return userRepo.findSelf(id)
                    .map(NeoUser::convertFullProfile);
        }
        return userRepo.findUserById(queryingUserId, id)
                .map(NeoUser::convertFullProfile)
                .switchIfEmpty(Mono.error(new HttpClientErrorException(HttpStatus.NOT_FOUND)));
    }
}
