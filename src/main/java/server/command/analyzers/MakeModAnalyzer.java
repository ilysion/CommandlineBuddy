package server.command.analyzers;

import server.UserDataForInMemory;
import server.UserStanding;
import server.conversion.ResponseConverter;
import server.conversion.ResponseType;
import server.singletons.Database;

public class MakeModAnalyzer implements CommandAnalyzer{
    @Override
    public boolean isValidCommand(String[] splittedUserInput) {
        return splittedUserInput.length == 2 && splittedUserInput[0] == "makemod";
    }

    @Override
    public String analyze(UserDataForInMemory userData, String[] splittedUserInput) {
        if (Database.getUserStanding(userData.getUsername()) == UserStanding.ADMIN) {
            Database.setUserStanding(UserStanding.MOD);
            return ResponseConverter.convertToStreamable(ResponseType.GENERAL_SUCCESS);
        }
        else {
            return ResponseConverter.convertToStreamable(ResponseType.NOT_ALLOWED);
        }
    }
}
