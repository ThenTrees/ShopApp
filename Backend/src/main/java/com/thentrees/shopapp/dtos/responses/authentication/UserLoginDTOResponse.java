package com.thentrees.shopapp.dtos.responses.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLoginDTOResponse {
    @JsonProperty("token")
    String token;

    @JsonProperty("refresh_token")
    String refreshToken;
}
