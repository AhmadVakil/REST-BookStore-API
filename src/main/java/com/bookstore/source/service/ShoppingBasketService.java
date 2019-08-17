package com.bookstore.source.service;

import com.bookstore.source.dao.BookShopDB;
import com.bookstore.source.dao.DBConnection;
import com.bookstore.source.dao.ShoppingBasketDao;
import com.bookstore.source.dao.BookStockDao;
import com.bookstore.source.entity.Book;
import com.bookstore.source.entity.BookList;
import com.bookstore.source.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ShoppingBasketService implements BookList {

    public static BookShopDB basketData = DBConnection.dbConnection();

    @Autowired
    private ShoppingBasketDao shoppingBasketDao;
    private BookStockDao bookStockDao;

    public List<Book> getAllBooksByUserId(String userId) throws Exception {
        return this.shoppingBasketDao.getAllBooksByUserId(userId);
    }

    public Map<String, Object> getPriceOfTheBasket(String userId) throws Exception {
        return this.shoppingBasketDao.getPriceOfTheBasket(userId);
    }

    public Map<String, Object> addNewBookToUserBasket(Book book, String userId) throws Exception {
        if (add(book, book.getQuantity()) && basketData.isUserExist(userId) && buy(book)[0] == 0) {
            return this.shoppingBasketDao.addNewBookToUserBasket(book, userId);
        } else {
            return this.shoppingBasketDao.bookOrUserNotExist(book, userId);
        }
    }

    public Map<String, Object> deleteBookFromShoppingBasket(Book book, String userId) throws Exception {
        return this.shoppingBasketDao.deleteBookFromShoppingBasket(book, userId);
    }

    public void deleteAllBooksFromBasket(String userId) throws Exception {
        this.shoppingBasketDao.deleteAllBooksFromBasket(userId);
    }

    public Map<String, Object> getAvailableQuantity(Book book) throws Exception {
        return this.shoppingBasketDao.getAvailableQuantity(book);
    }

    public Map<String, Integer> bookQuantityInTheBasket(Book book) throws Exception {
        return this.shoppingBasketDao.bookQuantityInTheBasket(book);
    }

    public boolean isBasketEmpty(String userId) throws Exception {
        return this.shoppingBasketDao.isBasketEmpty(userId);
    }

    public Map<String, Object> buyBooks(String userId) throws Exception {
        return this.shoppingBasketDao.buyBooks(userId);
    }

    @Override
    public List<Book> list(String searchString) {
        try {
            return this.shoppingBasketDao.searchBooks(searchString);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean add(Book book, int quantity) {
        try {
            if (basketData.availableQuantity(book.getAuthor(), book.getTitle(), book.getPrice()) < quantity) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public int[] buy(Book... books) {
        int status[] = new int[books.length];
        int index = 0;
        for (Book book : books) {
            try {
                if (!basketData.isBookExistInStock(book.getAuthor(), book.getTitle(), book.getPrice())) {
                    status[index] = 2;
                } else {
                    status[index] = basketData.availableQuantity(book.getAuthor(), book.getTitle(), book.getPrice()) == 0 ? 1 : 0;
                }
                index++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return status;
    }

}
