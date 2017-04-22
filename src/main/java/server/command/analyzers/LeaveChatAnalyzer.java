package server.command.analyzers;

import server.UserDataForInMemory;

public class LeaveChatAnalyzer implements CommandAnalyzer{
    @Override
    public boolean isValidCommand(String[] splittedUserInput) {
        return splittedUserInput.length == 1 && splittedUserInput[0] == "leave";
    }

    @Override
    public String analyze(UserDataForInMemory userData, String[] splittedUserInput) {
        userData.getBundle().closeQuietly();
        /*hmm.. will this method even reach the return statement?*/
        return "";
    }
}
