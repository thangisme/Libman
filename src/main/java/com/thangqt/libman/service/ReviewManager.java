package com.thangqt.libman.service;

import com.thangqt.libman.DAO.ReviewDAO;
import com.thangqt.libman.model.Review;

import java.sql.SQLException;
import java.util.List;

public class ReviewManager {
    private ReviewDAO reviewDAO;

    public ReviewManager(ReviewDAO reviewDAO) {
        this.reviewDAO = reviewDAO;
    }

    public void addReview(Review review) {
        try {
            reviewDAO.add(review);
        } catch (SQLException e) {
            System.out.println("Error adding review: " + e.getMessage());
        }
    }

    public Review getReview(int id) {
        try {
            return reviewDAO.get(id);
        } catch (SQLException e) {
            System.out.println("Error getting review: " + e.getMessage());
        }
        return null;
    }

    public Review getReview(int userId, int materialId) {
        try {
            return reviewDAO.get(userId, materialId);
        } catch (SQLException e) {
            System.out.println("Error getting review: " + e.getMessage());
        }
        return null;
    }

    public List<Review> getReviewsByMaterialId(int materialId) {
        try {
            return reviewDAO.getReviewsByMaterialId(materialId);
        } catch (SQLException e) {
            System.out.println("Error getting reviews: " + e.getMessage());
        }
        return null;
    }
}
