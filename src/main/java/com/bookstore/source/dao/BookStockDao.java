package com.bookstore.source.dao;

import com.bookstore.source.entity.Admin;
import com.bookstore.source.entity.Book;
import com.bookstore.source.entity.Order;
import com.bookstore.source.entity.User;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.util.*;

@Repository
public class BookStockDao {

    public static BookShopDB stockData = DBConnection.dbConnection();

    public static void loadTheStock() throws Exception {

        deleteTheEntireDb();
        URL url = new URL("https://www.webriders.se/other/bookstoredata.txt");
        Scanner textFile = new Scanner(url.openStream());
        while (textFile.hasNext()) {
            String[] values = textFile.nextLine().split(";");
            String bookTitle = values[0];
            String bookAuthor = values[1];
            BigDecimal price = new BigDecimal(values[2].replaceAll(",", ""));
            int quantity = Integer.parseInt(values[3]);
            Book newBook = new Book(bookTitle, bookAuthor, price, quantity, "Dummy description");
            stockData.addBookToStock(newBook);
        }

        // Creating default admin for the store
        Admin Dummy_Admin = new Admin("Dummy_Admin", "Dummy", "your_firstname",
                "your_lastname", "A dummy address", "A dummy email", "12345-123");

        // Creating a default user or memberships
        User Dummy_User = new User("user_firstname", "user_lastname", "user_address",
                "user_email", "123456-123", "Dummy_User");

        stockData.addNewAdmin(Dummy_Admin);
        stockData.addNewUser(Dummy_User);
    }

    public static List<Book> getAllBooks() throws Exception {
        ResultSet rs = stockData.displaySetOfAllBooksInStock();
        List<Book> books = new ArrayList<>();
        while (rs.next()) {
            Book book = new Book(rs.getString("title"), rs.getString("author"), new BigDecimal(rs.getString("price")),
                    rs.getInt("quantity"), rs.getString("description"));
            books.add(book);
        }
        return books;
    }

    public static List<Order> getAllBooksFromOrdersTable() throws Exception {
        ResultSet rs = stockData.displaySetOfAllBooksInOrdersTable();
        List<Order> orders = new ArrayList<>();
        while (rs.next()) {
            Order order = new Order(rs.getString("userid"), rs.getString("title"), rs.getString("author"),
                    new BigDecimal(rs.getString("price")), rs.getInt("quantity"));
            orders.add(order);
        }
        return orders;
    }

    public List<Admin> getAllAdmins() throws Exception {
        ResultSet rs = stockData.getAllAdmins();
        List<Admin> admins = new ArrayList<>();
        while (rs.next()) {
            Admin admin = new Admin(rs.getString("username"), rs.getString("password").replaceAll(".", "*"),
                    rs.getString("firstname"), rs.getString("lastname"), rs.getString("address"),
                    rs.getString("email"), rs.getString("personalnumber").replaceAll(".", "*"));
            admins.add(admin);
        }
        return admins;
    }

    public static List<User> getAllUsers() throws Exception {
        ResultSet rs = stockData.getAllUsers();
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            User user = new User(rs.getString("firstname"), rs.getString("lastname"), rs.getString("address"),
                    rs.getString("email"), rs.getString("personalnumber"), rs.getString("userid"));
            users.add(user);
        }
        return users;
    }

    public static Map<String, Object> addNewBookToStock(Book newBook) throws Exception {
        Map<String, Object> response = new HashMap<>();
        if (newBook.getQuantity() < 1) {
            response.put("message", "The quantity can not be negative or 0.");
            response.put("isBookAdded", false);
        } else if (stockData.isBookExistInStock(newBook.getAuthor(), newBook.getTitle(), newBook.getPrice())) {
            stockData.addBookToStock(newBook);
            response.put("message", "Book has been added to the stock before. Only the quantity will be updated.");
            response.put("isQuantityUpdated", true);
        } else {
            stockData.addBookToStock(newBook);
            response.put("message", "Book is added as a new book to the stock.");
            response.put("isBookAdded", true);
        }
        return response;
    }

    public static Map<String, Object> isUserExist(String userId) throws Exception {
        Map<String, Object> response = new HashMap<>();
        if (stockData.isUserExist(userId)) {
            response.put("message", "User Exist in the system.");
            response.put("isUserExist", true);
        } else {
            response.put("message", "User does not exist in the system.");
            response.put("isUserExist", false);
        }
        return response;
    }

    public static Map<String, Object> isAdminExist(String adminUserName) throws Exception {
        Map<String, Object> response = new HashMap<>();
        if (stockData.isAdminUserExist(adminUserName)) {
            response.put("message", "Admin Exist in the system.");
            response.put("isAdminExist", true);
        } else {
            response.put("message", "Admin does not exist in the system.");
            response.put("isAdminExist", false);
        }
        return response;
    }

    public static Map<String, Object> isBookExist(Book book) throws Exception {
        Map<String, Object> response = new HashMap<>();
        if (stockData.isBookExistInStock(book.getAuthor(), book.getTitle(), book.getPrice())) {
            response.put("message", "Book is exist in the stock.");
            response.put("isBookExist", true);
        } else {
            response.put("message", "Book does not exist in the stock.");
            response.put("isBookExist", false);
        }
        return response;
    }

    public static Map<String, Object> addNewAdmin(Admin admin) throws Exception {
        Map<String, Object> response = new HashMap<>();

        if (admin.getUsername() == null || admin.getPassword() == null) {
            response.put("message", "Username or password can not be null. Please chose at least one username and one password.");
            response.put("isAdminAdded", false);
        } else if (admin.getPassword().length() < 7) {
            response.put("message", "The password must be at least 7 characters.");
            response.put("isAdminAdded", false);
        } else if (!stockData.isAdminUserExist(admin.getUsername())) {
            stockData.addNewAdmin(admin);
            response.put("message", "New admin has been added into the system.");
            response.put("isAdminAdded", true);
        } else {
            response = isAdminExist(admin.getUsername());
        }
        return response;
    }

    public static Map<String, Object> addNewUser(User user) throws Exception {
        Map<String, Object> response = new HashMap<>();
        if (user.getUserid().length() < 2 || user.getUserid() == null) {
            response.put("message", "The user Id can not be either null or less than two characters");
        } else if (!stockData.isUserExist(user.getUserid())) {
            stockData.addNewUser(user);
            System.out.println("\nAdding new user\nFollowing information of the user is sent to the server:\n" + user.toString());
            response.put("message", "New user is added to the store");
            response.put("userFirstName", user.getFirstname());
            response.put("isUserExist", false);
        } else {
            System.out.println("User with the userId " + user.getUserid() + " is already exist.");
            response.put("message", "User Is Already Exist");
            response.put("userFirstName", user.getFirstname());
            response.put("isUserExist", true);
        }
        return response;
    }

    public static Map<String, Object> isAdminPasswordCorrect(String username, String password) throws Exception {
        Map<String, Object> response = new HashMap<>();
        if (!stockData.isAdminPasswordCorrect(username, password)) {
            response.put("message", "The password is incorrect");
            response.put("isAdminPasswordCorrect", false);
        } else {
            response.put("message", "The password is correct");
            response.put("isAdminPasswordCorrect", true);
        }
        return response;
    }

    public static void deleteTheEntireDb() throws Exception {
        stockData.deleteTheEntireDb();
    }
}
