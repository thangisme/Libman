package com.thangqt.libman.DAO;

import com.thangqt.libman.model.User;
import com.thangqt.libman.service.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQLUserDAO implements UserDAO {
    private Connection conn;

    public MySQLUserDAO() throws SQLException {
        this.conn = DatabaseConnection.getConnection();
    }

    @Override
    public void add(User user) throws SQLException {
        String query = "INSERT INTO users (name, email) VALUES (?, ?)";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setString(1, user.getName());
            stm.setString(2, user.getEmail());
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error adding user: " + e.getMessage());
        }

    }

    @Override
    public void update(User user) throws SQLException {
        String query = "UPDATE users SET name = ?, email = ? WHERE id = ?";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setString(1, user.getName());
            stm.setString(2, user.getEmail());
            stm.setInt(3, user.getId());
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error updating user: " + e.getMessage());
        }
    }


    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, id);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error deleting user: " + e.getMessage());
        }
    }

    @Override
    public User getById(int id) throws SQLException {
        String query = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return new User(id, rs.getString("name"), rs.getString("email"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new SQLException("Error getting user by id: " + e.getMessage());
        }
    }

    @Override
    public User getByEmail(String email) throws SQLException {
        String query = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setString(1, email);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("name"), email);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new SQLException("Error getting user by email: " + e.getMessage());
        }
    }

    @Override
    public List<User> getAll() throws SQLException {
        String query = "SELECT * FROM users";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            ResultSet rs = stm.executeQuery();
            List<User> allUsers = new ArrayList<>();
            while (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("name"), rs.getString("email"));
                allUsers.add(user);
            }
            return allUsers;
        } catch (SQLException e) {
            throw new SQLException("Error getting all users: " + e.getMessage());
        }
    }

    public boolean isExist(int userId) throws SQLException {
        String query = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, userId);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error checking user existence: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isExist(String email) throws SQLException {
        String query = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setString(1, email);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error checking user existence: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int getTotalUsersNumber() throws SQLException {
        String query = "SELECT COUNT(*) FROM users";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        } catch (SQLException e) {
            System.out.println("Error getting total users number: " + e.getMessage());
            return 0;
        }
    }
}
