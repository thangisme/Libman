package com.thangqt.libman.DAO;

import com.thangqt.libman.model.Review;

import java.sql.SQLException;
import java.util.List;

public interface ReviewDAO {
    void add(Review review) throws SQLException;

    Review get(int id) throws SQLException;

    Review get(int userId, int materialId) throws SQLException;

    void update(int id, int rating, String content) throws SQLException;

    void delete(int id) throws SQLException;

    List<Review> getReviewsByMaterialId(int materialId) throws SQLException;

    double getAverageRating(int materialId) throws SQLException;

}
