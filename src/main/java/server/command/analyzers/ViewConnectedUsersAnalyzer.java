package server.command.analyzers;

import server.UserDataForInMemory;
import server.UserStanding;
import server.conversion.ResponseConverter;
import server.conversion.ResponseType;
import server.singletons.Database;
import server.singletons.InMemoryDatabase;

import java.util.List;

public class ViewConnectedUsersAnalyzer implements CommandAnalyzer{
    @Override
    public boolean isValidCommand(String[] splittedUserInput) {
        return splittedUserInput.length == 1 && splittedUserInput[0] == "view";
    }

    @Override
    public String analyze(UserDataForInMemory userData, String[] splittedUserInput) {
        if (Database.getUserStanding(userData.getUsername()) == UserStanding.MOD || Database.getUserStanding(userData.getUsername()) == UserStanding.ADMIN || Database.getUserStanding(userData.getUsername()) == UserStanding.NORMAL) {
            List<String> usernames = InMemoryDatabase.safeGetUsernames();
            return ResponseConverter.convertToStreamableViewResponse(usernames);
        }
        else {
            return ResponseConverter.convertToStreamable(ResponseType.NOT_ALLOWED);
        }
    }
}
