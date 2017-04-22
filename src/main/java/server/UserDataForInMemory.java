package server;

public class UserDataForInMemory {
    private String username;
    private SingleConnectionBundle bundle;

    public UserDataForInMemory(String username, SingleConnectionBundle bundle) {
        this.username = username;
        this.bundle = bundle;
    }

    public SingleConnectionBundle getBundle() {
        return bundle;
    }

    public String getUsername() {
        return username;
    }
}
