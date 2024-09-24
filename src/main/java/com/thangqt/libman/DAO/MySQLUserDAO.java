package com.thangqt.libman.DAO;

import com.thangqt.libman.model.User;
import com.thangqt.libman.service.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MySQLUserDAO implements UserDAO {
    private Connection conn;

    public MySQLUserDAO() throws SQLException {
        this.conn = DatabaseConnection.getConnection();
    }

    @Override
    public void add(User user) throws SQLException {

    }

    @Override
    public void update(User user) throws SQLException {

    }

    @Override
    public void delete(int id) throws SQLException {

    }

    @Override
    public User getById(int id) throws SQLException {
        return null;
    }

    @Override
    public List<User> getAll() throws SQLException {
        return null;
    }
}
