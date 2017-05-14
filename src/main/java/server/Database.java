package server;

import org.springframework.util.Assert;
import server.enums.UserStanding;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static Database INSTANCE = null;
    private final static String DBFILE = "server.db";

    private Database() throws SQLException {
        File file = new File(DBFILE);
        boolean createTable = !file.exists();
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DBFILE)) {
            if (createTable) {
                connection.createStatement().executeUpdate("create table user (id integer PRIMARY KEY AUTOINCREMENT, name string, password string)");
            }
        }
    }

    /*please use this private static method in all of the non-private static methods in this class. This is good design right here. Yup.*/
    private static Database getDB() throws SQLException {
        if (INSTANCE == null) {
            INSTANCE = new Database();
        }
        return INSTANCE;
    }

    /*String getPassword(String username) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DBFILE)) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user WHERE name = ?");
            stmt.setString(1, username);
            ResultSet results = stmt.executeQuery();
            if (results.next()) {
                return results.getString("password").replaceAll("\\s+", "");
            }
            else {
                return null;
            }
        }
    }*/

    static boolean isUsernameFree(String username) {
        Assert.notNull(username, "Method called with null arguments");
        //TODO: implement database query. must return true if provided username is not currently in database. must return false if provided username is already in use.
        /*placeholder return value*/
        return false;
    }

    static void createNewAccount(String username, String password) {
        Assert.notNull(username, "Method called with null arguments");
        Assert.notNull(password, "Method called with null arguments");
        //TODO: implement database modification. must create new user in database with following information: username, password, time joined (in unix), .
    }

    static boolean checkLogin(String username, String password) {
        Assert.notNull(username, "Method called with null arguments");
        Assert.notNull(password, "Method called with null arguments");
        //TODO: implement database query. must return true if username/password combination is correct. must return false if it is not correct.
        /*placeholder return value*/
        return false;
    }

    public static UserStanding getUserStanding(String username) {
        Assert.notNull(username, "Method called with null arguments");
        //TODO: must query user standing from database. different standing values are specified in 'userstanding' class.
        /*PLACEHOLDER RETURN VALUE*/
        return null;
    }

    public static void setUserStanding(String username, UserStanding userStanding) {
        Assert.notNull(userStanding, "Method called with null arguments");
        Assert.notNull(username, "Method called with null arguments");
        //TODO: must modify user standing in database so that it is the same as specified. different standing values are specified in 'userstanding' class.
    }


    public static boolean isUserStandingAtleast(String username, UserStanding standing) {
        Assert.notNull(username, "Method called with null arguments");
        Assert.notNull(standing, "Method called with null arguments");
        UserStanding realStanding = getUserStanding(username);
        if (UserStanding.ADMIN.equals(realStanding)) {
            return true;
        }
        else if (UserStanding.MOD.equals(realStanding)) {
            return !standing.equals(UserStanding.ADMIN);
        }
        else if (UserStanding.NORMAL.equals(realStanding)) {
            return !(standing.equals(UserStanding.MOD) || standing.equals(UserStanding.ADMIN));
        }
        else if (UserStanding.SILENCED.equals(realStanding)) {
            return standing.equals(UserStanding.SILENCED) || standing.equals(UserStanding.BANNED);
        }
        else {
            return standing.equals(UserStanding.BANNED);
        }
    }

    public static boolean doesUserExist(String username) {
        //TODO: To be implemented. Must return true if user with this username exists in database.
        return false;
    }
}
