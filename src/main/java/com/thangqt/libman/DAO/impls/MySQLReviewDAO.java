package com.thangqt.libman.DAO.impls;

import com.thangqt.libman.DAO.ReviewDAO;
import com.thangqt.libman.model.Review;
import com.thangqt.libman.service.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MySQLReviewDAO implements ReviewDAO {
    private Connection conn;

    public MySQLReviewDAO() throws SQLException {
        this.conn = DatabaseConnection.getConnection();
    }

    @Override
    public void add(Review review) throws SQLException {
        String query = "INSERT INTO reviews (user_id, material_id, content, rating, date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, review.getUserId());
            stm.setInt(2, review.getMaterialId());
            stm.setString(3, review.getContent());
            stm.setInt(4, review.getRating());
            stm.setDate(5, java.sql.Date.valueOf(review.getDate()));
            stm.executeUpdate();
        } catch (Exception e) {
            throw new SQLException("Error adding review: " + e.getMessage());
        }
    }

    public Review makeReview(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int userId = rs.getInt("user_id");
        int materialId = rs.getInt("material_id");
        String content = rs.getString("content");
        int rating = rs.getInt("rating");
        LocalDate date = rs.getDate("date").toLocalDate();
        return new Review(id, userId, materialId, content, rating, date);
    }

    @Override
    public Review get(int id) throws SQLException {
        String query = "SELECT * FROM reviews WHERE id = ?";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return makeReview(rs);
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error getting review: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Review get(int userId, int materialId) throws SQLException {
        String query = "SELECT * FROM reviews WHERE user_id = ? AND material_id = ?";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, userId);
            stm.setInt(2, materialId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return makeReview(rs);
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error getting review: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Review> getReviewsByMaterialId(int materialId) throws SQLException {
        List<Review> reviews = new ArrayList<>();
        String query = "SELECT * FROM reviews WHERE material_id = ?";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, materialId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    reviews.add(makeReview(rs));
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error getting reviews: " + e.getMessage());
        }
        return reviews;
    }
}
