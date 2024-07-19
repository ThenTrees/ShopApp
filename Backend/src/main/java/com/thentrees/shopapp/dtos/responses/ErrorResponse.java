package com.thentrees.shopapp.dtos.responses;

import java.util.Date;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private Date timestamp;
    private int status;
    private String method;
    private String path;
    private String error;
    private String message;
}
