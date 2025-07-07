package org.example;

import dao.*;
import model.Book;
import model.Customer;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    public static void run() {
        BookDAO bookDAO = new BookDAO();
        System.out.println("Book List:");
        for (Book book : bookDAO.getAllBooks()) {
            System.out.println(book);
        }

        CustomerDAO dao = new CustomerDAO();
        List<Customer> customers = dao.getAllCustomers();

        for (Customer c : customers) {
            System.out.println(c);
        }

        CartDAO cartDAO = new CartDAO();
        cartDAO.addItemToCart(1, 1, 2);
        cartDAO.viewCart(1);

        OrderDAO orderDAO = new OrderDAO();
        orderDAO.placeOrder(1);

        PaymentDAO paymentDAO = new PaymentDAO();
        paymentDAO.makePayment(5, 299.99);

        ReviewDAO reviewDAO = new ReviewDAO();
        reviewDAO.addReview(1, 2, 5, "Absolutely loved this eBook! 🌟");

        OrderHistoryDAO historyDAO = new OrderHistoryDAO();
        historyDAO.viewOrderHistory(1);

        Scanner sc = new Scanner(System.in);
        CustomerDAO customerDAO = new CustomerDAO();

        System.out.println("📘 Welcome to BookStoreApp");
        System.out.print("Do you want to (1) Register or (2) Login? ");
        int choice = sc.nextInt();
        sc.nextLine();

        int customerId = -1;

        if (choice == 1) {
            System.out.print("Full Name: ");
            String name = sc.nextLine();
            System.out.print("Email: ");
            String email = sc.nextLine();
            System.out.print("Phone (10 digits): ");
            String phone = sc.nextLine();
            System.out.print("Address: ");
            String address = sc.nextLine();
            System.out.print("Password: ");
            String password = sc.nextLine();
            System.out.print("Customer Type (Regular/Premium): ");
            String type = sc.nextLine();

            boolean registered = customerDAO.registerCustomer(name, email, phone, address, type, password);
            if (registered) {
                System.out.println("✅ Registration successful!");
                customerId = customerDAO.loginCustomer(email);
            }

        } else if (choice == 2) {
            System.out.print("Enter your email: ");
            String email = sc.nextLine();
            customerId = customerDAO.loginCustomer(email);
        }

        if (customerId > 0) {
            System.out.println("🎉 Welcome, Customer #" + customerId + "!");
        }
    }
}

