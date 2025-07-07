package model;

import java.time.LocalDate;

public class Order {

    private int orderID;
    private LocalDate orderDate;
    private String OrderStatus;

    public Order(int orderID, LocalDate orderDate, String OrderStatus) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.OrderStatus = OrderStatus;
    }

    public int getOrderID() {
        return orderID;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }
    public String getOrderStatus() { return OrderStatus; }
    public void setStatus(String status) { this.OrderStatus = status; }
}

