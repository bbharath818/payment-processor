package com.lloyds.payments.exception;

public enum ErrorCode {

    ORDER_NOT_FOUND("PaymentIngestor_404", 404, "Account not found"),
    INVALID_ORDER("PaymentIngestor_400", 400, "Invalid Acount"),
    PAYMENT_FAILED("PaymentIngestor_500", 500, "Payment failed"),
    INTERNAL_ERROR("GEN_500", 500, "Internal server error");

    private final String code;
    private final int status;
    private final String defaultMessage;

    ErrorCode(String code, int status, String defaultMessage) {
        this.code = code;
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() { return code; }
    public int getStatus() { return status; }
    public String getDefaultMessage() { return defaultMessage; }
}
