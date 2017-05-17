package server.analyzers;

import server.Client;
import server.Database;
import server.ServerProperties;
import server.enums.ResponseType;
import server.enums.UserStanding;

import java.sql.SQLException;

public class TransferCurrencyAnalyzer implements CommandAnalyzer{

    private final String keyword = ServerProperties.getKeyword(this.getClass());

    @Override
    public boolean isValidCommand(String[] splittedUserInput) {
        return splittedUserInput.length == 3 && keyword.equals(splittedUserInput[0]);
    }

    @Override
    public ResponseType analyze(Client client, String[] splittedUserInput) throws SQLException {

        if (Database.getUserStanding(client.getUsername()) == UserStanding.ADMIN) {
            Double targetAddCurrency;
            try{
                targetAddCurrency = Double.parseDouble(splittedUserInput[2]);
            }
            catch (Exception e){
                return ResponseType.INVALID_CURRENCY;
            }
            String targetUsernameFrom = client.getUsername();
            String targetUsernameTo = splittedUserInput[1];
            if (Database.doesUserExist(targetUsernameFrom )&& Database.doesUserExist(targetUsernameTo)) {
                Double fromUserCurrency = Database.getUserCurrency(targetUsernameFrom);

                if(fromUserCurrency < targetAddCurrency){
                    return ResponseType.NOT_ENOUGH_CURRENCY;
                }
                if(targetAddCurrency<0){
                    return ResponseType.INVALID_CURRENCY;
                }
                Database.addUserCurrency(targetUsernameFrom, targetAddCurrency * -1);
                Database.addUserCurrency(targetUsernameTo, targetAddCurrency);
                Double currency = Database.getUserCurrency(targetUsernameFrom );
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
