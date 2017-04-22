package client;

import java.util.Arrays;
import java.util.List;

class ResponseDigester {

    void printResponse(String serverResponse) {
        if (serverResponse.startsWith("[TYPE=[SimplePrintOut]] [MESSAGE=[")) {
            String response = extractMessage(serverResponse);
            if (response != null) {
                System.out.println(response);
            }
        }
        else if (serverResponse.startsWith("[TYPE=[ChatUpdate]] [CHAT_LOG=[")) {
            String printableChatString = extractChatLog(serverResponse);
            if (!"".equals(printableChatString)) {
                System.out.println(printableChatString);
            }
        }
        System.out.println("Server sent invalid response. Please notify a mod about this bug.");
    }

    private String extractMessage(String simplePrintOutResponse) {
        StringBuilder messageField = new StringBuilder(simplePrintOutResponse);
        messageField.delete(0, 34);
        if (messageField.substring(messageField.length() - 2) == "]]") {
            messageField.delete(messageField.length() - 2, messageField.length());
            return messageField.toString();
        }
        return null;
        /*THIS SHOULD NEVER BE REACHED. ALTHOUGH IF IT IS REACHED, CLIENT APPLICATION SHOULD CONTINUE TO WORK.*/
    }

    private String extractChatLog(String chatUpdateResponse) {
        StringBuilder response = new StringBuilder(chatUpdateResponse);
        response.delete(0, 31);
        if (response.substring(response.length() - 2) == "]]") {
            response.delete(response.length() - 2, response.length());
            String[] chatLog = response.toString().split(" ");
            return convertChatLogToPrintable(Arrays.asList(chatLog));
        }
        /*THIS SHOULD NEVER BE REACHED. ALTHOUGH IF IT IS REACHED, CLIENT APPLICATION SHOULD CONTINUE TO WORK.*/
        return null;
    }

    private String convertChatLogToPrintable(List<String> chatLog) {
        List<String> newEntries = ChatHistory.getNewEntries(chatLog);
        String printableChatString = "";
        for (int i = 0; i < newEntries.size(); i++) {
            if (i == newEntries.size() - 1) {
                printableChatString += newEntries.get(i);
            }
            else {
                printableChatString += newEntries.get(i) + "\n";
            }
        }
        return printableChatString;
    }
}
