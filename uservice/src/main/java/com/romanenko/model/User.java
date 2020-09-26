package com.romanenko.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    private String id;
    @JsonProperty(required = true)
    private String name;
    @JsonProperty(required = true)
    private String avatarUrl;

    private String bio;
    private int followersAmount;
    private int followingAmount;
    private UserMeta userMeta;
}
