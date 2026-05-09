package com.lloyds.payments.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private int statusCode;
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponse(String message, int statusCode) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
