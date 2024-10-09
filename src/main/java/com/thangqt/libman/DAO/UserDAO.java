package com.thangqt.libman.DAO;

import com.thangqt.libman.model.User;

import java.sql.SQLException;
import java.util.List;

public interface UserDAO {
    void add(User user) throws SQLException;
    void update(User user) throws SQLException;
    void delete(int id) throws SQLException;
    User getById(int id) throws SQLException;
    User getByEmail(String email) throws SQLException;
    List<User> getAll() throws SQLException;
    boolean isExist(int userId);
    boolean isExist(String email);
}
