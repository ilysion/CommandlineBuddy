package server.command.analyzers;

import server.UserDataForInMemory;
import server.UserStanding;
import server.command.analyzers.CommandAnalyzer;
import server.conversion.ResponseConverter;
import server.conversion.ResponseType;
import server.singletons.Database;

public class SilenceAnalyzer implements CommandAnalyzer{
    @Override
    public boolean isValidCommand(String[] splittedUserInput) {
        return false;
    }

    @Override
    public String analyze(UserDataForInMemory userData, String[] splittedUserInput) {
        if (Database.getUserStanding(userData.getUsername()) == UserStanding.MOD || Database.getUserStanding(userData.getUsername()) == UserStanding.ADMIN) {
            Database.setUserStanding(UserStanding.SILENCED);
            return ResponseConverter.convertToStreamable(ResponseType.GENERAL_SUCCESS);
        }
        else {
            return ResponseConverter.convertToStreamable(ResponseType.NOT_ALLOWED);
        }
    }
}
