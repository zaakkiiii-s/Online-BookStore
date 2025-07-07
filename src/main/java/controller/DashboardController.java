package controller;

import dao.BookDAO;
import dao.CartDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Book;

import java.io.IOException;
import java.sql.SQLException;

public class DashboardController {

    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, Integer> colBookID;
    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, Double> colPrice;
    @FXML private TableColumn<Book, Integer> colStock;
    @FXML private TableColumn<Book, TextField> colQuantity;
    @FXML private TableColumn<Book, Button> colAdd;
    @FXML private Label messageLabel;

    private final BookDAO bookDAO = new BookDAO();
    private final CartDAO cartDAO = new CartDAO();

    private int currentCustomerId;

    @FXML
    public void initialize() {
        colBookID.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        colQuantity.setCellValueFactory(param -> {
            TextField tf = new TextField("1");
            tf.setMaxWidth(50);
            return new javafx.beans.property.SimpleObjectProperty<>(tf);
        });

        colAdd.setCellFactory(col -> new TableCell<>() {
            private final Button addButton = new Button("Add");

            {
                addButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    TextField quantityField = colQuantity.getCellObservableValue(book).getValue();
                    int qty;
                    try {
                        qty = Integer.parseInt(quantityField.getText());
                        if (qty <= 0) throw new NumberFormatException();
                    } catch (NumberFormatException e) {
                        messageLabel.setText("Enter a valid quantity for book ID " + book.getBookID());
                        return;
                    }
                    if (qty > book.getStockQuantity()) {
                        messageLabel.setText("Not enough stock for book ID " + book.getBookID());
                        return;
                    }

                    try {
                        int cartId = cartDAO.getCartIdByCustomerId(currentCustomerId);
                        if (cartId == -1) {
                            // Try to create a new cart for this customer
                            cartDAO.createCartForCustomer(currentCustomerId);
                            cartId = cartDAO.getCartIdByCustomerId(currentCustomerId);
                        }

                        boolean success = cartDAO.addItemToCart(cartId, book.getBookID(), qty);
                        if (success) {
                            messageLabel.setText("Added " + qty + " of '" + book.getTitle() + "' to cart.");
                        } else {
                            messageLabel.setText("Failed to add to cart.");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        messageLabel.setText("❌ Database error.");
                    }
                });
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(addButton);
                }
            }
        });
    }
    public void setCurrentCustomerId(int customerId) {
        this.currentCustomerId = customerId;
        System.out.println("✅ Dashboard loaded for customer ID: " + customerId);
        loadBooks(); // <- only call loadBooks here after customer ID is available
    }



    private void loadBooks() {
        ObservableList<Book> books = FXCollections.observableArrayList(bookDAO.getAllBooks());
        bookTable.setItems(books);
    }
    @FXML
    private void openCart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CartView.fxml"));
            Parent root = loader.load();

            // Pass customer ID to CartController
            CartController controller = loader.getController();
            controller.setCurrentCustomerId(currentCustomerId);

            Stage stage = (Stage) bookTable.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
            stage.setTitle("Your Cart");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void openOrderHistory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OrderHistory.fxml"));
            Parent root = loader.load();

            OrderHistoryController controller = loader.getController();
            controller.setCurrentCustomerId(currentCustomerId);  // ✅ again here

            Stage stage = (Stage) bookTable.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
            stage.setTitle("Your Orders");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

