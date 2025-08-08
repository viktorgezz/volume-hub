package ru.viktorgezz.api_T_connector.handler;


import java.util.List;

public class ErrorResponse {

    private String message;
    private String code;
    private List<ValidationError> validationErrors;

    public ErrorResponse() {
    }

    public ErrorResponse(String message, String code, java.util.List<ValidationError> validationErrors) {
        this.message = message;
        this.code = code;
        this.validationErrors = validationErrors;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String message;
        private String code;
        private List<ValidationError> validationErrors;

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder validationErrors(List<ValidationError> validationErrors) {
            this.validationErrors = validationErrors;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(message, code, validationErrors);
        }
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    @Override
    public String toString() {
        return "ErrorResponse(message=" + message + ", code=" + code + ", validationErrors=" + validationErrors + ")";
    }
}

