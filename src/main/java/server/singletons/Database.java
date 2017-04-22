package server.singletons;

import server.UserStanding;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static Database INSTANCE = null;
    private final static String DBFILE = "server.db";

    private Database() throws SQLException {
        File file = new File(DBFILE);
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DBFILE)) {
            if (!file.exists()) {
                connection.createStatement().executeUpdate("create table user (id integer PRIMARY KEY AUTOINCREMENT, name string, password string)");
            }
        }
    }

    /*PLEASE USE THIS PRIVATE STATIC METHOD IN ALL OF THE NON-PRIVATE STATIC METHODS IN THIS CLASS. This is good design right here. Yup.*/
    private static Database getDB() throws SQLException {
        if (INSTANCE == null) {
            INSTANCE = new Database();
        }
        return INSTANCE;
    }

    public static boolean isUsernameFree(String username) {
        //TODO: IMPLEMENT DATABASE QUERY. MUST RETURN TRUE IF PROVIDED USERNAME IS NOT CURRENTLY IN DATABASE. MUST RETURN FALSE IF PROVIDED USERNAME IS ALREADY IN USE.
        /*placeholder return value*/
        return false;
    }

    public static void createNewAccount(String username, String password) {
        //TODO: IMPLEMENT DATABASE MODIFICATION. MUST CREATE NEW USER IN DATABASE WITH FOLLOWING INFORMATION: username, password, time joined (in UNIX), .
    }

    public static boolean isValidLogin(String username, String password) {
        //TODO: IMPLEMENT DATABASE QUERY. MUST RETURN TRUE IF USERNAME/PASSWORD COMBINATION IS CORRECT. MUST RETURN FALSE IF IT IS NOT CORRECT.
        /*placeholder return value*/
        return false;
    }

    public static List<String> getChatLog() {
        //TODO: MUST RETURN THE CHAT LOG FROM DATABASE. I PROPOSE THAT EACH ENTRY/LINE IN THE CHAT LOG IS A STRING AND THESE STRINGS ARE ARRANGED INTO A LIST WITH THE NEWEST ENTRY/LINE FIRST AND OLDEST ENTRY/LINE LAST.
        /*The strings in the list could be formatted like this:
        * "[TYPE=[Message]] [USER=[DanB]] [TIME=[135234871614] [MESSAGE=[lol admin sucks!!!]]]"
        * "[TYPE=[Alert]] [TIME =[13523487486]] [ALERT=['Danb' has been kicked from the chat by 'ProAdmin69'.]]"
        * And so on..
        * */

        /*PLACEHOLDER RETURN VALUE*/
        return new ArrayList<>();
    }

    public static void addEntryToDatabase(String entry) {
        //TODO: THE GIVEN ENTRY MUST BE ADDED TO THE DATABASE.
        /*The given entry will already be in the correct format: (and won't have to be changed. The string can simply be added to the singletons.)
        * "[TYPE=[Message]] [USER=[DanB]] [TIME=[135234871614] [MESSAGE=[lol admin sucks!!!]]]"
        * "[TYPE=[Alert]] [TIME =[13523487486]] [ALERT=['Danb' has been kicked from the chat by 'ProAdmin69'.]]"
        * And so on..
        * */
    }

    public static UserStanding getUserStanding(String username) {
        //TODO: MUST QUERY USER STANDING FROM DATABASE. DIFFERENT STANDING VALUES ARE SPECIFIED IN 'UserStanding' CLASS.
        /*PLACEHOLDER RETURN VALUE*/
        return UserStanding.ADMIN;
    }

    public static void setUserStanding(UserStanding userStanding) {
        //TODO: MUST MODIFY USER STANDING IN DATABASE SO THAT IT IS THE SAME AS SPECIFIED. DIFFERENT STANDING VALUES ARE SPECIFIED IN 'UserStanding' CLASS.
    }


    /*
    Is this method still necessary? I kept it here just in case.

    private String getPassword(String username) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DBFILE)) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user WHERE name = ?");
            ResultSet results = stmt.executeQuery();
            stmt.setString(1, username);
            if (results.next()) {
                return results.getString("password").replaceAll("\\s+", "");
            }
            else {
                return null;
            }
        }
    }*/

}
