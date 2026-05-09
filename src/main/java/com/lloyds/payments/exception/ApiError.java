package com.lloyds.payments.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApiError {
    private String message;
    private String errorCode;
    private int status;
    private LocalDateTime timestamp;


}
