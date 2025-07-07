package controller;

import dao.CartDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import model.CartItem;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CartController {

    @FXML
    private TableView<CartItem> cartTable;

    @FXML
    private TableColumn<CartItem, String> bookTitleCol;

    @FXML
    private TableColumn<CartItem, Integer> quantityCol;

    @FXML
    private TableColumn<CartItem, Double> priceCol;

    @FXML
    private TableColumn<CartItem, Button> removeCol;

    @FXML
    private Label totalAmountLabel;

    @FXML private Button payNowButton;

    private int lastOrderId = -1;  // store the order ID to use in payment screen


    private final CartDAO cartDAO = new CartDAO();

    private int currentCustomerId;

    public void setCurrentCustomerId(int customerId) {
        this.currentCustomerId = customerId;
        loadCartItems();
    }

    private void loadCartItems() {
        try {
            int cartId = cartDAO.getCartIdByCustomerId(currentCustomerId);
            if (cartId == -1) {
                showAlert("Cart not found.");
                return;
            }

            // Fetch CartItems (make sure your DAO returns CartItem objects with bookTitle, quantity, price, bookID)
            List<CartItem> items = cartDAO.getCartItems(cartId);

            cartTable.setItems(FXCollections.observableArrayList(items));

            // Setup columns
            bookTitleCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
            quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

            // Remove button column
            removeCol.setCellFactory(col -> new TableCell<>() {
                private final Button removeButton = new Button("Remove");

                {
                    removeButton.setOnAction(event -> {
                        CartItem item = getTableView().getItems().get(getIndex());
                        cartDAO.removeItemFromCart(cartId, item.getBookID());
                        loadCartItems();  // refresh after removal
                    });
                }

                @Override
                protected void updateItem(Button item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : removeButton);
                }
            });

            updateTotal();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Failed to load cart items.");
        }
    }

    private void updateTotal() {
        double total = cartTable.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        totalAmountLabel.setText(String.format("%.2f", total));
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void goBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();

            DashboardController controller = loader.getController();
            controller.setCurrentCustomerId(currentCustomerId);  // ✅ Pass the ID again!

            Stage stage = (Stage) cartTable.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
            stage.setTitle("BookStore Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void placeOrder() {
        try {
            int cartId = cartDAO.getCartIdByCustomerId(currentCustomerId);
            if (cartId == -1) {
                showAlert("Cart not found.");
                return;
            }

            List<CartItem> items = cartDAO.getCartItems(cartId);
            if (items.isEmpty()) {
                showAlert("Your cart is empty.");
                return;
            }

            // Step 1: Create order
            int orderId = cartDAO.createOrder(currentCustomerId);
            if (orderId == -1) {
                showAlert("Failed to create order.");
                return;
            }

            // Step 2: Add items
            boolean allInserted = cartDAO.createOrderDetails(orderId, items);
            if (!allInserted) {
                showAlert("Failed to add items to order.");
                return;
            }

            cartDAO.clearCart(cartId);

            lastOrderId = orderId;
            payNowButton.setDisable(false);  // ✅ Enable payment button
            showConfirmation("✅ Order placed. You can now pay.");

            cartTable.getItems().clear();
            totalAmountLabel.setText("0.00");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error placing order: " + e.getMessage());
        }
    }



    private void showConfirmation(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    private void openPaymentScreen(int orderId, int customerId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Payment.fxml"));
            Parent root = loader.load();

            controller.PaymentController paymentController = loader.getController();
            paymentController.setCurrentOrderId(orderId);
            paymentController.setCurrentCustomerId(customerId);

            Stage stage = (Stage) cartTable.getScene().getWindow();  // You can also use any other control
            stage.setScene(new Scene(root, 400, 300));
            stage.setTitle("Payment");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Unable to load payment screen.");
        }
    }
    @FXML
    private void goToPayment() {
        if (lastOrderId == -1) {
            showAlert("No order to pay for.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Payment.fxml"));
            Parent root = loader.load();

            PaymentController paymentController = loader.getController();
            paymentController.setCurrentCustomerId(currentCustomerId);
            paymentController.setCurrentOrderId(lastOrderId);

            Stage stage = (Stage) cartTable.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
            stage.setTitle("Payment");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}

