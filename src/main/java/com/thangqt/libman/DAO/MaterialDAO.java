package com.thangqt.libman.DAO;

import com.thangqt.libman.model.Material;
import java.sql.SQLException;
import java.util.List;

public interface MaterialDAO {
  void add(Material material) throws SQLException;

  void update(Material material) throws SQLException;

  void delete(int id) throws SQLException;

  boolean isExist(int id) throws SQLException;

  int getAvailableQuantity(int id) throws SQLException;

  int getQuantity(int id) throws SQLException;

  void setAvailableQuantity(int id, int quantity) throws SQLException;

  void setQuantity(int id, int quantity) throws SQLException;

  Material getById(int id) throws SQLException;

  List<Material> getAll() throws SQLException;

  List<Material> search(String query) throws SQLException;

  List<Material> searchByTitle(String title) throws SQLException;

  List<Material> searchByAuthor(String author) throws SQLException;

  int getTotalMaterialsNumber() throws SQLException;

  List<Material> getRecentlyAddedMaterials(int i) throws SQLException;

  List<Material> getPopularMaterials(int numberOfMaterials, int period) throws SQLException;
}
