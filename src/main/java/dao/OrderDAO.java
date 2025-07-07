package dao;

import model.CartItem;
import util.DBConnection;

import java.sql.*;
import java.util.List;
import model.Order;
import java.util.ArrayList;

public class OrderDAO {

    // Updated to return orderId if successful, or -1 on failure
    public int placeOrder(int customerId) {
        Connection conn = null;
        PreparedStatement orderStmt = null;
        PreparedStatement detailStmt = null;
        PreparedStatement stockStmt = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            CartDAO cartDAO = new CartDAO();
            int cartId = cartDAO.getCartIdByCustomerId(customerId);
            List<CartItem> items = cartDAO.getCartItems(cartId);

            if (items.isEmpty()) {
                System.out.println("🛑 Cart is empty. Cannot place order.");
                return -1;
            }

            // Step 1: Insert into Order table
            String insertOrder = "INSERT INTO `Order` (CustomerID, OrderDate) VALUES (?, NOW())";
            orderStmt = conn.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, customerId);
            orderStmt.executeUpdate();

            ResultSet rs = orderStmt.getGeneratedKeys();
            int orderId = -1;
            if (rs.next()) {
                orderId = rs.getInt(1);
            } else {
                conn.rollback();
                return -1;
            }

            // Step 2: Insert into OrderDetails and update stock
            String insertDetail = "INSERT INTO OrderDetails (OrderID, BookID, Quantity, Subtotal) VALUES (?, ?, ?, ?)";
            String updateStock = "UPDATE Book SET StockQuantity = StockQuantity - ? WHERE BookID = ?";

            detailStmt = conn.prepareStatement(insertDetail);
            stockStmt = conn.prepareStatement(updateStock);

            for (CartItem item : items) {
                double price = getBookPrice(item.getBookID(), conn);
                double subtotal = price * item.getQuantity();

                // Insert into OrderDetails
                detailStmt.setInt(1, orderId);
                detailStmt.setInt(2, item.getBookID());
                detailStmt.setInt(3, item.getQuantity());
                detailStmt.setDouble(4, subtotal);
                detailStmt.executeUpdate();

                // Update Book stock
                stockStmt.setInt(1, item.getQuantity());
                stockStmt.setInt(2, item.getBookID());
                stockStmt.executeUpdate();
            }

            // Clear cart
            String clearCart = "DELETE FROM CartItems WHERE CartID = ?";
            try (PreparedStatement clearStmt = conn.prepareStatement(clearCart)) {
                clearStmt.setInt(1, cartId);
                clearStmt.executeUpdate();
            }

            conn.commit();
            System.out.println("✅ Order placed successfully!");
            return orderId;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return -1;

        } finally {
            try {
                if (orderStmt != null) orderStmt.close();
                if (detailStmt != null) detailStmt.close();
                if (stockStmt != null) stockStmt.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Helper method to get book price
    private double getBookPrice(int bookId, Connection conn) throws SQLException {
        String sql = "SELECT Price FROM Book WHERE BookID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("Price");
            }
        }
        return 0.0;
    }

    // Fetch all orders for a given customer
    public List<Order> getOrdersByCustomerId(int customerId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT OrderID, OrderDate, OrderStatus FROM `Order` WHERE CustomerID = ? ORDER BY OrderDate DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("OrderID"),
                        rs.getDate("OrderDate").toLocalDate(),
                        rs.getString("OrderStatus")
                );
                orders.add(order);
            }
        }

        return orders;
    }


    // Update order status, e.g. after payment
    public void updateOrderStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE `Order` SET OrderStatus = ? WHERE OrderID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
        }
    }
}


