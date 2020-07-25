package com.romanenko.dao.impl.neo.model;

import com.romanenko.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.value.MapValue;
import org.neo4j.springframework.data.core.schema.Id;
import org.neo4j.springframework.data.core.schema.Node;
import org.neo4j.springframework.data.core.schema.Property;

@Node(primaryLabel = NeoUser.PRIMARY_LABEL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NeoUser {
    /**
     * Since @QueryResult is not supported in current version of neo4j rx, there are some workarounds, including this.
     * In future, for easier migration to proper method, let's save this nested label
     */
    public static final String AS_NESTED_LABEL = "neoUser";
    public static final String FOLLOWER_AMOUNT_LABEL = "followers";
    public static final String FOLLOWING_AMOUNT_LABEL = "following";
    public static final String PRIMARY_LABEL = "app_user";
    public static final String NAME_LABEL = "name";
    public static final String AVATAR_LABEL = "avatar";
    public static final String ID_LABEL = "puId";
    public static final String BIO_LABEL = "bio";

    @Property(name = NAME_LABEL)
    private String name;
    @Id
    @Property(name = ID_LABEL)
    private String puId;
    @Property(name = AVATAR_LABEL)
    private String avatarUrl;
    @Property(name = BIO_LABEL)
    private String bio;

    public NeoUser(User user) {
        name = user.getName();
        puId = user.getId();
        avatarUrl = user.getAvatarUrl();
        bio = user.getBio();
    }

    public static User convertFullProfile(MapValue mapValue) {
        var builder = User.builder();
        Value neoUser = mapValue.get(AS_NESTED_LABEL);
        if (!neoUser.isNull()) {
            builder
                    .name(neoUser.get(NAME_LABEL).asString(null))
                    .id(neoUser.get(ID_LABEL).asString(null))
                    .avatarUrl(neoUser.get(AVATAR_LABEL).asString(null))
                    .bio(neoUser.get(BIO_LABEL).asString(null));
        }
        builder.followersAmount(mapValue.get(FOLLOWER_AMOUNT_LABEL, 0))
                .followingAmount(mapValue.get(FOLLOWING_AMOUNT_LABEL, 0));
        return builder.build();
    }

    public User toSimpleModel() {
        return User.builder()
                .name(name)
                .id(puId)
                .avatarUrl(avatarUrl)
                .build();
    }

    public User toFullProfileModel() {
        return User.builder()
                .name(name)
                .id(puId)
                .avatarUrl(avatarUrl)
                .bio(bio)
                .build();
    }
}
