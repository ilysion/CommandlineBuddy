package server;

import org.mindrot.jbcrypt.BCrypt;
import java.io.File;
import java.sql.*;

/**
 * Created on 2017-04-05.
 */

public class Database {
    private static String DBFILE = "server.db";
    private Connection connection = null;

    public Database() {
        init();
    }

    private void init() {
        File file = new File(DBFILE);
        boolean createTable = false;

        try
        {
            if (!file.exists()){
                createTable = true;
            }

            connection = DriverManager.getConnection("jdbc:sqlite:" + DBFILE);

            if (createTable) {
                try {
                    connection.createStatement().executeUpdate("create table user (id integer PRIMARY KEY AUTOINCREMENT, name string, password string)");
                } catch (Exception e) {
                    throw new RuntimeException("Error: Database failed to initialize! " + e);
                }
            }
        }

        catch(SQLException e) {
            throw new RuntimeException("Error: Database failed to initialize! " + e);
        }

    }

    public void closeDB() {
        try
        {
            if(connection != null) connection.close();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public boolean userExists(String username) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user WHERE name = ?");
            stmt.setString(1,username);
            ResultSet results = stmt.executeQuery();

            return results.next();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkpw(String username, String password) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user WHERE name = ?");
            stmt.setString(1,username);
            ResultSet results = stmt.executeQuery();

            results.next();
            String hashFromDB = results.getString(3).replaceAll("\\s+","");
            results.close();

            System.out.println(BCrypt.checkpw(password, hashFromDB));

            return BCrypt.checkpw(password, hashFromDB);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
