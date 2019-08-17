package com.bookstore.source.entity;

import java.math.BigDecimal;

public class Order {

    private String userid;
    private String title;
    private String author;
    private BigDecimal price;
    private int quantity;

    public Order(String userid, String title, String author, BigDecimal price, int quantity) {
        this.userid = userid;
        this.title = title;
        this.author = author;
        this.price = price;
        this.quantity = quantity;
    }

    public Order() {
    }

    public String getUserId() {
        return userid;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setUserId(String userId) {
        this.userid = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPrice(BigDecimal price) {
        this.price = new BigDecimal(String.valueOf(price));
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String toString() {
        return "\tUser: " + userid + " \tTitle: " + this.title + " Author: " + this.author + " Price: " + this.price + " Quantity: " + this.quantity;
    }
}
