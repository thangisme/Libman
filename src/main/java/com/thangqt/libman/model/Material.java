package com.thangqt.libman.model;

enum MaterialType {
    Book,
    Magazine
}

public abstract class Material {
    private int id;
    private String title;
    private String author;
    private String description;
    private String publisher;
    private MaterialType type;
    private boolean isAvailable;

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
}
