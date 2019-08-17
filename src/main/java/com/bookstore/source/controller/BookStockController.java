package com.bookstore.source.controller;

import com.bookstore.source.entity.Book;
import com.bookstore.source.entity.Admin;
import com.bookstore.source.entity.Order;
import com.bookstore.source.entity.User;
import com.bookstore.source.service.BookStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookStock")
public class BookStockController {

    @Autowired
    private BookStockService bookStockService;

    @RequestMapping(method = RequestMethod.GET)
    public List<Book> getAllBooks() throws Exception {
        return bookStockService.getAllBooks();
    }

    @RequestMapping(value = "/showOrders", method = RequestMethod.GET)
    public List<Order> getAllBooksFromOrdersTable() throws Exception {
        return bookStockService.getAllBooksFromOrdersTable();
    }

    @RequestMapping(value = "/showAdmins", method = RequestMethod.GET)
    public List<Admin> getAllAdmins() throws Exception {
        return bookStockService.getAllAdmins();
    }

    @RequestMapping(value = "/showUsers", method = RequestMethod.GET)
    public List<User> getAllUsers() throws Exception {
        return bookStockService.getAllUsers();
    }

    @RequestMapping(value = "/isAdminExist/adminUserName={adminUserName}", method = RequestMethod.GET)
    public Map<String, Object> isAdminExist(@PathVariable("adminUserName") String adminUserName) throws Exception {
        return bookStockService.isAdminExist(adminUserName);
    }

    @RequestMapping(value = "/isUserExist/userId={userId}", method = RequestMethod.GET)
    public Map<String, Object> isUserExist(@PathVariable("userId") String userId) throws Exception {
        return bookStockService.isUserExist(userId);
    }

    @RequestMapping(value = "/addBook", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> addNewBookToStock(@RequestBody Book book) throws Exception {
        return bookStockService.addNewBookToStock(book);
    }

    @RequestMapping(value = "/isBookExist", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> isBookExist(@RequestBody Book book) throws Exception {
        return bookStockService.isBookExist(book);
    }

    //In real implementation the site needs to be https to avoid attackers sniffing the password.
    //Also, all requests to server can use a token. The token is stored as cookie on user's machine and can have expiry time for session invalidation.
    @RequestMapping(value = "/addNewAdmin", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> addNewAdmin(@RequestBody Admin admin) throws Exception {
        return bookStockService.addNewAdmin(admin);
    }

    @RequestMapping(value = "/addNewUser", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> addNewUser(@RequestBody User user) throws Exception {
        return bookStockService.addNewUser(user);
    }

    @RequestMapping(value = "/isAdminPasswordCorrect", method = RequestMethod.POST)
    public Map<String, Object> isAdminPasswordCorrect(@RequestBody Map<String, String> adminPassword) throws Exception {
        return bookStockService.isAdminPasswordCorrect(adminPassword.get("username"), adminPassword.get("password"));
    }

}
