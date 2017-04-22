package server.conversion;

import java.util.List;

public class ResponseConverter {
    public static String convertToStreamable(ResponseType type, String... chatEntries) {
        if (type.getTypeCode() == "SimplePrintOut") {
            return "[TYPE=[SimplePrintOut]] [MESSAGE=[" + type.getDefaultMessage() + "]]";
        }
        if (type.getTypeCode() == "ChatUpdate") {
            String chatLogString = "[TYPE=[ChatUpdate]] [CHAT_LOG=[";
            for (int i = 0; i < chatEntries.length; i++) {
                if (i == chatEntries.length - 1) {
                    chatLogString += "[" + chatEntries[i] + "]]]";
                }
                else {
                    chatLogString += "[" + chatEntries[i] + "] ";
                }
            }
            return chatLogString;
        }
        /*THIS SHOULD NEVER BE REACHED!!*/
        throw new RuntimeException("ResponseDigester convertToStreamable wanted to return null, which should never happen since all ResponseType enums should be accounted for.");
    }

    public static String convertToChatEntry(String message, String username) {
        return "[TYPE=[Message]] [USER=["+username+"]] [TIME=["+System.currentTimeMillis()+"] [MESSAGE=["+message+"]]]";
    }
    public static String convertToStreamableViewResponse(List<String> usernames) {
        String response = "[TYPE=[SimplePrintOut]] [MESSAGE=[";
        for (int i = 0; i < usernames.size(); i++) {
            if (i == usernames.size() - 1) {
                response += usernames.get(i);
            }
            else {
                response += usernames.get(i) + "\n";
            }
        }
        response += "]]";
        return response;
    }
}
