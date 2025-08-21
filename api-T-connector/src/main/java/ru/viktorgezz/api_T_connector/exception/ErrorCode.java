package ru.viktorgezz.api_T_connector.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    CANDLES_NOT_FOUND("FIGI_NOT_FOUND", "За промежуток времени предыдущая свеча figi: %s - не найдена", HttpStatus.NOT_FOUND),
    INTERNAL_EXCEPRION("INTERNAL_EXCEPRION", "Internal error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String defaultMessage;
    private  final HttpStatus status;

    ErrorCode(final String code,
              final String defaultMessage,
              final HttpStatus status
    ) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
