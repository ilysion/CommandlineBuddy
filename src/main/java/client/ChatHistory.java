package client;

import java.util.ArrayList;
import java.util.List;

/*This is a singleton.*/
class ChatHistory {
    private final List<String> historyOfPrintedChat;
    private static ChatHistory CHAT_HISTORY_INSTANCE = null;

    private ChatHistory() {
        historyOfPrintedChat = new ArrayList<>();
    }

    private static ChatHistory getChatHistory() {
        if (CHAT_HISTORY_INSTANCE == null) {
            CHAT_HISTORY_INSTANCE = new ChatHistory();
        }
        return CHAT_HISTORY_INSTANCE;
    }

    static List<String> getNewEntries(List<String> chatLogUpdate) {
        chatLogUpdate.removeAll(getChatHistory().historyOfPrintedChat);
        getChatHistory().historyOfPrintedChat.addAll(chatLogUpdate);
        return chatLogUpdate;
    }
}
