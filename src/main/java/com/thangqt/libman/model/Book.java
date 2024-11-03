package com.thangqt.libman.model;

public class Book extends Material{
    private String isbn;
    private int pageCount;

    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.isAvailable = true;
        this.type = MaterialType.Book;
    }

    public Book(String title, String author, String description, String publisher, String isbn, int pageCount) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.publisher = publisher;
        this.isbn = isbn;
        this.pageCount = pageCount;
        this.isAvailable = true;
        this.type = MaterialType.Book;
    }

    public Book(String title, String author, String description, String isbn, String coverImageUrl) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.isbn = isbn;
        this.coverImageUrl = coverImageUrl;
        this.isAvailable = true;
        this.type = MaterialType.Book;
    }

    public Book(String title, String author, String description, String publisher, String isbn, String coverImageUrl, int quantity, int availableQuantity) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.publisher = publisher;
        this.isbn = isbn;
        this.coverImageUrl = coverImageUrl;
        this.quantity = quantity;
        this.availableQuantity = availableQuantity;
        this.isAvailable = true;
        this.type = MaterialType.Book;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
