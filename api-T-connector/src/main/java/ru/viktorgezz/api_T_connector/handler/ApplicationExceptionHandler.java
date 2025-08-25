package ru.viktorgezz.api_T_connector.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.viktorgezz.api_T_connector.exception.BusinessException;
import ru.viktorgezz.api_T_connector.exception.ErrorCode;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApplicationExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(final BusinessException e) {
        final ErrorResponse body = ErrorResponse
                .builder()
                .message(e.getMessage())
                .code(e.getErrorCode().getCode())
                .build();

        log.info("BusinessException: {}", e.getMessage());
        log.debug(e.getMessage(), e);

        return ResponseEntity.status(e.getErrorCode()
                        .getStatus() != null ? e.getErrorCode()
                        .getStatus() : HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        final List<ValidationError> errors = new ArrayList<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            final String fieldName = ((FieldError) error).getField();
            final String errorCode = error.getDefaultMessage();
            errors.add(ValidationError.builder()
                    .field(fieldName)
                    .message(errorCode)
                    .code(errorCode)
                    .build());
        });
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .validationErrors(errors)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(final Exception e) {
        log.error(e.getMessage(), e);
        final ErrorResponse body = ErrorResponse.builder()
                .message(ErrorCode.INTERNAL_EXCEPRION.getDefaultMessage())
                .code(ErrorCode.INTERNAL_EXCEPRION.getCode())
                .build();
        return ResponseEntity.status(ErrorCode.INTERNAL_EXCEPRION.getStatus())
                .body(body);
    }
}


