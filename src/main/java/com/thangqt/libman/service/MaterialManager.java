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

    public void addMaterial(Material material) throws SQLException {
        materialDAO.add(material);
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
}