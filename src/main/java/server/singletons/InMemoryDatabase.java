package server.singletons;

import server.UserDataForInMemory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*This is a singleton.*/
public class InMemoryDatabase {
    private Set<UserDataForInMemory> connectedUserDataForInMemories;
    private static InMemoryDatabase IN_MEMORY_DATABASE_INSTANCE = null;

    private InMemoryDatabase() {
        connectedUserDataForInMemories = new HashSet<>();
    }

    /*NB! The following safe methods are synchronized with the ChatHistory.class*/

    public static synchronized boolean safeAdd(UserDataForInMemory userDataForInMemory) {
        return getInMemoryDatabase().connectedUserDataForInMemories.add(userDataForInMemory);
    }

    public static synchronized boolean safeRemove(UserDataForInMemory userDataForInMemory) {
        return getInMemoryDatabase().connectedUserDataForInMemories.remove(userDataForInMemory);
    }

    public static synchronized boolean safeContains(UserDataForInMemory userDataForInMemory) {
        return getInMemoryDatabase().connectedUserDataForInMemories.contains(userDataForInMemory);
    }

    public static synchronized List<String> safeGetUsernames() {
        List<String> usernames = new ArrayList<>();
        for (UserDataForInMemory user : getInMemoryDatabase().connectedUserDataForInMemories) {
            usernames.add(user.getUsername());
        }
        return usernames;
    }

    private static InMemoryDatabase getInMemoryDatabase() {
        if (IN_MEMORY_DATABASE_INSTANCE == null) {
            IN_MEMORY_DATABASE_INSTANCE = new InMemoryDatabase();
        }
        return IN_MEMORY_DATABASE_INSTANCE;
    }
}
