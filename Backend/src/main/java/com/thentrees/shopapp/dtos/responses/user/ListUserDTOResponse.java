package com.thentrees.shopapp.dtos.responses.user;

import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListUserDTOResponse {
    List<UserDTOResponse> users;
    int totalPages;
}
