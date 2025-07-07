package model;

public class Book {
    private int bookID;
    private String title;
    private double price;
    private int stockQuantity;
    private int publicationYear;
    private String bookType;

    public Book(int bookID, String title, double price, int stockQuantity, int publicationYear, String bookType) {
        this.bookID = bookID;
        this.title = title;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.publicationYear = publicationYear;
        this.bookType = bookType;
    }

    public int getBookID() { return bookID; }
    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
    public int getPublicationYear() { return publicationYear; }
    public String getBookType() { return bookType; }

    @Override
    public String toString() {
        return bookID + ": " + title + " | R" + price + " | Stock: " + stockQuantity + " | " + bookType;
    }
}

