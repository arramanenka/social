package com.romanenko.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String id;
    @JsonProperty(required = true)
    private String name;
    @JsonProperty(required = true)
    private String avatarUrl;
}
