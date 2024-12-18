package com.thangqt.libman.DAO;

import com.thangqt.libman.model.User;
import java.sql.SQLException;
import java.util.List;

public interface UserDAO {
  User authenticate(String email, String password) throws SQLException;

  User add(User user) throws SQLException;

  void update(User user) throws SQLException;

  void delete(int id) throws SQLException;

  User getById(int id) throws SQLException;

  User getByEmail(String email) throws SQLException;

  List<User> getAll() throws SQLException;

  List<User> searchByName(String name) throws SQLException;

  boolean isExist(int userId) throws SQLException;

  boolean isExist(String email) throws SQLException;

  int getTotalUsersNumber() throws SQLException;
}
