package server.command.analyzers;

import server.UserDataForInMemory;

public interface CommandAnalyzer {
    boolean isValidCommand(String[] splittedUserInput);

    String analyze(UserDataForInMemory userData, String[] splittedUserInput);

    //TODO: ADD MORE COMMANDS AND ANALYZERS! MORE AND MORE! 100 YEARS OF ANALYZERS! NEVER STOP!!
}
