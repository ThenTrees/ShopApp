package com.thentrees.shopapp.dtos.responses.user;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thentrees.shopapp.models.Role;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTOResponse implements Serializable {
    @JsonProperty("id")
    Long id;

    @JsonProperty("fullname")
    String fullName;

    @JsonProperty("phone_number")
    String phoneNumber;

    @JsonProperty("address")
    String address;

    @JsonProperty("is_active")
    boolean active;

    @JsonProperty("date_of_birth")
    Date dateOfBirth;

    @JsonProperty("facebook_account_id")
    int facebookAccountId;

    @JsonProperty("google_account_id")
    int googleAccountId;

    @JsonProperty("role")
    Role role;

    public UserDTOResponse(String fullName, String phoneNumber, String address) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
}
