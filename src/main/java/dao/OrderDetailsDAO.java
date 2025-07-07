package dao;

import model.OrderDetail;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailsDAO {

    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        String sql = "SELECT b.Title, od.Quantity, b.Price, od.Subtotal " +
                "FROM OrderDetails od " +
                "JOIN Book b ON od.BookID = b.BookID " +
                "WHERE od.OrderID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String title = rs.getString("Title");
                int quantity = rs.getInt("Quantity");
                double price = rs.getDouble("Price");
                double subtotal = rs.getDouble("Subtotal");

                details.add(new OrderDetail(title, quantity, price, subtotal));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }
}

