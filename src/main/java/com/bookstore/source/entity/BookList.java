package com.bookstore.source.entity;

import java.util.List;

public interface BookList {

    public List<Book> list(String searchString);

    public boolean add(Book book, int quantity);

    public int[] buy(Book... books);

}
