package model;

public class OrderDetail {
    private String bookTitle;
    private int quantity;
    private double price;
    private double subtotal;

    public OrderDetail(String bookTitle, int quantity, double price, double subtotal) {
        this.bookTitle = bookTitle;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal;
    }

    // Getters and setters
    public String getBookTitle() { return bookTitle; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getSubtotal() { return subtotal; }
}

