package controller;

import dao.BookDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Book;

import java.util.List;

public class BookController {

    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book, Integer> idCol;
    @FXML
    private TableColumn<Book, String> titleCol;
    @FXML
    private TableColumn<Book, Double> priceCol;
    @FXML
    private TableColumn<Book, Integer> stockCol;

    private final BookDAO bookDAO = new BookDAO();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        refreshTable(); // load data initially
    }

    @FXML
    public void refreshTable() {
        List<Book> books = bookDAO.getAllBooks();
        ObservableList<Book> bookList = FXCollections.observableArrayList(books);
        bookTable.setItems(bookList);
    }
}
