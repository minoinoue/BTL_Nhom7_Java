package hethongqlnv;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=hethongqlnv;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "123456789";

    public static Connection getConnection() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
