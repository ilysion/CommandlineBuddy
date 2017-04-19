package server;

import java.io.File;
import java.sql.*;


class Database {
    private static Database INSTANCE = null;
    private final static String DBFILE = "server.db";

    private Database() throws SQLException {
        init();
    }

    static Database getDB() throws SQLException {
        if (INSTANCE == null) {
            INSTANCE = new Database();
        }
        return INSTANCE;
    }

    private void init() throws SQLException {
        File file = new File(DBFILE);
        boolean createTable = false;
        if (!file.exists()) {
            createTable = true;
        }
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DBFILE);
        if (createTable) {
            connection.createStatement().executeUpdate("create table user (id integer PRIMARY KEY AUTOINCREMENT, name string, password string)");
        }
        connection.close();
    }

    String getPassword(String username) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DBFILE);
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user WHERE name = ?");
             ResultSet results = stmt.executeQuery();) {
            stmt.setString(1, username);
            boolean exists = results.next();
            if (exists) {
                return results.getString("password").replaceAll("\\s+", "");
            }
            else {
                return null;
            }
        }
    }
}
