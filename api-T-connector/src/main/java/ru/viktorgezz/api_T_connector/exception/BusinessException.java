package ru.viktorgezz.api_T_connector.exception;

public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] args;

    public BusinessException(final ErrorCode errorCode, final Object... args) {
        super(getFormatterMessage(errorCode, args));
        this.errorCode = errorCode;
        this.args = args;
    }

    private static String getFormatterMessage(ErrorCode errorCode, Object[] args) {
        if (args == null || args.length == 0) {
            return errorCode.getDefaultMessage();
        }
        return String.format(errorCode.getDefaultMessage(), args);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Object[] getArgs() {
        return args;
    }
}
