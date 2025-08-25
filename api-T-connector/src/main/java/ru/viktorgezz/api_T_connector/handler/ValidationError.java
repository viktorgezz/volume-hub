package ru.viktorgezz.api_T_connector.handler;

public class ValidationError {
    private String field;
    private String message;
    private String code;

    public ValidationError() {
    }

    public ValidationError(String field, String message, String code) {
        this.field = field;
        this.message = message;
        this.code = code;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String field;
        private String message;
        private String code;

        public Builder field(String field) {
            this.field = field;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public ValidationError build() {
            return new ValidationError(field, message, code);
        }
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "ValidationError(field=" + field + ", message=" + message + ", code=" + code + ")";
    }
}