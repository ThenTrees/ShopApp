package com.example.shopapp.dtos.responses.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLoginDtoResponse {
    @JsonProperty("message")
    String message;

    @JsonProperty("token")
    String token;
}
