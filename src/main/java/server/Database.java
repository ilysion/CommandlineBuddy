package server;

import java.io.File;
import java.sql.*;

/**
 * Created on 2017-04-05.
 */

public class Database {
    private static Database INSTANCE = null;
    private final static String DBFILE = "server.db";

    private Database() {
        try {
            init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Database getDB() {
        if (INSTANCE == null) {
            INSTANCE = new Database();
        }
        return INSTANCE;
    }

    private void init() {
        File file = new File(DBFILE);
        boolean createTable = false;

        try {
            if (!file.exists()) {
                createTable = true;
            }

            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DBFILE);

            if (createTable) {
                try {
                    connection.createStatement().executeUpdate("create table user (id integer PRIMARY KEY AUTOINCREMENT, name string, password string)");
                } catch (Exception e) {
                    throw new RuntimeException("Error: Database failed to initialize! " + e);
                }
            }

            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException("Error: Database failed to initialize! " + e);
        }

    }

    public String getPassword(String username) throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DBFILE)) {
            try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user WHERE name = ?")) {
                stmt.setString(1, username);
                try (ResultSet results = stmt.executeQuery()) {
                    boolean exists = results.next();
                    if (exists) {
                        return results.getString("password").replaceAll("\\s+", "");
                    } else {
                        return null;
                    }
                }
            }
        }
    }
}
