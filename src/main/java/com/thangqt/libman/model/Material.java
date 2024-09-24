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
    private MaterialType type;
    private boolean isAvailable;
}
