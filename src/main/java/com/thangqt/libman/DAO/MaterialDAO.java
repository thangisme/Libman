package com.thangqt.libman.DAO;

import com.thangqt.libman.model.Material;
import java.sql.SQLException;
import java.util.List;

public interface MaterialDAO {
    void add(Material material) throws SQLException;
    void update(Material material) throws SQLException;
    void delete(int id) throws SQLException;
    Material getById(int id) throws SQLException;
    List<Material> getAll() throws SQLException;
    List<Material> searchByTitle(String title) throws SQLException;
}
