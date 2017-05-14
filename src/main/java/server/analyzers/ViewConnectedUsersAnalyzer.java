package server.analyzers;

import server.Client;
import server.Database;
import server.ServerProperties;
import server.enums.UserStanding;
import server.enums.ResponseType;

public class ViewConnectedUsersAnalyzer implements CommandAnalyzer{
    private final String keyword = ServerProperties.getKeyword(this.getClass());

    @Override
    public boolean isValidCommand(String[] splittedUserInput) {
        return splittedUserInput.length == 1 && keyword.equals(splittedUserInput[0]);
    }

    @Override
    public ResponseType analyze(Client client, String[] splittedUserInput) {
        if (Database.isUserStandingAtleast(client.getUsername(), UserStanding.NORMAL)) {
            return ResponseType.INCOMING_ACTIVE_USERS;
        }
        else {
            return ResponseType.NOT_ALLOWED;
        }
    }

    @Override
    public String getKeyword() {
        return keyword;
    }
}
