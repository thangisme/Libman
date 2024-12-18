package com.thangqt.libman.DAO.impls;

import com.thangqt.libman.DAO.MaterialDAO;
import com.thangqt.libman.model.Book;
import com.thangqt.libman.model.Magazine;
import com.thangqt.libman.model.Material;
import com.thangqt.libman.service.DatabaseConnection;
import com.thangqt.libman.service.MaterialManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MySQLMaterialDAO implements MaterialDAO {
  private Connection conn;

  public MySQLMaterialDAO() throws SQLException {
    this.conn = DatabaseConnection.getConnection();
  }

  @Override
  public Material add(Material material) throws SQLException {
    String query =
        "INSERT INTO materials (title, author, publisher, description, cover_image_url, type, quantity, available_quantity, is_available, added_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    try (PreparedStatement stm = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      stm.setString(1, material.getTitle());
      stm.setString(2, material.getAuthor());
      stm.setString(3, material.getPublisher());
      stm.setString(4, material.getDescription());
      stm.setString(5, material.getCoverImageUrl());
      stm.setString(6, material instanceof Book ? "Book" : "Magazine");
      stm.setInt(7, material.getQuantity());
      stm.setInt(8, material.getAvailableQuantity());
      stm.setBoolean(9, material.isAvailable());
      stm.setDate(10, Date.valueOf(LocalDate.now()));
      stm.executeUpdate();

      try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          material.setId(generatedKeys.getInt(1));
        }
      }

      if (material instanceof Book) {
        addBook((Book) material);
      } else if (material instanceof Magazine) {
        addMagazine((Magazine) material);
      }
    } catch (SQLException e) {
      throw new SQLException("Error adding material: " + e.getMessage());
    }
    return material;
  }

  private void addBook(Book book) throws SQLException {
    String query = "INSERT INTO books (id, page_count, isbn) VALUES (?, ?, ?)";
    try (PreparedStatement stm = conn.prepareStatement(query)) {
      stm.setInt(1, book.getId());
      stm.setInt(2, book.getPageCount());
      stm.setString(3, book.getIsbn());
      stm.executeUpdate();
    } catch (SQLException e) {
      throw new SQLException("Error adding book: " + e.getMessage());
    }
  }

  private void addMagazine(Magazine magazine) throws SQLException {
    String query =
        "INSERT INTO magazines (id, issueNumber, currentIssue, issn) VALUES (?, ?, ?, ?)";
    try (PreparedStatement stm = conn.prepareStatement(query)) {
      stm.setInt(1, magazine.getId());
      stm.setInt(2, magazine.getIssueNumber());
      stm.setInt(3, magazine.getCurrentIssue());
      stm.setString(4, magazine.getIssn());
      stm.executeUpdate();
    } catch (SQLException e) {
      throw new SQLException("Error adding magazine: " + e.getMessage());
    }
  }

  @Override
  public void update(Material material) throws SQLException {
    String query =
        "UPDATE materials SET title = ?, author = ?, type = ?, is_available = ?, cover_image_url = ?, publisher = ?, description = ?, quantity = ?, available_quantity = ? WHERE id = ?";
    try (PreparedStatement stm = conn.prepareStatement(query)) {
      stm.setString(1, material.getTitle());
      stm.setString(2, material.getAuthor());
      stm.setString(3, material instanceof Book ? "Book" : "Magazine");
      stm.setBoolean(4, material.isAvailable());
      stm.setString(5, material.getCoverImageUrl());
      stm.setString(6, material.getPublisher());
      stm.setString(7, material.getDescription());
      stm.setInt(8, material.getQuantity());
      stm.setInt(9, material.getAvailableQuantity());
      stm.setInt(10, material.getId());
      stm.executeUpdate();

      if (material instanceof Book) {
        updateBook((Book) material);
      } else if (material instanceof Magazine) {
        updateMagazine((Magazine) material);
      }
    } catch (SQLException e) {
      throw new SQLException("Error updating material: " + e.getMessage());
    }
  }

  private void updateBook(Book book) throws SQLException {
    String query = "UPDATE books SET page_count = ?, isbn = ? WHERE id = ?";
    try (PreparedStatement stm = conn.prepareStatement(query)) {
      stm.setInt(1, book.getPageCount());
      stm.setString(2, book.getIsbn());
      stm.setInt(3, book.getId());
      stm.executeUpdate();
    } catch (SQLException e) {
      throw new SQLException("Error updating book: " + e.getMessage());
    }
  }

  private void updateMagazine(Magazine magazine) throws SQLException {
    String query = "UPDATE magazines SET issueNumber = ?, currentIssue = ?, issn = ? WHERE id = ?";
    try (PreparedStatement stm = conn.prepareStatement(query)) {
      stm.setInt(1, magazine.getIssueNumber());
      stm.setInt(2, magazine.getCurrentIssue());
      stm.setString(3, magazine.getIssn());
      stm.setInt(4, magazine.getId());
      stm.executeUpdate();
    } catch (SQLException e) {
      throw new SQLException("Error updating magazine: " + e.getMessage());
    }
  }

  @Override
  public void delete(int id) throws SQLException {
    String query = "DELETE FROM materials WHERE id = ?";
    try (PreparedStatement stm = conn.prepareStatement(query)) {
      stm.setInt(1, id);
      stm.executeUpdate();
    } catch (SQLException e) {
      throw new SQLException("Error deleting material: " + e.getMessage());
    }
  }

  private Material createMaterialFromResult(ResultSet rs) throws SQLException {
    int id = rs.getInt("id");
    String title = rs.getString("title");
    String author = rs.getString("author");
    String description = rs.getString("description");
    String publisher = rs.getString("publisher");
    int quantity = rs.getInt("quantity");
    int availableQuantity = rs.getInt("available_quantity");
    String coverImageUrl = rs.getString("cover_image_url");
    LocalDate addedDate = rs.getDate("added_date").toLocalDate();
    if (rs.getString("type").equals("Book")) {
      String isbn = rs.getString("books.isbn");
      int pageCount = rs.getInt("page_count");
      Book book = new Book(title, author, description, publisher, isbn, pageCount);
      book.setId(id);
      book.setAddedDate(addedDate);
      if (coverImageUrl != null) {
        book.setCoverImageUrl(coverImageUrl);
      }
      book.setQuantity(quantity);
      book.setAvailableQuantity(availableQuantity);
      return book;
    } else if (rs.getString("type").equals("Magazine")) {
      String issn = rs.getString("issn");
      int issueNumber = rs.getInt("issueNumber");
      int currentIssue = rs.getInt("currentIssue");
      Magazine magazine =
          new Magazine(title, author, description, publisher, issn, issueNumber, currentIssue);
      magazine.setId(id);
      magazine.setAddedDate(addedDate);
      if (coverImageUrl != null) {
        magazine.setCoverImageUrl(coverImageUrl);
      }
      magazine.setQuantity(quantity);
      magazine.setAvailableQuantity(availableQuantity);
      return magazine;
    }
    return null;
  }

  @Override
  public Material getById(int id) throws SQLException {
    String query =
        "SELECT materials.*, books.page_count, books.isbn, magazines.issn, magazines.issueNumber, magazines.currentIssue "
            + "FROM materials "
            + "LEFT JOIN books ON materials.id = books.id "
            + "LEFT JOIN magazines ON materials.id = magazines.id "
            + "WHERE materials.id = ?";
    try (PreparedStatement stm = conn.prepareStatement(query)) {
      stm.setInt(1, id);
      ResultSet rs = stm.executeQuery();
      if (rs.next()) {
        return createMaterialFromResult(rs);
      } else {
        return null;
      }
    } catch (SQLException e) {
      throw new SQLException("Error getting material by id: " + e.getMessage());
    }
  }

  @Override
  public List<Material> getAll() throws SQLException {
    String query =
        "SELECT materials.*, books.page_count, books.isbn, magazines.issn, magazines.issueNumber, magazines.currentIssue "
            + "FROM materials "
            + "LEFT JOIN books ON materials.id = books.id "
            + "LEFT JOIN magazines ON materials.id = magazines.id ";
    try (PreparedStatement stm = conn.prepareStatement(query)) {
      ResultSet rs = stm.executeQuery();
      List<Material> allMaterials = new ArrayList<>();
      while (rs.next()) {
        Material material = createMaterialFromResult(rs);
        allMaterials.add(material);
      }
      return allMaterials;
    } catch (SQLException e) {
      throw new SQLException("Error getting all materials: " + e.getMessage());
    }
  }

  @Override
  public List<Material> search(String query) throws SQLException {
    String searchQuery =
        "SELECT materials.*, books.page_count, books.isbn, magazines.issn, magazines.issueNumber, magazines.currentIssue "
            + "FROM materials "
            + "LEFT JOIN books ON materials.id = books.id "
            + "LEFT JOIN magazines ON materials.id = magazines.id "
            + "WHERE title LIKE ? OR author LIKE ?";

    try (PreparedStatement stm = conn.prepareStatement(searchQuery)) {
      String likeQuery = "%" + query + "%";
      stm.setString(1, likeQuery);
      stm.setString(2, likeQuery);

      ResultSet rs = stm.executeQuery();
      List<Material> allMaterials = new ArrayList<>();
      while (rs.next()) {
        Material material = createMaterialFromResult(rs);
        allMaterials.add(material);
      }
      return allMaterials;
    } catch (SQLException e) {
      throw new SQLException("Error searching materials: " + e.getMessage());
    }
  }

  @Override
  public List<Material> searchByTitle(String title) throws SQLException {
    String query =
        "SELECT materials.*, books.page_count, books.isbn, magazines.issn, magazines.issueNumber, magazines.currentIssue "
            + "FROM materials "
            + "LEFT JOIN books ON materials.id = books.id "
            + "LEFT JOIN magazines ON materials.id = magazines.id "
            + "WHERE title LIKE ?";
    try (PreparedStatement stm = conn.prepareStatement(query)) {
      stm.setString(1, "%" + title + "%");
      ResultSet rs = stm.executeQuery();
      List<Material> allMaterials = new ArrayList<>();
      while (rs.next()) {
        Material material = createMaterialFromResult(rs);
        allMaterials.add(material);
      }
      return allMaterials;
    } catch (SQLException e) {
      throw new SQLException("Error searching materials by title: " + e.getMessage());
    }
  }

  @Override
  public List<Material> searchByAuthor(String author) throws SQLException {
    String query =
        "SELECT materials.*, books.page_count, books.isbn, magazines.issn, magazines.issueNumber, magazines.currentIssue "
            + "FROM materials "
            + "LEFT JOIN books ON materials.id = books.id "
            + "LEFT JOIN magazines ON materials.id = magazines.id "
            + "WHERE author LIKE ?";
    try (PreparedStatement stm = conn.prepareStatement(query)) {
      stm.setString(1, "%" + author + "%");
      ResultSet rs = stm.executeQuery();
      List<Material> allMaterials = new ArrayList<>();
      while (rs.next()) {
        Material material = createMaterialFromResult(rs);
        allMaterials.add(material);
      }
      return allMaterials;
    } catch (SQLException e) {
      throw new SQLException("Error searching materials by author: " + e.getMessage());
    }
  }

  @Override
  public int getAvailableQuantity(int id) throws SQLException {
    String query = "SELECT available_quantity FROM materials WHERE id = ?";
    try (PreparedStatement stm = conn.prepareStatement(query)) {
      stm.setInt(1, id);
      ResultSet rs = stm.executeQuery();
      if (rs.next()) {
        return rs.getInt("available_quantity");
      } else {
        return 0;
      }
    } catch (SQLException e) {
      throw new SQLException("Error getting available quantity: " + e.getMessage());
    }
  }

  @Override
  public int getQuantity(int id) throws SQLException {
    String query = "SELECT quantity FROM materials WHERE id = ?";
    try (PreparedStatement stm = conn.prepareStatement(query)) {
      stm.setInt(1, id);
      ResultSet rs = stm.executeQuery();
      if (rs.next()) {
        return rs.getInt("quantity");
      } else {
        return 0;
      }
    } catch (SQLException e) {
      throw new SQLException("Error getting quantity: " + e.getMessage());
    }
  }

  @Override
  public void setAvailableQuantity(int id, int quantity) throws SQLException {
    String query = "UPDATE materials SET available_quantity = ? WHERE id = ?";
    try (PreparedStatement stm = conn.prepareStatement(query)) {
      stm.setInt(1, quantity);
      stm.setInt(2, id);
      stm.executeUpdate();
    } catch (SQLException e) {
      throw new SQLException("Error setting available quantity: " + e.getMessage());
    }
  }

  @Override
  public void setQuantity(int id, int quantity) throws SQLException {
    String query = "UPDATE materials SET quantity = ? WHERE id = ?";
    try (PreparedStatement stm = conn.prepareStatement(query)) {
      stm.setInt(1, quantity);
      stm.setInt(2, id);
      stm.executeUpdate();
    } catch (SQLException e) {
      throw new SQLException("Error setting quantity: " + e.getMessage());
    }
  }

  @Override
  public boolean isExist(int id) throws SQLException {
    String query = "SELECT * FROM materials WHERE id = ?";
    try (PreparedStatement stm = conn.prepareStatement(query)) {
      stm.setInt(1, id);
      ResultSet rs = stm.executeQuery();
      return rs.next();
    } catch (SQLException e) {
      throw new SQLException("Error checking if material exists: " + e.getMessage());
    }
  }

  @Override
  public int getTotalMaterialsNumber() throws SQLException {
    String query = "SELECT COUNT(*) FROM materials";
    try (PreparedStatement stm = conn.prepareStatement(query)) {
      ResultSet rs = stm.executeQuery();
      if (rs.next()) {
        return rs.getInt(1);
      } else {
        return 0;
      }
    } catch (SQLException e) {
      return 0;
    }
  }

  public List<Material> getRecentlyAddedMaterials(int i) throws SQLException {
    String query =
        "SELECT materials.*, books.page_count, books.isbn, magazines.issn, magazines.issueNumber, magazines.currentIssue "
            + "FROM materials "
            + "LEFT JOIN books ON materials.id = books.id "
            + "LEFT JOIN magazines ON materials.id = magazines.id "
            + "ORDER BY added_date DESC LIMIT ?";
    try (PreparedStatement stm = conn.prepareStatement(query)) {
      stm.setInt(1, i);
      ResultSet rs = stm.executeQuery();
      List<Material> allMaterials = new ArrayList<>();
      while (rs.next()) {
        Material material = createMaterialFromResult(rs);
        allMaterials.add(material);
      }
      return allMaterials;
    } catch (SQLException e) {
      throw new SQLException("Error getting recently added materials: " + e.getMessage());
    }
  }

  @Override
  public List<Material> getPopularMaterials(int numberOfMaterials, int period) throws SQLException {
    String query =
        "SELECT materials.*, books.page_count, books.isbn, magazines.issn, magazines.issueNumber, magazines.currentIssue, COUNT(loans.material_id) AS borrow_count "
            + "FROM materials "
            + "LEFT JOIN books ON materials.id = books.id "
            + "LEFT JOIN magazines ON materials.id = magazines.id "
            + "LEFT JOIN loans ON materials.id = loans.material_id "
            + "WHERE loans.borrow_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY) "
            + "GROUP BY materials.id "
            + "ORDER BY borrow_count DESC "
            + "LIMIT ?";
    try (PreparedStatement stm = conn.prepareStatement(query)) {
      stm.setInt(1, period);
      stm.setInt(2, numberOfMaterials);
      ResultSet rs = stm.executeQuery();
      List<Material> popularMaterials = new ArrayList<>();
      while (rs.next()) {
        Material material = createMaterialFromResult(rs);
        popularMaterials.add(material);
      }
      return popularMaterials;
    } catch (SQLException e) {
      throw new SQLException("Error getting popular materials: " + e.getMessage());
    }
  }
}
