package com.bookstore.source.dao;

import com.bookstore.source.entity.Book;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.*;

@Repository
public class ShoppingBasketDao {

    public static BookShopDB basketData = DBConnection.dbConnection();

    public static List<Book> getAllBooksByUserId(String userId) throws Exception {
        ResultSet rs = basketData.getAllBooksInBasketByUserId(userId);
        List<Book> rows = new ArrayList<>();
        while (rs.next()) {
            Book book = new Book(rs.getString("title"), rs.getString("author"), new BigDecimal(rs.getString("price")),
                    rs.getInt("quantity"), rs.getString("description"));
            rows.add(book);
        }
        return rows;
    }

    public static List<Book> searchBooks(String searchString) throws Exception {
        ResultSet rs = basketData.displaySetOfAllBooksInStock();
        List<Book> rows = new ArrayList<>();
        while (rs.next()) {
            if (rs.getString("title").toLowerCase().contains(searchString.toLowerCase()) ||
                    rs.getString("author").toLowerCase().contains(searchString.toLowerCase())) {
                Book book = new Book(rs.getString("title"), rs.getString("author"), new BigDecimal(rs.getString("price")),
                        rs.getInt("quantity"), rs.getString("description"));
                rows.add(book);
            }
        }
        return rows;
    }

    public static Map<String, Object> getPriceOfTheBasket(String userId) throws Exception {
        Map<String, Object> response = new HashMap<>();
        if (!basketData.isUserExist(userId)) {
            response.put("message", "The userId does not exist. The user should be added first");
            response.put("isUserExist", false);
        } else {
            BigDecimal totalPrice = basketData.getPriceOfAllBooksInBasketByUserId(userId);
            response.put("message", "The user with the userId " + userId + "does exist.");
            response.put("isUserExist", true);
            response.put("totalPriceOfTheBasket", totalPrice);
        }
        return response;
    }

    public static Map<String, Object> addNewBookToUserBasket(Book newBook, String userId) throws Exception {
        Map<String, Object> response = new HashMap<>();
        basketData.addNewBookToUserBasket(newBook, userId);
        System.out.println("\nAdding new book to basket\nBook with the following information is sent to the server:\n" + newBook.toString());
        response.put("message", "Book is added to the basket. Buy the book(s) to place your orders.");
        response.put("isBookExist", true);
        return response;
    }

    public static Map<String, Object> bookOrUserNotExist(Book book, String userId) throws Exception {
        Map<String, Object> response = new HashMap<>();
        if (!basketData.isUserExist(userId)) {
            response.put("message", "User does not exist in the system.");
            response.put("isUserExist", false);
        } else if (book.getQuantity() < 1) {
            response.put("message", "The quantity can not be negative or 0.");
        } else if (!basketData.isBookExistInStock(book.getAuthor(), book.getTitle(), book.getPrice())) {
            response.put("message", "Requested book is not available in the stock");
            response.put("isBookExist", false);
        } else if (basketData.availableQuantity(book.getAuthor(), book.getTitle(), book.getPrice()) < book.getQuantity()) {
            response.put("message", "Requested quantity is not available in the stock");
            response.put("isQuantityAvailable", false);
            response.put("AvailableQuantity", basketData.availableQuantity(book.getAuthor(), book.getTitle(), book.getPrice()));
        }
        return response;
    }

    public static Map<String, Object> getAvailableQuantity(Book book) throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("availableQuantity", basketData.availableQuantity(book.getAuthor(), book.getTitle(), book.getPrice()));
        return response;
    }

    public static Map<String, Integer> bookQuantityInTheBasket(Book book) throws Exception {
        Map<String, Integer> response = new HashMap<>();
        response.put("bookQuantityInTheBasket", basketData.bookQuantityInTheBasket(book.getAuthor(), book.getTitle(), book.getPrice()));
        return response;
    }

    public static boolean isBasketEmpty(String userId) throws Exception {
        return basketData.isBasketEmpty(userId);
    }

    public static Map<String, Object> deleteBookFromShoppingBasket(Book book, String userId) throws Exception {
        Map<String, Object> response = new HashMap<>();
        if (!basketData.isUserExist(userId)) {
            response.put("message", "User does not exist in the system.");
            response.put("isUserExist", false);
        } else if (!basketData.isBookExistInStock(book.getAuthor(), book.getTitle(), book.getPrice())) {
            response.put("message", "Requested book is not available in the shopping basket");
            response.put("isBookExist", false);
        } else if (basketData.bookQuantityInTheBasket(book.getAuthor(), book.getTitle(), book.getPrice()) < book.getQuantity()) {
            response.put("message", "Requested quantity is not available in the shopping basket");
            response.put("isQuantityAvailable", false);
            response.put("AvailableQuantity", basketData.bookQuantityInTheBasket(book.getAuthor(), book.getTitle(), book.getPrice()));
        } else {
            basketData.deleteBookFromShoppingBasket(book, userId);
            response.put("message", "Book has been deleted from the basket");
            response.put("isBookDeleted", true);
        }
        return response;
    }

    public static void deleteAllBooksFromBasket(String userId) throws Exception {
        basketData.cleanShoppingBasketTable(userId);
    }

    public static Map<String, Object> buyBooks(String userId) throws Exception {
        Map<String, Object> response = new HashMap<>();
        if (!basketData.isUserExist(userId)) {
            response.put("message", "User does not exist in the system.");
            response.put("isUserExist", false);
        } else if (isBasketEmpty(userId)) {
            response.put("message", "There is no book in the basket.");
            response.put("isBasketEmpty", true);
        } else {
            basketData.addBooksToOrdersTable(userId);
            response.put("message", "Books are added into the order table");
            response.put("isBuyingProcessComplete", true);
        }

        return response;
    }
}

