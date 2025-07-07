package dao;
import util.DBConnection;

import model.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Book")) {

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("BookID"),
                        rs.getString("Title"),
                        rs.getDouble("Price"),
                        rs.getInt("StockQuantity"),
                        rs.getInt("PublicationYear"),
                        rs.getString("BookType")
                );
                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }
}

