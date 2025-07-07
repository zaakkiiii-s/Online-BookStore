package controller;

import dao.CustomerDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {


    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private ChoiceBox<String> customerTypeBox;
    @FXML private Label messageLabel;
    @FXML private PasswordField passwordField;
    private final CustomerDAO customerDAO = new CustomerDAO();

    @FXML
    public void initialize() {
        customerTypeBox.getItems().addAll("Regular", "Premium");
        customerTypeBox.setValue("Regular");
    }

    @FXML
    private void handleRegister() {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        String type = customerTypeBox.getValue();
        String password = passwordField.getText();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            messageLabel.setText("❌ Please fill all fields.");
            return;
        }

        if (!email.contains("@")) {
            messageLabel.setText("❌ Invalid email.");
            return;
        }

        if (phone.length() != 10) {
            messageLabel.setText("❌ Phone must be 10 digits.");
            return;
        }

        boolean success = customerDAO.registerCustomer(name, email, phone, address, type, password);
        if (success) {
            messageLabel.setText("✅ Registration successful! Please login.");
        } else {
            messageLabel.setText("❌ Registration failed. Email might already exist.");
        }
    }
    @FXML
    private void goToLogin() {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();  // or any control like emailField
            stage.setScene(new Scene(loginRoot, 400, 300)); // adjust size as needed
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
