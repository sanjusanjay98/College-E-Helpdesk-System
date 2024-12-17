package college;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/csp";
    private static final String USER = "root";
    private static final String PASSWORD = "admin";

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
    	Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Database connected successfully!");
        return connection;
    }
}