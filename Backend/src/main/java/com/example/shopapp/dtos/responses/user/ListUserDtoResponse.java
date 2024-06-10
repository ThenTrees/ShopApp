package com.example.shopapp.dtos.responses.user;

import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListUserDtoResponse {
    List<UserDtoResponse> users;
    int totalPages;
}
