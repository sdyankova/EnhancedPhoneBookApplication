import java.sql.*;
import java.util.ArrayList;

public class connect {
    private static final String URL = "jdbc:mysql://localhost:3306/PhonebookDB";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    public static ArrayList<String[]> executeQuery(String query) {
        ArrayList<String[]> results = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                form.model.addColumn(metaData.getColumnName(i));
            }

            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getString(i + 1);
                }
                results.add(row);
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return results;
    }

    public static void deleteRow(String Name) {
        String query = "DELETE FROM PhonebookDB.Contacts WHERE ID = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, Name);
            pstmt.executeUpdate();

            System.out.println("Contact deleted successfully!");

        } catch (SQLException ex) {
            System.out.println("SQL Error: " + ex.getMessage());
        }
    }

    public static void updateDatabase(String Name, String[] columns, String[] newValues) {
        if (columns.length == 0 || newValues.length == 0) {
            System.out.println("Error: No columns or values provided.");
            return;
        }

        if (columns.length != newValues.length) {
            System.out.println("Error: Column count does not match value count.");
            return;
        }

        StringBuilder queryBuilder = new StringBuilder("UPDATE PhonebookDB.Contacts SET ");
        for (int i = 0; i < columns.length; i++) {
            queryBuilder.append(columns[i]).append(" = ?");
            if (i < columns.length - 1) {
                queryBuilder.append(", ");
            }
        }
        queryBuilder.append(" WHERE ID = ?");

        String query = queryBuilder.toString();

        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            connection.setAutoCommit(false);

            pstmt = connection.prepareStatement(query);

            for (int i = 0; i < newValues.length; i++) {
                pstmt.setString(i + 1, newValues[i]);
            }
            pstmt.setString(newValues.length + 1, Name);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                connection.commit();
                System.out.println("Update committed successfully");
            } else {
                connection.rollback();
                System.out.println("Update failed. Transaction rolled back.");
            }
        } catch (SQLException ex) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
                System.out.println("SQL Error: " + ex.getMessage());
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
    }

    public static void addContact(String Name, String PhoneNumber, String Email, String Address) {
        String query = "INSERT INTO PhonebookDB.Contacts (Name, Phone, Email, Address) VALUES (?, ?, ?, ?)";

        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(query);

            pstmt.setString(1, Name);
            pstmt.setString(2, PhoneNumber);
            pstmt.setString(3, Email);
            pstmt.setString(4, Address);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                connection.commit();
                System.out.println("Insertion committed successfully");
            } else {
                connection.rollback();
                System.out.println("Insertion failed. Transaction rolled back.");
            }
        } catch (SQLException ex) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
                System.out.println("SQL Error: " + ex.getMessage());
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
    }
}