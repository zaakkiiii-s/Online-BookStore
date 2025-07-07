package dao;
import util.DBConnection;


import model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Customer")) {

            while (rs.next()) {
                Customer c = new Customer(
                        rs.getInt("CustomerID"),
                        rs.getString("NameSurname"),
                        rs.getString("Email"),
                        rs.getString("PhoneNumber")
                );
                customers.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customers;
    }
    public boolean registerCustomer(String nameSurname, String email, String phone, String address, String type, String password) {
        String sql = """
        INSERT INTO Customer (NameSurname, Email, PhoneNumber, Address, CustomerType, Password)
        VALUES (?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nameSurname);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, address);
            stmt.setString(5, type);
            stmt.setNString(6, password);


            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public int loginCustomer(String email) {
        String sql = "SELECT CustomerID FROM Customer WHERE Email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int customerId = rs.getInt("CustomerID");
                System.out.println("✅ Login successful. Your Customer ID: " + customerId);
                return customerId;
            } else {
                System.out.println("❌ No account found with this email.");
                return -1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    public boolean checkEmailExists(String email, String password) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Customer WHERE Email = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public int getCustomerIdByEmail(String email) throws SQLException {
        String sql = "SELECT CustomerID FROM Customer WHERE Email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("CustomerID");
            } else {
                return -1; // or throw exception if preferred
            }
        }
    }

}

