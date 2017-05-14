package server.enums;

import server.ServerProperties;

public enum ResponseType {

    DISCONNECTED_FAIL_ATTEMPTS(),
    REQUEST_LOGIN_INPUT(),
    INVALID_LOGIN_INPUT(),
    ACCOUNT_CREATED(),
    USERNAME_TAKEN(),
    INVALID_LOGIN_FORMAT(),
    INVALID_COMMAND(),
    HELP_NORMAL(),
    HELP_MOD(),
    HELP_ADMIN(),
    GENERAL_SUCCESS(),
    NOT_ALLOWED();

    private final String defaultMessage;

    ResponseType() {
        this.defaultMessage = ServerProperties.getMessageOfResponse(this);
    }

    public String getMessage() {
        return defaultMessage;
    }
}