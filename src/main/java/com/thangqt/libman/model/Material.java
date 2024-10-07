package com.thangqt.libman.model;

enum MaterialType {
    Book,
    Magazine
}

public abstract class Material {
    protected int id;
    protected String title;
    protected String author;
    protected String description;
    protected String publisher;
    protected MaterialType type;
    protected int quantity;
    protected int availableQuantity;
    protected boolean isAvailable;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisher() {
        return publisher;
    }

    public int getId() {
        return id;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
        this.isAvailable = availableQuantity > 0;
    }
}
