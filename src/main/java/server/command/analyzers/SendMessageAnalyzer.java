package server.command.analyzers;

import server.UserDataForInMemory;
import server.UserStanding;
import server.conversion.ResponseConverter;
import server.conversion.ResponseType;
import server.singletons.Database;

public class SendMessageAnalyzer implements CommandAnalyzer {
    @Override
    public boolean isValidCommand(String[] splittedUserInput) {
        return splittedUserInput[0] == "send";
    }

    @Override
    public String analyze(UserDataForInMemory userData, String[] splittedUserInput) {
        UserStanding userStanding = Database.getUserStanding(userData.getUsername());
        if (userStanding == UserStanding.NORMAL || userStanding == UserStanding.ADMIN || userStanding == UserStanding.MOD) {
            String userMessage = "";
            for (int i = 0; i < splittedUserInput.length; i++) {
                if (i != 0) {
                    userMessage += splittedUserInput[i] + " ";
                }
                if (i == splittedUserInput.length - 1) {
                    userMessage += splittedUserInput[i];
                }
            }
            String chatEntry = ResponseConverter.convertToChatEntry(userMessage, userData.getUsername());
            Database.addEntryToDatabase(chatEntry);
            return ResponseConverter.convertToStreamable(ResponseType.GENERAL_SUCCESS);
        }
        else {
            return ResponseConverter.convertToStreamable(ResponseType.NOT_ALLOWED);
        }
    }
}
