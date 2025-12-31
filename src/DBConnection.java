import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/project";
    private static final String USER = "root";
    private static final String PASSWORD = "#M@f@z$$@q1b#";

    private static Connection conn = null;

    public static Connection getConnection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connected to database.");
            } catch (SQLException e) {
                System.out.println("Database connection failed: " + e.getMessage());
            }
        }
        return conn;
    }
public static void main(String Args[]){
        DBConnection.getConnection();
}
}