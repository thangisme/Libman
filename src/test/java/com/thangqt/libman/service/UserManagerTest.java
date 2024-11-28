package com.thangqt.libman.service;

import com.thangqt.libman.DAO.UserDAO;
import com.thangqt.libman.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/** Unit tests for the UserManager class. */
@ExtendWith(MockitoExtension.class)
public class UserManagerTest {

  @Mock private UserDAO userDAO;

  private UserManager userManager;

  /**
   * Sets up the test environment before each test. Initializes the UserManager with a mocked
   * UserDAO.
   */
  @BeforeEach
  void setUp() {
    userManager = new UserManager(userDAO);
  }

  /**
   * Tests the authenticate method with valid credentials. Verifies that the method returns the
   * expected User object.
   *
   * @throws SQLException if a database access error occurs
   */
  @Test
  void authenticate_WithValidCredentials_ShouldReturnUser() throws SQLException {
    String email = "test@example.com";
    String password = "password";
    User expectedUser = new User(1, "Test User", email);
    when(userDAO.authenticate(email, password)).thenReturn(expectedUser);

    User result = userManager.authenticate(email, password);

    // Assert: Verify the result and interactions with the mock
    assertEquals(expectedUser, result);
    verify(userDAO).authenticate(email, password);
  }

  /**
   * Tests the addUser method. Verifies that the method returns the added User object.
   *
   * @throws SQLException if a database access error occurs
   */
  @Test
  void addUser_ShouldReturnAddedUser() throws SQLException {
    User newUser = new User("Test User", "test@example.com");
    when(userDAO.add(newUser)).thenReturn(newUser);

    User result = userManager.addUser(newUser);

    // Assert: Verify the result and interactions with the mock
    assertEquals(newUser, result);
    verify(userDAO).add(newUser);
  }

  /**
   * Tests the searchUserByName method. Verifies that the method returns a list of users matching
   * the search criteria.
   *
   * @throws SQLException if a database access error occurs
   */
  @Test
  void searchUserByName_ShouldReturnMatchingUsers() throws SQLException {
    String searchName = "John";
    List<User> expectedUsers =
        Arrays.asList(
            new User(1, "John Doe", "john@example.com"),
            new User(2, "Johnny Smith", "johnny@example.com"));
    when(userDAO.searchByName(searchName)).thenReturn(expectedUsers);

    List<User> result = userManager.searchUserByName(searchName);

    // Assert: Verify the result and interactions with the mock
    assertEquals(expectedUsers, result);
    verify(userDAO).searchByName(searchName);
  }

  /**
   * Tests the isUserExist method with a valid email. Verifies that the method returns true if the
   * user exists.
   *
   * @throws SQLException if a database access error occurs
   */
  @Test
  void isUserExist_WithValidEmail_ShouldReturnTrue() throws SQLException {
    String email = "test@example.com";
    when(userDAO.isExist(email)).thenReturn(true);

    boolean result = userManager.isUserExist(email);

    assertTrue(result);
    verify(userDAO).isExist(email);
  }

  /**
   * Tests the updateUser method. Verifies that the method calls the update method on the UserDAO.
   *
   * @throws SQLException if a database access error occurs
   */
  @Test
  void updateUser_ShouldCallUpdateOnUserDAO() throws SQLException {
    User user = new User(1, "Updated User", "updated@example.com");

    userManager.updateUser(user);

    verify(userDAO).update(user);
  }

  /**
   * Tests the deleteUser method. Verifies that the method calls the delete method on the UserDAO.
   *
   * @throws SQLException if a database access error occurs
   */
  @Test
  void deleteUser_ShouldCallDeleteOnUserDAO() throws SQLException {
    int userId = 1;

    userManager.deleteUser(userId);

    verify(userDAO).delete(userId);
  }

  /**
   * Tests the getUserById method. Verifies that the method returns the expected User object.
   *
   * @throws SQLException if a database access error occurs
   */
  @Test
  void getUserById_ShouldReturnUser() throws SQLException {
    int userId = 1;
    User expectedUser = new User(userId, "Test User", "test@example.com");
    when(userDAO.getById(userId)).thenReturn(expectedUser);

    User result = userManager.getUserById(userId);

    assertEquals(expectedUser, result);
    verify(userDAO).getById(userId);
  }

  /**
   * Tests the getUserByEmail method. Verifies that the method returns the expected User object.
   *
   * @throws SQLException if a database access error occurs
   */
  @Test
  void getUserByEmail_ShouldReturnUser() throws SQLException {
    String email = "test@example.com";
    User expectedUser = new User(1, "Test User", email);
    when(userDAO.getByEmail(email)).thenReturn(expectedUser);

    User result = userManager.getUserByEmail(email);

    assertEquals(expectedUser, result);
    verify(userDAO).getByEmail(email);
  }

  /**
   * Tests the getAllUsers method. Verifies that the method returns a list of all users.
   *
   * @throws SQLException if a database access error occurs
   */
  @Test
  void getAllUsers_ShouldReturnAllUsers() throws SQLException {
    List<User> expectedUsers = Arrays.asList(
            new User(1, "User 1", "user1@example.com"),
            new User(2, "User 2", "user2@example.com")
    );
    when(userDAO.getAll()).thenReturn(expectedUsers);

    List<User> result = userManager.getAllUsers();

    assertEquals(expectedUsers, result);
    verify(userDAO).getAll();
  }

  /**
   * Tests the getTotalUsersNumber method. Verifies that the method returns the total number of users.
   *
   * @throws SQLException if a database access error occurs
   */
  @Test
  void getTotalUsersNumber_ShouldReturnTotalNumberOfUsers() throws SQLException {
    int totalUsers = 10;
    when(userDAO.getTotalUsersNumber()).thenReturn(totalUsers);

    int result = userManager.getTotalUsersNumber();

    assertEquals(totalUsers, result);
    verify(userDAO).getTotalUsersNumber();
  }
}
