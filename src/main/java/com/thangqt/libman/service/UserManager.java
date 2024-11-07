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

  public User authenticate(String email, String password) throws SQLException {
    return userDAO.authenticate(email, password);
  }

  public User addUser(User user) throws SQLException {
    return userDAO.add(user);
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

  public User getUserByEmail(String email) throws SQLException {
    return userDAO.getByEmail(email);
  }

  public List<User> getAllUsers() throws SQLException {
    return userDAO.getAll();
  }

  public boolean isUserExist(int userId) throws SQLException {
    return userDAO.isExist(userId);
  }

  public boolean isUserExist(String email) throws SQLException {
    return userDAO.isExist(email);
  }

  public int getTotalUsersNumber() throws SQLException {
    return userDAO.getTotalUsersNumber();
  }

  public List<User>  searchUserByName(String name) throws SQLException {
    return userDAO.searchByName(name);
  }
}
