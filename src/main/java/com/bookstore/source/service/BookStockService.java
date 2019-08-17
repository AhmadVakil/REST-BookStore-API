package com.bookstore.source.service;

import com.bookstore.source.dao.BookStockDao;
import com.bookstore.source.entity.Admin;
import com.bookstore.source.entity.Book;
import com.bookstore.source.entity.Order;
import com.bookstore.source.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BookStockService {

    @Autowired
    private BookStockDao bookStockDao;

    public List<Book> getAllBooks() throws Exception {
        return this.bookStockDao.getAllBooks();
    }

    public List<Order> getAllBooksFromOrdersTable() throws Exception {
        return this.bookStockDao.getAllBooksFromOrdersTable();
    }

    public Map<String, Object> addNewBookToStock(Book book) throws Exception {
        return this.bookStockDao.addNewBookToStock(book);
    }

    public Map<String, Object> isUserExist(String userId) throws Exception {
        return this.bookStockDao.isUserExist(userId);
    }

    public Map<String, Object> isAdminExist(String adminUserName) throws Exception {
        return this.bookStockDao.isAdminExist(adminUserName);
    }

    public Map<String, Object> addNewAdmin(Admin admin) throws Exception {
        return this.bookStockDao.addNewAdmin(admin);
    }

    public Map<String, Object> addNewUser(User user) throws Exception {
        return this.bookStockDao.addNewUser(user);
    }

    public List<Admin> getAllAdmins() throws Exception {
        return this.bookStockDao.getAllAdmins();
    }

    public Map<String, Object> isAdminPasswordCorrect(String username, String password) throws Exception {
        return this.bookStockDao.isAdminPasswordCorrect(username, password);
    }

    public List<User> getAllUsers() throws Exception {
        return this.bookStockDao.getAllUsers();
    }

    public Map<String, Object> isBookExist(Book book) throws Exception {
        return this.bookStockDao.isBookExist(book);
    }

    public void deleteTheEntireDb() throws Exception {
        this.bookStockDao.deleteTheEntireDb();
    }

}
