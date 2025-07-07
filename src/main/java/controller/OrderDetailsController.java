package controller;

import dao.OrderDetailsDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.OrderDetail;

import java.util.List;

public class OrderDetailsController {

    @FXML private TableView<OrderDetail> detailsTable;
    @FXML private TableColumn<OrderDetail, String> colBookTitle;
    @FXML private TableColumn<OrderDetail, Integer> colQuantity;
    @FXML private TableColumn<OrderDetail, Double> colPrice;
    @FXML private TableColumn<OrderDetail, Double> colSubtotal;

    private int orderId;

    public void setOrderId(int orderId) {
        this.orderId = orderId;
        loadOrderDetails();
    }

    private void loadOrderDetails() {
        OrderDetailsDAO dao = new OrderDetailsDAO();
        List<OrderDetail> details = dao.getOrderDetailsByOrderId(orderId);
        ObservableList<OrderDetail> data = FXCollections.observableArrayList(details);

        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        detailsTable.setItems(data);
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) detailsTable.getScene().getWindow();
        stage.close();
    }
}
