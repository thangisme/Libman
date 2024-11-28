package com.thangqt.libman.service;

import com.thangqt.libman.DAO.MaterialDAO;
import com.thangqt.libman.model.Book;
import com.thangqt.libman.model.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MaterialManager class.
 */
@ExtendWith(MockitoExtension.class)
public class MaterialManagerTest {

    @Mock
    private MaterialDAO materialDAO;

    private MaterialManager materialManager;

    /**
     * Sets up the test environment before each test.
     * Initializes the MaterialManager with a mocked MaterialDAO.
     */
    @BeforeEach
    void setUp() {
        materialManager = new MaterialManager(materialDAO);
    }

    /**
     * Tests the addMaterial method.
     * Verifies that the method returns the added Material object.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void addMaterial_ShouldReturnAddedMaterial() throws SQLException {
        Material material = new Book("Test Book", "Author", "342");
        when(materialDAO.add(material)).thenReturn(material);

        Material result = materialManager.addMaterial(material);

        assertEquals(material, result);
        verify(materialDAO).add(material);
    }

    /**
     * Tests the isMaterialAvailable method when the quantity is greater than zero.
     * Verifies that the method returns true.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void isMaterialAvailable_WhenQuantityGreaterThanZero_ShouldReturnTrue() throws SQLException {
        int materialId = 1;
        when(materialDAO.getAvailableQuantity(materialId)).thenReturn(5);

        boolean result = materialManager.isMaterialAvailable(materialId);

        assertTrue(result);
        verify(materialDAO).getAvailableQuantity(materialId);
    }

    /**
     * Tests the decreaseAvailableQuantity method.
     * Verifies that the available quantity is decreased by one.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void decreaseAvailableQuantity_ShouldUpdateQuantityCorrectly() throws SQLException {
        int materialId = 1;
        when(materialDAO.getAvailableQuantity(materialId)).thenReturn(5);

        materialManager.decreaseAvailableQuantity(materialId);

        verify(materialDAO).setAvailableQuantity(materialId, 4);
    }

    /**
     * Tests the searchMaterials method.
     * Verifies that the method returns a list of materials matching the search query.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void searchMaterials_ShouldReturnMatchingMaterials() throws SQLException {
        String query = "Java";
        List<Material> expectedMaterials = Arrays.asList(
            new Book("Java Programming", "Author 1", "432"),
            new Book("Advanced Java", "Author 2", "4234")
        );
        when(materialDAO.search(query)).thenReturn(expectedMaterials);

        List<Material> result = materialManager.searchMaterials(query);

        assertEquals(expectedMaterials, result);
        verify(materialDAO).search(query);
    }

    /**
     * Tests the getPopularMaterials method.
     * Verifies that the method returns the most borrowed materials.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void getPopularMaterials_ShouldReturnMostBorrowedMaterials() throws SQLException {
        int numberOfMaterials = 5;
        int period = 30;
        List<Material> expectedMaterials = Arrays.asList(
            new Book("Popular Book 1", "Author 1", "534"),
            new Book("Popular Book 2", "Author 2", "45")
        );
        when(materialDAO.getPopularMaterials(numberOfMaterials, period)).thenReturn(expectedMaterials);

        List<Material> result = materialManager.getPopularMaterials(numberOfMaterials, period);

        assertEquals(expectedMaterials, result);
        verify(materialDAO).getPopularMaterials(numberOfMaterials, period);
    }

    /**
     * Tests the increaseAvailableQuantity method.
     * Verifies that the available quantity is increased by one.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void increaseAvailableQuantity_ShouldUpdateQuantityCorrectly() throws SQLException {
        int materialId = 1;
        when(materialDAO.getAvailableQuantity(materialId)).thenReturn(5);

        materialManager.increaseAvailableQuantity(materialId);

        verify(materialDAO).setAvailableQuantity(materialId, 6);
    }

    /**
     * Tests the getMaterialById method.
     * Verifies that the method returns the expected Material object.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void getMaterialById_ShouldReturnMaterial() throws SQLException {
        int materialId = 1;
        Material expectedMaterial = new Book("Test Book", "Author", "342");
        when(materialDAO.getById(materialId)).thenReturn(expectedMaterial);

        Material result = materialManager.getMaterialById(materialId);

        assertEquals(expectedMaterial, result);
        verify(materialDAO).getById(materialId);
    }

    /**
     * Tests the updateMaterial method.
     * Verifies that the method calls the update method on the MaterialDAO.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void updateMaterial_ShouldCallUpdateOnMaterialDAO() throws SQLException {
        Material material = new Book("Updated Book", "Updated Author", "123");

        materialManager.updateMaterial(material);

        verify(materialDAO).update(material);
    }

    /**
     * Tests the deleteMaterial method.
     * Verifies that the method calls the delete method on the MaterialDAO.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void deleteMaterial_ShouldCallDeleteOnMaterialDAO() throws SQLException {
        int materialId = 1;

        materialManager.deleteMaterial(materialId);

        verify(materialDAO).delete(materialId);
    }
}