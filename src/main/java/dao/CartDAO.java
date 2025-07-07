package dao;

import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.CartItem;  // make sure you have this model class as discussed


public class CartDAO {

    // Method to add an item to the cart
    public boolean addItemToCart(int cartID, int bookID, int quantity) {
        String sql = "INSERT INTO CartItems (CartID, BookID, Quantity) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cartID);
            stmt.setInt(2, bookID);
            stmt.setInt(3, quantity);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Book added to cart successfully!");
                return true;  // <-- Return true here!
            } else {
                System.out.println("❌ Failed to add book to cart.");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false on exception
        }
    }


    // Optional: View cart contents (for debugging)
    public void viewCart(int cartID) {
        String sql = "SELECT * FROM CartItems WHERE CartID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cartID);
            ResultSet rs = stmt.executeQuery();

            System.out.println("🛒 Cart Items:");
            while (rs.next()) {
                System.out.println(" - BookID: " + rs.getInt("BookID") + ", Qty: " + rs.getInt("Quantity"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeItemFromCart(int cartID, int bookID) {
        String sql = "CALL RemoveItemFromCart(?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cartID);
            stmt.setInt(2, bookID);
            stmt.executeUpdate();
            System.out.println("✅ Book removed from cart.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCartIdByCustomerId(int customerId) throws SQLException {
        String sql = "SELECT CartID FROM Cart WHERE CustomerID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("CartID");
            } else {
                // If no cart exists, create one
                createCartForCustomer(customerId);
                // Try again recursively or with new connection
                return getCartIdByCustomerId(customerId);
            }
        }
    }


    public List<String> getCartItemsDescription(int cartId) throws SQLException {
        List<String> items = new ArrayList<>();
        String sql = "SELECT b.Title, ci.Quantity FROM CartItems ci JOIN Book b ON ci.BookID = b.BookID WHERE ci.CartID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cartId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String title = rs.getString("Title");
                int qty = rs.getInt("Quantity");
                items.add(title + " - Qty: " + qty);
            }
        }
        return items;
    }
    public List<CartItem> getCartItems(int cartID) throws SQLException {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT ci.CartItemID, ci.CartID, ci.BookID, ci.Quantity, b.Title, b.Price " +
                "FROM CartItems ci JOIN Book b ON ci.BookID = b.BookID WHERE ci.CartID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cartID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CartItem item = new CartItem(
                        rs.getInt("CartItemID"),
                        rs.getInt("CartID"),
                        rs.getInt("BookID"),
                        rs.getString("Title"),
                        rs.getInt("Quantity"),
                        rs.getDouble("Price")
                );
                items.add(item);
            }
        }
        return items;
    }
    public int createOrder(int customerId) throws SQLException {
        String sql = "INSERT INTO `Order` (CustomerID, OrderDate) VALUES (?, NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, customerId);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // OrderID
            }
        }
        return -1;
    }

    public boolean createOrderDetails(int orderId, List<CartItem> items) throws SQLException {
        String sql = "INSERT INTO OrderDetails (OrderID, BookID, Quantity, Subtotal) " +
                "VALUES (?, ?, ?, (SELECT Price FROM Book WHERE BookID = ?) * ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (CartItem item : items) {
                stmt.setInt(1, orderId);
                stmt.setInt(2, item.getBookID());
                stmt.setInt(3, item.getQuantity());
                stmt.setInt(4, item.getBookID());
                stmt.setInt(5, item.getQuantity());
                stmt.addBatch();
            }
            stmt.executeBatch();
            return true;
        }
    }

    public void clearCart(int cartId) throws SQLException {
        String sql = "DELETE FROM CartItems WHERE CartID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cartId);
            stmt.executeUpdate();
        }
    }
    public void createCartForCustomer(int customerId) throws SQLException {
        String sql = "INSERT INTO Cart (CustomerID, CreatedDate) VALUES (?, CURRENT_DATE)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.executeUpdate();
        }
    }



}

