package server.analyzers;

import server.Client;
import server.Database;
import server.ServerProperties;
import server.enums.UserStanding;
import server.enums.ResponseType;

import java.sql.SQLException;

public class HelpRequestAnalyzer implements CommandAnalyzer{

    private final String keyword = ServerProperties.getKeyword(this.getClass());

    @Override
    public String getKeyword() {
        return keyword;
    }

    @Override
    public boolean isValidCommand(String[] splittedUserInput) {
        return splittedUserInput.length == 1 && keyword.equals(splittedUserInput[0]);
    }

    @Override
    public ResponseType analyze(Client client, String[] splittedUserInput) throws SQLException {
        UserStanding userStanding = Database.getUserStanding(client.getUsername());
        if (userStanding == UserStanding.NORMAL || userStanding == UserStanding.SILENCED) {
            return ResponseType.HELP_NORMAL;
        }
        if (userStanding == UserStanding.ADMIN) {
            return ResponseType.HELP_ADMIN;
        }
        if (userStanding == UserStanding.MOD) {
            return ResponseType.HELP_MOD;
        }
        throw new IllegalStateException("userStanding should be one of the values above.");
    }
}
