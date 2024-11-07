package com.thangqt.libman.controller;

import atlantafx.base.theme.Styles;
import com.thangqt.libman.model.User;
import com.thangqt.libman.service.ServiceFactory;
import com.thangqt.libman.service.SessionManager;
import com.thangqt.libman.service.UserManager;
import java.io.IOException;
import java.sql.SQLException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.validator.routines.EmailValidator;
import org.mindrot.jbcrypt.BCrypt;

public class SignUpController {
  @FXML private VBox signupForm;
  @FXML private TextField nameField;
  @FXML private TextField emailField;
  @FXML private PasswordField passwordField;
  @FXML private Label errorMessage;

  private final UserManager userManager;

  public SignUpController() throws SQLException {
    ServiceFactory serviceFactory = ServiceFactory.getInstance();
    this.userManager = serviceFactory.getUserManager();
  }

  @FXML
  private void initialize() {
    // Remove autofocus on email field
    Platform.runLater(() -> signupForm.requestFocus());

    signupForm.setOnKeyPressed(event -> {
      if (event.getCode().equals(KeyCode.ENTER)) {
        handleSignUp();
      }
    });
  }

  @FXML
  private void handleSignUp() {
    String name = nameField.getText();
    String email = emailField.getText();
    String password = passwordField.getText();

    if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
      errorMessage.setText("All field are required");
      return;
    }

    if (!isValidEmail(email)) {
      errorMessage.setText("Email is invalid.");
      return;
    }

    try {
      if (userManager.getUserByEmail(email) != null) {
        errorMessage.setText("An user with the specified email already existed.");
        return;
      }
      String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
      User user = userManager.addUser(new User(name, email, "USER", passwordHash));
      if (user != null) {
        SessionManager.setCurrentUser(user);
        loadDashboard();
      } else {
        errorMessage.setText("Invalid email or password");
      }
    } catch (Exception e) {
      errorMessage.setText("An error occurred during sign up");
    }
  }

  private void loadDashboard() {
    try {
      Stage stage = (Stage) emailField.getScene().getWindow();
      FXMLLoader dashboardLoader =
          new FXMLLoader(getClass().getResource("/com/thangqt/libman/fxml/dashboard.fxml"));
      Scene dashboardScene = new Scene(dashboardLoader.load(), 1360, 760);
      dashboardScene
          .getStylesheets()
          .add(getClass().getResource("/com/thangqt/libman/style.css").toExternalForm());
      stage.setScene(dashboardScene);
      stage.setTitle("Libman");
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void loadLogInForm() {
    try {
      Stage stage = (Stage) emailField.getScene().getWindow();
      FXMLLoader loginLoader =
          new FXMLLoader(getClass().getResource("/com/thangqt/libman/fxml/login.fxml"));
      Scene loginScene = new Scene(loginLoader.load(), 1360, 760);
      stage.setScene(loginScene);
      stage.setTitle("Login");
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public boolean isValidEmail(String email) {
    return EmailValidator.getInstance().isValid(email);
  }
}
