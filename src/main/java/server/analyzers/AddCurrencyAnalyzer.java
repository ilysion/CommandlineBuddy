package server.analyzers;

import server.Client;
import server.Database;
import server.ServerProperties;
import server.enums.ResponseType;
import server.enums.UserStanding;

public class AddCurrencyAnalyzer implements CommandAnalyzer {

    private final String keyword = ServerProperties.getKeyword(this.getClass());

    @Override
    public boolean isValidCommand(String[] splittedUserInput) {
        return splittedUserInput.length == 2 && keyword.equals(splittedUserInput[0]);
    }

    @Override
    public ResponseType analyze(Client client, String[] splittedUserInput) {

        if (Database.getUserStanding(client.getUsername()) == UserStanding.ADMIN) {
            Double targetAddCurrency;
            try{
                targetAddCurrency = Double.parseDouble(splittedUserInput[1]);
            }
            catch (Exception e){
                return ResponseType.INVALID_CURRENCY;
            }

            String username = client.getUsername();
            if (Database.doesUserExist(username)) {
                Database.addUserCurrency(username, targetAddCurrency);
                Double currency = Database.getUserCurrency(username);
                client.addToQueue("Your currency is now: " + currency + " coins." );
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
