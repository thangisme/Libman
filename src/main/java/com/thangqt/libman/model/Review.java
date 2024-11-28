package com.thangqt.libman.model;

import java.time.LocalDate;

public class Review {
    private int id;
    private int userId;
    private int materialId;
    private String content;
    private int rating;
    private LocalDate date;

    public Review(int id, int userId, int materialId, String content, int rating, LocalDate date) {
        this.id = id;
        this.userId = userId;
        this.materialId = materialId;
        this.content = content;
        this.rating = rating;
        this.date = date;
    }

    public Review(int userId, int materialId, String content, int rating, LocalDate date) {
        this.userId = userId;
        this.materialId = materialId;
        this.content = content;
        this.rating = rating;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
