package com.thangqt.libman.service;

import com.thangqt.libman.DAO.ReviewDAO;
import com.thangqt.libman.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ReviewManager class.
 */
@ExtendWith(MockitoExtension.class)
public class ReviewManagerTest {

    @Mock
    private ReviewDAO reviewDAO;

    private ReviewManager reviewManager;

    /**
     * Sets up the test environment before each test.
     * Initializes the ReviewManager with mocked dependencies.
     */
    @BeforeEach
    void setUp() {
        reviewManager = new ReviewManager(reviewDAO);
    }

    /**
     * Tests the addReview method.
     * Verifies that the review is added.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void addReview_ShouldAddReview() throws SQLException {
        Review review = new Review(1, 1, 1, "Great material!", 5, LocalDate.now());

        reviewManager.addReview(review);

        verify(reviewDAO).add(review);
    }

    /**
     * Tests the getReview method.
     * Verifies that the method returns the expected Review object.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void getReview_ShouldReturnReview() throws SQLException {
        int reviewId = 1;
        Review expectedReview = new Review(reviewId, 1, 1, "Great material!", 5, LocalDate.now());
        when(reviewDAO.get(reviewId)).thenReturn(expectedReview);

        Review result = reviewManager.getReview(reviewId);

        assertEquals(expectedReview, result);
        verify(reviewDAO).get(reviewId);
    }

    /**
     * Tests the getReviewByUserIdAndMaterialId method.
     * Verifies that the method returns the expected Review object.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void getReviewByUserIdAndMaterialId_ShouldReturnReview() throws SQLException {
        int userId = 1;
        int materialId = 1;
        Review expectedReview = new Review(1, userId, materialId, "Great material!", 5, LocalDate.now());
        when(reviewDAO.get(userId, materialId)).thenReturn(expectedReview);

        Review result = reviewManager.getReview(userId, materialId);

        assertEquals(expectedReview, result);
        verify(reviewDAO).get(userId, materialId);
    }

    /**
     * Tests the getReviewsByMaterialId method.
     * Verifies that the method returns a list of reviews for the specified material.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void getReviewsByMaterialId_ShouldReturnListOfReviews() throws SQLException {
        int materialId = 1;
        List<Review> expectedReviews = Arrays.asList(
                new Review(1, 1, materialId, "Great material!", 5, LocalDate.now()),
                new Review(2, 2, materialId, "Not bad", 3, LocalDate.now())
        );
        when(reviewDAO.getReviewsByMaterialId(materialId)).thenReturn(expectedReviews);

        List<Review> result = reviewManager.getReviewsByMaterialId(materialId);

        assertEquals(expectedReviews, result);
        verify(reviewDAO).getReviewsByMaterialId(materialId);
    }
}