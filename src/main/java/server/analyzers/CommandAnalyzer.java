package server.analyzers;

import server.Client;
import server.enums.ResponseType;

public interface CommandAnalyzer {
    boolean isValidCommand(String[] splittedUserInput);

    ResponseType analyze(Client client, String[] splittedUserInput);

    String getKeyword();

}
