package server.command.analyzers;

import server.UserDataForInMemory;
import server.UserStanding;
import server.conversion.ResponseConverter;
import server.conversion.ResponseType;
import server.singletons.Database;

public class HelpRequestAnalyzer implements CommandAnalyzer{
    @Override
    public boolean isValidCommand(String[] splittedUserInput) {
        return splittedUserInput.length == 1 && splittedUserInput[0] == "help";
    }

    @Override
    public String analyze(UserDataForInMemory userData, String[] splittedUserInput) {
        UserStanding userStanding = Database.getUserStanding(userData.getUsername());
        if (userStanding == UserStanding.NORMAL || userStanding == UserStanding.SILENCED) {
            return ResponseConverter.convertToStreamable(ResponseType.HELP_NORMAL);
        }
        if (userStanding == UserStanding.ADMIN) {
            return ResponseConverter.convertToStreamable(ResponseType.HELP_ADMIN);
        }
        if (userStanding == UserStanding.MOD) {
            return ResponseConverter.convertToStreamable(ResponseType.HELP_MOD);
        }
        throw new RuntimeException("userStanding should be one of the values above.");
    }
}
