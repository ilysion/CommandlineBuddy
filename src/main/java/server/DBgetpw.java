package server;

import java.sql.*;

/**
 * Created by CmdBuddyTeam on 1.04.2017.
 */

public class DBgetpw {
    private static String dbURL = "jdbc:derby://localhost:1527/BuddyDatabase;user=buddy;password=buddy";
    private static Connection conn = null;
    private static PreparedStatement stmt = null;
    private String pw;

    private void connectDb(){
        try
        {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            conn = DriverManager.getConnection(dbURL);
        }
        catch (Exception except)
        {
            except.printStackTrace();
        }
    }

    private void closeDb(){
        try
        {
            if (stmt != null)
            {
                stmt.close();
            }
            if (conn != null)
            {
                DriverManager.getConnection(dbURL + ";shutdown=true");
                conn.close();
            }
        }
        catch (SQLException sqlExcept)
        {

        }
    }

    public String getUserPw(String username){

        connectDb();
        try{

            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username=?");
            stmt.setString(1,username);
            ResultSet results = stmt.executeQuery();

            results.next();
            pw = results.getString(2 );
            results.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
        finally {
            closeDb();
        }
        return pw;
    }
}
