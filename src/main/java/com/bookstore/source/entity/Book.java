package com.bookstore.source.entity;

import java.math.BigDecimal;

public class Book {

    private String title;
    private String author;
    private BigDecimal price;
    private int quantity;
    private String description;

    public Book(String title, String author, BigDecimal price, int quantity, String description) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
    }

    public Book() {
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

    public String getDescription() {
        return description;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return "\tTitle: " + this.title + " Author: " + this.author + " Price: " + this.price + " Quantity: " + this.quantity + " Description: " + this.description;
    }
}
