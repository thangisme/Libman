package com.thangqt.libman.DAO;

import com.thangqt.libman.model.Book;
import com.thangqt.libman.model.Magazine;
import com.thangqt.libman.model.Material;
import com.thangqt.libman.service.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.List;

public class MySQLMaterialDAO implements MaterialDAO {
    private Connection conn;

    public MySQLMaterialDAO() throws SQLException {
        this.conn = DatabaseConnection.getConnection();
    }

    @Override
    public void add(Material material) throws SQLException {

    }

    private void addBook(Book book) throws SQLException {

    }

    private void addMagazine(Magazine magazine) throws SQLException {

    }

    @Override
    public void update(Material material) throws SQLException {

    }

    private void updateBook(Book book) throws SQLException {

    }

    private void updateMagazine(Magazine magazine) throws SQLException {

    }

    @Override
    public void delete(int id) throws SQLException {

    }

    @Override
    public Material getById(int id) throws SQLException {
        return null;
    }

    @Override
    public List<Material> getAll() throws SQLException {
        return null;
    }

    @Override
    public List<Material> searchByTitle(String title) throws SQLException {
        return null;
    }
}
