package com.romanenko.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserMeta {
    private boolean isBlacklistedByQueryingPerson;
    private boolean isQueryingPersonBlacklisted;
    private boolean isFollowedByQueryingPerson;
    private boolean isFollowingQueryingPerson;
}
