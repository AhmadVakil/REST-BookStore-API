package com.bookstore.source.unit;

import static com.bookstore.source.dao.BookStockDao.loadTheStock;

import static org.junit.Assert.*;

import com.bookstore.source.dao.BookShopDB;
import com.bookstore.source.dao.DBConnection;
import com.bookstore.source.entity.Admin;
import com.bookstore.source.entity.Book;
import com.bookstore.source.entity.User;
import com.bookstore.source.service.ShoppingBasketService;
import com.bookstore.source.service.BookStockService;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class bookStoreTest {

    @Test
    public void bookStoreTest() throws Exception {
        BookStockService storeService = new BookStockService();
        ShoppingBasketService basketService = new ShoppingBasketService();
        BookShopDB dataBase = DBConnection.dbConnection();
        dataBase.testMode = true;
        Map<String, Object> bookStoreResponse;

        // Cleaning up the BookShopTest.db database for test purposes.
        dataBase.deleteTheEntireDb();

        // Creating a book object for the stock
        Book stockBook = new Book();
        stockBook.setTitle("A good title");
        stockBook.setAuthor("A good author");
        stockBook.setPrice(new BigDecimal("20"));
        stockBook.setQuantity(20);

        // Creating an out of stock book object for the stock
        Book outOfStockBook = new Book();
        outOfStockBook.setTitle("A rare title");
        outOfStockBook.setAuthor("A rare author");
        outOfStockBook.setPrice(new BigDecimal("2000"));
        outOfStockBook.setQuantity(0);
        dataBase.addBookToStock(outOfStockBook);

        // Creating a book object to request it from the stock
        Book requestedBook = new Book();
        requestedBook.setTitle("A good title");
        requestedBook.setAuthor("A good author");
        requestedBook.setPrice(new BigDecimal("20"));
        requestedBook.setQuantity(1);

        // Creating new admin for the store
        Admin admin = new Admin();
        admin.setUsername("Test_Admin");
        admin.setPassword("12345678");
        admin.setFirstname("Admin_FirstName");
        admin.setLastname("Admin_LastName");

        // Creating a new user to add it to the memberships
        User user = new User();
        user.setUserid("Test_User");
        user.setFirstname("User_FirstName");
        user.setLastname("User_LastName");

        // Test Adding a new user
        bookStoreResponse = storeService.addNewUser(user);
        assertEquals(bookStoreResponse.get("message"), "New user is added to the store");
        assertEquals(bookStoreResponse.get("userFirstName"), "User_FirstName");

        // Test Adding the same user again
        bookStoreResponse = storeService.addNewUser(user);
        assertEquals(bookStoreResponse.get("message"), "User Is Already Exist");
        assertEquals(bookStoreResponse.get("userFirstName"), "User_FirstName");

        // Test adding a new book to the stock
        bookStoreResponse = storeService.addNewBookToStock(stockBook);
        assertEquals(bookStoreResponse.get("message"), "Book is added as a new book to the stock.");
        assertEquals(bookStoreResponse.get("isBookAdded"), true);

        // Test adding the requested book, it should be the same as the stock otherwise it can't be added
        basketService.addNewBookToUserBasket(requestedBook, "Test_User");
        bookStoreResponse = basketService.getAvailableQuantity(stockBook);
        assertNotEquals(bookStoreResponse.get("availableQuantity"), 20);
        assertEquals(bookStoreResponse.get("availableQuantity"), 19);

        // Test adding the same book to the stock
        bookStoreResponse = storeService.addNewBookToStock(stockBook);
        assertEquals(bookStoreResponse.get("message"), "Book has been added to the stock before. Only the quantity will be updated.");
        assertEquals(bookStoreResponse.get("isQuantityUpdated"), true);
        bookStoreResponse = basketService.getAvailableQuantity(stockBook);
        assertEquals(bookStoreResponse.get("availableQuantity"), 39);

        // Test adding book to non existing user's basket
        bookStoreResponse = basketService.addNewBookToUserBasket(requestedBook, "Another_User");
        assertEquals(bookStoreResponse.get("message"), "User does not exist in the system.");
        assertEquals(bookStoreResponse.get("isUserExist"), false);

        // Test adding a new admin to the store
        bookStoreResponse = storeService.addNewAdmin(admin);
        assertEquals(bookStoreResponse.get("message"), "New admin has been added into the system.");
        assertEquals(bookStoreResponse.get("isAdminAdded"), true);

        // Test adding the same admin again to the store
        bookStoreResponse = storeService.addNewAdmin(admin);
        assertEquals(bookStoreResponse.get("message"), "Admin Exist in the system.");
        assertEquals(bookStoreResponse.get("isAdminExist"), true);

        // Test if the admin password is correct
        bookStoreResponse = storeService.isAdminPasswordCorrect(admin.getUsername(), "12345678");
        assertEquals(bookStoreResponse.get("message"), "The password is correct");
        assertEquals(bookStoreResponse.get("isAdminPasswordCorrect"), true);
        bookStoreResponse = storeService.isAdminPasswordCorrect(admin.getUsername(), "abcdefghi");
        assertEquals(bookStoreResponse.get("message"), "The password is incorrect");
        assertEquals(bookStoreResponse.get("isAdminPasswordCorrect"), false);

        // Test if the price of the basket will be updated and is correct
        bookStoreResponse = basketService.getPriceOfTheBasket(user.getUserid());
        assertEquals(bookStoreResponse.get("totalPriceOfTheBasket"), new BigDecimal("20.0"));
        requestedBook.setQuantity(2); // Adding two more of the requested book to the basket
        basketService.addNewBookToUserBasket(requestedBook, user.getUserid());
        bookStoreResponse = basketService.getPriceOfTheBasket(user.getUserid());
        assertEquals(bookStoreResponse.get("totalPriceOfTheBasket"), new BigDecimal("60.0"));

        // Test if we can exceed the quantity limit
        requestedBook.setQuantity(50); // Exceeding the quantity limit
        bookStoreResponse = basketService.addNewBookToUserBasket(requestedBook, user.getUserid());
        assertEquals(bookStoreResponse.get("message"), "Requested quantity is not available in the stock");
        assertEquals(bookStoreResponse.get("isQuantityAvailable"), false);

        // Test removing books from the basket
        requestedBook.setQuantity(3);
        basketService.deleteBookFromShoppingBasket(requestedBook, user.getUserid());
        bookStoreResponse = basketService.getPriceOfTheBasket(user.getUserid());
        assertEquals(bookStoreResponse.get("totalPriceOfTheBasket"), new BigDecimal("0.0"));

        // Test if the quantity of the book in the basket will be returned to the stock after removing them from the basket
        bookStoreResponse = basketService.getAvailableQuantity(stockBook);
        assertEquals(bookStoreResponse.get("availableQuantity"), 40);

        // Test if we can add a non available book to user's basket
        Book unavailableBook = new Book();
        unavailableBook.setTitle("A rare title");
        unavailableBook.setAuthor("A rare author");
        unavailableBook.setPrice(new BigDecimal("5000.0"));
        unavailableBook.setQuantity(1);
        bookStoreResponse = basketService.addNewBookToUserBasket(unavailableBook, "Test_User");
        assertEquals(bookStoreResponse.get("message"), "Requested book is not available in the stock");

        // Test the buy method statuses by passing existing, non existing and out of stock book
        Book existBook = new Book("A good title", "A good author", new BigDecimal("20"),
                1, "This is just a description");

        Book outOfStock = new Book("A rare title", "A rare author", new BigDecimal("2000"),
                1, "This is just a description");

        Book notExist = new Book("Not exist title", "Not exit author", new BigDecimal("123.0"),
                1, "This is just a description");

        assertEquals(basketService.buy(existBook, outOfStock, notExist)[0], 0);
        assertEquals(basketService.buy(existBook, outOfStock, notExist)[1], 1);
        assertEquals(basketService.buy(existBook, outOfStock, notExist)[2], 2);

    }
}
