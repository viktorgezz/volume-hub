package ru.viktorgezz.api_T_connector.exception;

public class CommonProjectException extends Exception {
    private final String msg;

    public CommonProjectException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
