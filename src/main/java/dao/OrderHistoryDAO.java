package dao;

import util.DBConnection;

import java.sql.*;

public class OrderHistoryDAO {

    public void viewOrderHistory(int customerID) {
        String sql = """
            SELECT o.OrderID, o.OrderDate, od.BookID, b.Title, od.Quantity, od.Subtotal
            FROM `Order` o
            JOIN OrderDetails od ON o.OrderID = od.OrderID
            JOIN Book b ON od.BookID = b.BookID
            WHERE o.CustomerID = ?
            ORDER BY o.OrderDate DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerID);
            ResultSet rs = stmt.executeQuery();

            System.out.println("📄 Order History for Customer ID: " + customerID);
            System.out.println("--------------------------------------------------");

            while (rs.next()) {
                System.out.println("🧾 OrderID: " + rs.getInt("OrderID"));
                System.out.println("📅 Date: " + rs.getDate("OrderDate"));
                System.out.println("📚 Book: " + rs.getString("Title"));
                System.out.println("🔢 Quantity: " + rs.getInt("Quantity"));
                System.out.println("💰 Subtotal: R" + rs.getDouble("Subtotal"));
                System.out.println("--------------------------------------------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

