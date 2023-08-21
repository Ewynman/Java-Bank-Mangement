import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:data/bankDatabase.db";

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL);
        System.out.println("Connected to the database");

        // Print the list of tables in the connected database
        printTables(connection);

        return connection;
    }

    private static void printTables(Connection connection) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            String[] tableTypes = {"TABLE"};
            ResultSet tables = metaData.getTables(null, null, null, tableTypes);

            System.out.println("Tables in the database:");
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println(tableName);
            }

            tables.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Connection connection = getConnection();
            // Perform other database operations here using the connection
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
