package model;

public class CartItem {
    private int cartItemID;
    private int cartID;
    private int bookID;
    private String bookTitle;   // NEW
    private int quantity;
    private double price;       // NEW

    public CartItem(int cartItemID, int cartID, int bookID, String bookTitle, int quantity, double price) {
        this.cartItemID = cartItemID;
        this.cartID = cartID;
        this.bookID = bookID;
        this.bookTitle = bookTitle;
        this.quantity = quantity;
        this.price = price;
    }

    public int getCartItemID() { return cartItemID; }
    public int getCartID() { return cartID; }
    public int getBookID() { return bookID; }
    public String getBookTitle() { return bookTitle; }    // NEW getter
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }            // NEW getter

    @Override
    public String toString() {
        return "CartItem #" + cartItemID +
                " | CartID: " + cartID +
                " | BookID: " + bookID +
                " | Title: " + bookTitle +
                " | Qty: " + quantity +
                " | Price: " + price;
    }
}


