package dokotubu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {

    private static String url;
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static void setUrl( String url ) {
        DBManager.url = url;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection( url, USER, PASSWORD );
    }
    
}