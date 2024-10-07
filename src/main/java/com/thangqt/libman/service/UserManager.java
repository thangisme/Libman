package com.thangqt.libman.service;

import com.thangqt.libman.DAO.UserDAO;
import com.thangqt.libman.model.User;

import java.sql.SQLException;
import java.util.List;

public class UserManager {
    private UserDAO userDAO;

    public UserManager(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void addUser(User user) throws SQLException {
        userDAO.add(user);
    }

    public void updateUser(User user) throws SQLException {
        userDAO.update(user);
    }

    public void deleteUser(int id) throws SQLException {
        userDAO.delete(id);
    }

    public User getUserById(int id) throws SQLException {
        return userDAO.getById(id);
    }

    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAll();
    }

    public boolean isUserExist(int userId) {
        return userDAO.isExist(userId);
    }

    public boolean isUserExist(String email) {
        return userDAO.isExist(email);
    }
}
