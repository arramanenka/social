package com.romanenko.uservice.dao.impl.user.neo;

import com.romanenko.uservice.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.springframework.data.core.schema.Id;
import org.neo4j.springframework.data.core.schema.Node;
import org.neo4j.springframework.data.core.schema.Property;

@Node(primaryLabel = NeoUser.PRIMARY_LABEL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NeoUser {
    public static final String PRIMARY_LABEL = "app_user";
    public static final String NAME_LABEL = "name";
    public static final String AVATAR_LABEL = "avatar";
    public static final String ID_LABEL = "id";
    @Property(name = NAME_LABEL)
    private String name;
    @Id
    @Property(name = ID_LABEL)
    private String puId;
    @Property(name = AVATAR_LABEL)
    private String avatarUrl;

    public NeoUser(User user) {
        name = user.getName();
        puId = user.getId();
        avatarUrl = user.getAvatarUrl();
    }

    public User toModel() {
        return User.builder()
                .name(name)
                .id(puId)
                .avatarUrl(avatarUrl)
                .build();
    }
}
