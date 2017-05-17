package server.analyzers;

import server.Client;
import server.enums.ResponseType;

import java.sql.SQLException;

public interface CommandAnalyzer {
    boolean isValidCommand(String[] splittedUserInput);

    ResponseType analyze(Client client, String[] splittedUserInput) throws SQLException;

    String getKeyword();

}
