package controller;


import dao.CustomerDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.sql.SQLException;


public class LoginController {

    public PasswordField passwordField;
    @FXML
    private TextField emailField;

    @FXML
    private Label messageLabel;

    private final CustomerDAO customerDAO = new CustomerDAO();

    @FXML
    private Button loginButton;  // or any other node in your FXML


    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();
        if (email == null || !email.contains("@")|| password.isEmpty()) {
            messageLabel.setText("❌ Please enter a valid email.");
            return;
        }

        try {
            if (customerDAO.checkEmailExists(email, password)) {
                messageLabel.setText("✅ Login successful!");
                openDashboard();
            } else {
                messageLabel.setText("❌ Incorrecrt email or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("❌ Error during login.");
        }
    }


    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            DashboardController dashboardController = loader.getController();
            int customerId = customerDAO.getCustomerIdByEmail(emailField.getText());
            dashboardController.setCurrentCustomerId(customerId);

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(dashboardRoot, 600, 400));
            stage.setTitle("BookStore Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openRegister() {
        try {
            Parent registerRoot = FXMLLoader.load(getClass().getResource("/Register.fxml"));
            Scene registerScene = new Scene(registerRoot, 400, 400);
            Stage stage = (Stage) emailField.getScene().getWindow();  // assuming emailField is declared
            stage.setScene(registerScene);
            stage.setTitle("Register New Customer");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Inside your LoginController, when login is successful:

    @FXML
    private void onLoginSuccess() throws IOException {
        // Load BookView.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/BookView.fxml"));
        Parent bookViewRoot = loader.load();

        // Get current stage from a node in login scene, e.g. login button or any control
        Stage stage = (Stage) loginButton.getScene().getWindow();

        // Switch scene to BookView
        stage.setScene(new Scene(bookViewRoot, 600, 400));
        stage.setTitle("Available Books");
        stage.show();
    }



}


