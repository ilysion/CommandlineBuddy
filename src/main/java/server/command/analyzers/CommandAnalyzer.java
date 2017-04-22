package server.command.analyzers;

import server.UserDataForInMemory;

public interface CommandAnalyzer {
    boolean isValidCommand(String[] splittedUserInput);

    String analyze(UserDataForInMemory userData, String[] splittedUserInput);
}
