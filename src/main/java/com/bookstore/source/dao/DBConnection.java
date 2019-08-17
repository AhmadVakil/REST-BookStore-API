package com.bookstore.source.dao;

public class DBConnection {

    public static BookShopDB DB = new BookShopDB();

    public static BookShopDB dbConnection() {
        return DB;
    }

}
