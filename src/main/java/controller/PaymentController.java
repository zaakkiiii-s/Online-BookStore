package controller;

import dao.OrderDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.sql.SQLException;

public class PaymentController {

    @FXML
    private TextField cardHolderField;
    @FXML
    private TextField cardNumberField;
    @FXML
    private TextField expiryField;
    @FXML
    private PasswordField cvvField;
    @FXML
    private Label messageLabel;

    private int currentCustomerId;
    private int currentOrderId;

    public void setCurrentCustomerId(int id) {
        this.currentCustomerId = id;
    }

    public void setCurrentOrderId(int orderId) {
        this.currentOrderId = orderId;
    }

    @FXML
    private void handlePayment() {
        String cardHolder = cardHolderField.getText().trim();
        String cardNumber = cardNumberField.getText().trim();
        String expiry = expiryField.getText().trim();
        String cvv = cvvField.getText().trim();

        if (cardHolder.isEmpty() || cardNumber.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
            messageLabel.setText("❌ Please fill all fields.");
            return;
        }

        if (!cardNumber.matches("\\d{16}")) {
            messageLabel.setText("❌ Card number must be 16 digits.");
            return;
        }

        if (!cvv.matches("\\d{3}")) {
            messageLabel.setText("❌ CVV must be 3 digits.");
            return;
        }

        // 💳 Simulate mock payment logic
        boolean paymentSuccess = Math.random() > 0.1;  // 90% success rate for demo

        if (paymentSuccess) {
            // ✅ Update order status to Paid
            OrderDAO orderDAO = new OrderDAO();
            try {
                orderDAO.updateOrderStatus(currentOrderId, "Paid");
            } catch (SQLException e) {
                e.printStackTrace();
                messageLabel.setText("❌ Error updating order status.");
                return;
            }

            // ✅ Show success message and simulate email
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("✅ Payment successful!\n📧 A receipt has been sent to your email.");

            // Optional: Print to console for mock receipt
            System.out.println("📧 Sending receipt to customer " + currentCustomerId + " ...");
            System.out.println("Order ID: " + currentOrderId);
            System.out.println("Status: Paid");
            System.out.println("Thank you for your purchase!");

            // ✅ Delay and return to Dashboard
            new Thread(() -> {
                try {
                    Thread.sleep(2500); // 2.5 second delay so user sees message
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                javafx.application.Platform.runLater(this::goBackToDashboard);
            }).start();

        } else {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("❌ Payment failed. Please try again.");
        }
    }

    private void goBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();
            controller.DashboardController dashboard = loader.getController();
            dashboard.setCurrentCustomerId(currentCustomerId);

            Stage stage = (Stage) cardHolderField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
            stage.setTitle("BookStore Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


