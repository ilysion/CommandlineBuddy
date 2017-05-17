package server.analyzers;

import server.Client;
import server.Database;
import server.ServerProperties;
import server.enums.UserStanding;
import server.enums.ResponseType;

public class MakeModAnalyzer implements CommandAnalyzer {
    private final String keyword = ServerProperties.getKeyword(this.getClass());

    @Override
    public boolean isValidCommand(String[] splittedUserInput) {
        return splittedUserInput.length == 2 && keyword.equals(splittedUserInput[0]);
    }

    @Override
    public ResponseType analyze(Client client, String[] splittedUserInput) {
        if (Database.getUserStanding(client.getUsername()) == UserStanding.ADMIN) {
            String targetUsername = splittedUserInput[1];
            if (Database.doesUserExist(targetUsername)) {
                Database.setUserStanding(targetUsername, UserStanding.MOD);
                client.setUserStanding(UserStanding.MOD);
                return ResponseType.GENERAL_SUCCESS;
            }
            else {
                return ResponseType.USER_DOES_NOT_EXIST;
            }

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
