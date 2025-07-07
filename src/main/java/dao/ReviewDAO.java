package dao;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReviewDAO {

    public void addReview(int customerID, int bookID, int rating, String reviewText) {
        String sql = "INSERT INTO Review (CustomerID, BookID, Rating, ReviewText, ReviewDate) VALUES (?, ?, ?, ?, CURRENT_DATE)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerID);
            stmt.setInt(2, bookID);
            stmt.setInt(3, rating);
            stmt.setString(4, reviewText);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Review submitted!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

