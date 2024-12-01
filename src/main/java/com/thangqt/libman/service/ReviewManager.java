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

    public boolean addReview(Review review) {
        try {
            reviewDAO.add(review);
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding review: " + e.getMessage());
        }
        return false;
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

    public boolean updateReview(int id, int rating, String content) {
        try {
            reviewDAO.update(id, rating, content);
            return true;
        } catch (SQLException e) {
            System.out.println("Error updating review: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteReview(int id) {
        try {
            reviewDAO.delete(id);
            return true;
        } catch (SQLException e) {
            System.out.println("Error deleting review: " + e.getMessage());
        }
        return false;
    }

    public double getAverageRating(int materialId) {
        try {
            return reviewDAO.getAverageRating(materialId);
        } catch (SQLException e) {
            System.out.println("Error getting average rating: " + e.getMessage());
        }
        return 0;
    }
}
