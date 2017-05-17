package server.analyzers;

import server.Client;
import server.Database;
import server.ServerProperties;
import server.enums.ResponseType;

public class ShowCurrencyAnalyzer implements CommandAnalyzer{

    private final String keyword = ServerProperties.getKeyword(this.getClass());

    @Override
    public boolean isValidCommand(String[] splittedUserInput) {
        return splittedUserInput.length == 1 && keyword.equals(splittedUserInput[0]);
    }

    @Override
    public ResponseType analyze(Client client, String[] splittedUserInput) {
        String username = client.getUsername();
        if (Database.doesUserExist(username)) {
            Double currency = Database.getUserCurrency(username);
            client.addToQueue("Your currency is: " + currency + " coins." );
            return ResponseType.GENERAL_SUCCESS;
        }
        else {
            return ResponseType.USER_DOES_NOT_EXIST;
        }

    }

    @Override
    public String getKeyword() {
        return keyword;
    }
}
