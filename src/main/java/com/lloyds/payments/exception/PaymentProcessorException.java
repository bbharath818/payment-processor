package com.lloyds.payments.exception;


public class PaymentProcessorException extends BaseException {

    public PaymentProcessorException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage(),
                errorCode.getCode(),
                errorCode.getStatus());
    }

    public PaymentProcessorException(ErrorCode errorCode, String customMessage) {
        super(customMessage,
                errorCode.getCode(),
                errorCode.getStatus());
    }

    public PaymentProcessorException(String errorCode, String customMessage, int status) {
        super(customMessage,
                errorCode,
                status);
    }
}
