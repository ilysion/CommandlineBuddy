package server.conversion;

public enum ResponseType {

    /*The typeCode field might seem redundant as all constants are "SimpleOutPrint", but this design allows more advanced commands to be added in the future.*/
    FORCE_DISCONNECTED("SimpleOutPrint","Disconnected from server due to too many unsuccessful connectUser attempts."),
    REQUEST_LOGIN_INPUT("SimpleOutPrint","Connected to server.\nIf you have previously created an account, please enter your username and password in following format: '<username> <password>'.\nIf you want to create a new account, please enter your desired username and password in the following format: 'register <username> <password>'."),
    INVALID_LOGIN_INPUT("SimpleOutPrint","Invalid login. Try again."),
    ACCOUNT_CREATED("SimpleOutPrint","UserDataForInMemory successfully created. You can now log in."),
    USERNAME_TAKEN("SimpleOutPrint","Username already taken. Try again."),
    INVALID_LOGIN_FORMAT("SimpleOutPrint","Invalid response format. Try again."),
    INVALID_COMMAND("SimpleOutPrint","Invalid command. Try again."),
    HELP_NORMAL("SimpleOutPrint","The following commands are available to you:\nhelp\nsend <message>\nleave'\nview"),
    HELP_MOD("SimpleOutPrint","The following commands are available to you:\nhelp\nsend <message>\nleave\nview\npermaban <username>\nremoveban <username>"),
    HELP_ADMIN("SimpleOutPrint","The following commands are available to you:\nhelp\nsend <message>\nleave\nview\npermaban <username>\nremoveban <username>\nmakemod <username>\nremovemod <username>"),
    GENERAL_SUCCESS("SimpleOutPrint","Command executed successfully."),
    NOT_ALLOWED("SimpleOutPrint","You are not allowed to do that.");

    private String defaultMessage;
    private String typeCode;

    ResponseType(String typeCode, String defaultMessage) {
        this.defaultMessage = defaultMessage;
        this.typeCode = typeCode;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public String getTypeCode() {
        return typeCode;
    }
}
