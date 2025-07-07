package dao;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PaymentDAO {

    public void makePayment(int orderID, double amount) {
        String sql = "INSERT INTO Payment (OrderID, AmountPaid, PaymentMethod, PaymentStatus) VALUES (?, ?, 'Card', 'Paid')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderID);
            stmt.setDouble(2, amount);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Payment made successfully for OrderID: " + orderID);
            } else {
                System.out.println("❌ Payment failed.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

