package com.bookstore.source.controller;

import com.bookstore.source.entity.Book;
import com.bookstore.source.service.ShoppingBasketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shoppingBasket")
public class ShoppingBasketController {

    @Autowired
    private ShoppingBasketService shoppingBasketService;

    @RequestMapping(value = "/showBooksInUserBasket/userId={userId}", method = RequestMethod.GET)
    public List<Book> getAllBooksByUserId(@PathVariable("userId") String userId) throws Exception {
        return shoppingBasketService.getAllBooksByUserId(userId);
    }

    @RequestMapping(value = "/search={searchString}", method = RequestMethod.GET)
    public List<Book> list(@PathVariable("searchString") String searchString) throws Exception {
        return shoppingBasketService.list(searchString);
    }

    @RequestMapping(value = "/totalPriceOfTheBasket/userId={userId}", method = RequestMethod.GET)
    public Map<String, Object> getPriceOfTheBasket(@PathVariable("userId") String userId) throws Exception {
        return shoppingBasketService.getPriceOfTheBasket(userId);
    }

    @RequestMapping(value = "/deleteFromBasket/userId={userId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> deleteBookFromShoppingBasket(@RequestBody Book book, @PathVariable("userId") String userId) throws Exception {
        return shoppingBasketService.deleteBookFromShoppingBasket(book, userId);
    }

    @RequestMapping(value = "/deleteAllBooksFromBasket/userId={userId}", method = RequestMethod.POST)
    @ResponseBody
    public void deleteAllBooksFromBasket(@PathVariable("userId") String userId) throws Exception {
        shoppingBasketService.deleteAllBooksFromBasket(userId);
    }

    @RequestMapping(value = "/addBookToBasket/userId={userId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> addNewBookToBasket(@RequestBody Book book, @PathVariable("userId") String userId) throws Exception {
        return shoppingBasketService.addNewBookToUserBasket(book, userId);
    }

    @RequestMapping(value = "/getBookQuantity", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getAvailableQuantity(@RequestBody Book book) throws Exception {
        return shoppingBasketService.getAvailableQuantity(book);
    }

    @RequestMapping(value = "/buyBooks/userId={userId}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> buyBooks(@PathVariable("userId") String userId) throws Exception {
        return shoppingBasketService.buyBooks(userId);
    }

}
