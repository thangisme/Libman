package com.thangqt.libman.service;

import com.thangqt.libman.DAO.*;
import com.thangqt.libman.model.*;
import java.sql.SQLException;
import java.util.List;

public class MaterialManager {
  private MaterialDAO materialDAO;

  public MaterialManager(MaterialDAO materialDAO) {
    this.materialDAO = materialDAO;
  }

  public Material addMaterial(Material material) throws SQLException {
    return materialDAO.add(material);
  }

  public void updateMaterial(Material material) throws SQLException {
    materialDAO.update(material);
  }

  public void deleteMaterial(int id) throws SQLException {
    materialDAO.delete(id);
  }

  public Material getMaterialById(int id) throws SQLException {
    return materialDAO.getById(id);
  }

  public List<Material> getAllMaterials() throws SQLException {
    return materialDAO.getAll();
  }

  public List<Material> getAllMaterialsByType(String type) throws SQLException {
    return materialDAO.getAll();
  }

  public boolean isMaterialAvailable(int id) throws SQLException {
    return materialDAO.getAvailableQuantity(id) > 0;
  }

  public void decreaseAvailableQuantity(int id) throws SQLException {
    int availableQuantity = materialDAO.getAvailableQuantity(id);
    materialDAO.setAvailableQuantity(id, availableQuantity - 1);
  }

  public void increaseAvailableQuantity(int id) throws SQLException {
    int availableQuantity = materialDAO.getAvailableQuantity(id);
    materialDAO.setAvailableQuantity(id, availableQuantity + 1);
  }

  public boolean isMaterialExist(int id) throws SQLException {
    return materialDAO.isExist(id);
  }

  public List<Material> searchMaterials(String query) throws SQLException {
    return materialDAO.search(query);
  }

  public List<Material> getMaterialsByTitle(String title) throws SQLException {
    return materialDAO.searchByTitle(title);
  }

  public List<Material> getMaterialsByAuthor(String author) throws SQLException {
    return materialDAO.searchByAuthor(author);
  }

  public int getTotalMaterialsNumber() throws SQLException {
    return materialDAO.getTotalMaterialsNumber();
  }

  public List<Material> getRecentlyAddedMaterials(int i) throws SQLException {
    return materialDAO.getRecentlyAddedMaterials(i);
  }

    public List<Material> getPopularMaterials(int numberOfMaterials, int period) throws SQLException {
        return materialDAO.getPopularMaterials(numberOfMaterials, period);
    }
}
