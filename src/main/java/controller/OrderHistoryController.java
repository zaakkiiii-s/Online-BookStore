package controller;

import dao.OrderDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;


import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import model.Order;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class OrderHistoryController {

    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, Integer> colOrderId;
    @FXML private TableColumn<Order, String> colOrderDate;
    @FXML private TableColumn<Order, String> colOrderStatus;
    @FXML private TableColumn<Order, Void> colAction;

    private final OrderDAO orderDAO = new OrderDAO();
    private int currentCustomerId;

    public void setCurrentCustomerId(int customerId) {
        this.currentCustomerId = customerId;
        System.out.println("OrderHistoryController: currentCustomerId = " + customerId);
        loadOrders();
    }

    private void loadOrders() {
        try {
            List<Order> orders = orderDAO.getOrdersByCustomerId(currentCustomerId);
            ObservableList<Order> data = FXCollections.observableArrayList(orders);

            colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderID"));
            colOrderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
            colOrderStatus.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<Order, String>("orderStatus"));


            System.out.println("Customer ID: " + currentCustomerId + ", Orders found: " + orders.size());

            orderTable.setItems(data);
            setupActionColumn();
        } catch (SQLException e) {

            e.printStackTrace();
        }
        System.out.println("Fetching orders for customer ID: " + currentCustomerId);

    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();

            // Set customer ID again if needed
            controller.DashboardController dashboard = loader.getController();
            dashboard.setCurrentCustomerId(currentCustomerId);  // create setter if not there

            Stage stage = (Stage) orderTable.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setupActionColumn() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button viewButton = new Button("View");
            private final Button payButton = new Button("Pay");
            private final HBox hbox = new HBox(5, viewButton, payButton);

            {
                viewButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    System.out.println("View order: " + order.getOrderID());

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/OrderDetails.fxml"));
                        Parent root = loader.load();

                        OrderDetailsController controller = loader.getController();
                        controller.setOrderId(order.getOrderID());

                        Stage stage = new Stage(); // show in new window
                        stage.setScene(new Scene(root));
                        stage.setTitle("Order Details - #" + order.getOrderID());
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                payButton.setOnAction(event -> {
                        Order order = getTableView().getItems().get(getIndex());
                        if (!"Paid".equalsIgnoreCase(order.getOrderStatus())) {
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Payment.fxml"));
                                Parent root = loader.load();

                                PaymentController controller = loader.getController();
                                controller.setCurrentOrderId(order.getOrderID());

                                Stage stage = new Stage();
                                stage.setScene(new Scene(root));
                                stage.setTitle("Payment for Order #" + order.getOrderID());
                                stage.show();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Order already paid.");
                        }
                    });

                }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Order order = getTableView().getItems().get(getIndex());
                    payButton.setDisable("Paid".equalsIgnoreCase(order.getOrderStatus()));
                    setGraphic(hbox);
                }
            }
        });
    }


}

