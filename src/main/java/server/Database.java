package server;

import org.mindrot.jbcrypt.BCrypt;
import server.enums.UserStanding;

import java.io.File;
import java.sql.*;
import java.util.Objects;

public class Database {
    private static Database INSTANCE = null;
    private final static String DBFILE = "server.db";

    private Database() throws SQLException {
        File file = new File(DBFILE);
        boolean createTable = !file.exists();
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DBFILE)) {
            if (createTable) {
                connection.createStatement().executeUpdate("create table user (id integer PRIMARY KEY AUTOINCREMENT, name string, password string, standing string, currency DECIMAL DEFAULT 0)");
            }
        }
    }

    private static Database getDB() throws SQLException {
        if (INSTANCE == null) {
            INSTANCE = new Database();
        }
        return INSTANCE;
    }

    static void createNewAccount(String username, String password) throws SQLException {
        Objects.requireNonNull(username, "Method called with null arguments");
        Objects.requireNonNull(password, "Method called with null arguments");
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DBFILE)) {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO user (name, password, standing) VALUES (?, ?, 'NORMAL') ");
            stmt.setString(1, username);
            stmt.setString(2, BCrypt.hashpw(password, BCrypt.gensalt()));
            stmt.execute();
        }
    }

    static boolean checkLogin(String username, String password) throws SQLException {
        Objects.requireNonNull(username, "Method called with null arguments");
        Objects.requireNonNull(password, "Method called with null arguments");
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DBFILE)) {
            PreparedStatement stmt = connection.prepareStatement("SELECT password FROM user WHERE name = ?");
            stmt.setString(1, username);
            ResultSet results = stmt.executeQuery();
            if (results.next()) {
                String password_hashed = results.getString("password").replaceAll("\\s+", "");
                return BCrypt.checkpw(password, password_hashed);
            }
        }
        return false;
    }

    public static UserStanding getUserStanding(String username) throws SQLException {
        Objects.requireNonNull(username, "Method called with null arguments");
        UserStanding userStanding = null;
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DBFILE)) {
            PreparedStatement stmt = connection.prepareStatement("SELECT standing FROM user WHERE name = ?");
            stmt.setString(1, username);
            ResultSet results = stmt.executeQuery();
            if (results.next()) {
                String standing = results.getString("standing").replaceAll("\\s+", "");
                switch (standing){
                    case "NORMAL": userStanding = UserStanding.NORMAL;
                        break;
                    case "MOD": userStanding = UserStanding.MOD;
                        break;
                    case "ADMIN": userStanding = UserStanding.ADMIN;
                        break;
                    case "BANNED": userStanding = UserStanding.BANNED;
                        break;
                    case "SILENCED": userStanding = UserStanding.SILENCED;
                }
            }
        }
        return userStanding;
    }

    public static void setUserStanding(String username, UserStanding userStanding) throws SQLException {
        Objects.requireNonNull(userStanding, "Method called with null arguments");
        Objects.requireNonNull(username, "Method called with null arguments");
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DBFILE)) {
            PreparedStatement stmt = connection.prepareStatement("UPDATE user SET standing = ? WHERE name = ?");
            stmt.setString(1, userStanding.name());
            stmt.setString(2, username);
            stmt.execute();
        }
    }


    public static boolean isUserStandingAtleast(String username, UserStanding standing) throws SQLException {
        Objects.requireNonNull(username, "Method called with null arguments");
        Objects.requireNonNull(standing, "Method called with null arguments");
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

    public static boolean doesUserExist(String username) throws SQLException {
        getDB();
        Objects.requireNonNull(username, "Method called with null arguments");
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DBFILE)) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user WHERE name = ?");
            stmt.setString(1, username);
            ResultSet results = stmt.executeQuery();
            return results.next();
        }
    }

    public static double getUserCurrency(String username) throws SQLException {
        Objects.requireNonNull(username, "Method called with null arguments");
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DBFILE)) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user WHERE name = ?");
            stmt.setString(1, username);
            ResultSet results = stmt.executeQuery();
            return results.getDouble("currency");
        }
    }

    public static void addUserCurrency(String username, Double currency) throws SQLException {
        Objects.requireNonNull(username, "Method called with null arguments");
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DBFILE)) {
            PreparedStatement stmt = connection.prepareStatement("UPDATE user SET currency = currency + ? WHERE name = ?");
            stmt.setDouble(1, currency);
            stmt.setString(2, username);
            stmt.execute();
        }
    }


}
