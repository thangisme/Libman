package com.thangqt.libman.controller;

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

public class LoginController {
  @FXML private VBox loginForm;
  @FXML private TextField emailField;
  @FXML private PasswordField passwordField;
  @FXML private Label errorMessage;

  private final UserManager userManager;

  public LoginController() throws SQLException {
    ServiceFactory serviceFactory = ServiceFactory.getInstance();
    this.userManager = serviceFactory.getUserManager();
  }

  @FXML
  private void initialize() {
    // Remove autofocus on email field
    Platform.runLater(() -> loginForm.requestFocus());

    loginForm.setOnKeyPressed(event -> {
      if (event.getCode().equals(KeyCode.ENTER)) {
        handleLogin();
      }
    });
  }

  @FXML
  private void handleLogin() {
    String email = emailField.getText();
    String password = passwordField.getText();

    if (email.isEmpty() || password.isEmpty()) {
      errorMessage.setText("All field are required.");
      return;
    }

    if (!isValidEmail(email)) {
      errorMessage.setText("Email is invalid.");
      return;
    }

    try {
      User user = userManager.authenticate(email, password);
      if (user != null) {
        SessionManager.setCurrentUser(user);
        loadDashboard();
      } else {
        errorMessage.setText("Invalid email or password");
      }
    } catch (Exception e) {
      errorMessage.setText("An error occurred during login");
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

  public void loadSignupForm() {
    try {
      Stage stage = (Stage) emailField.getScene().getWindow();
      FXMLLoader signupLoader =
          new FXMLLoader(getClass().getResource("/com/thangqt/libman/fxml/signup.fxml"));
      Scene signupScene = new Scene(signupLoader.load(), 1360, 760);
      signupScene
          .getStylesheets()
          .add(getClass().getResource("/com/thangqt/libman/style.css").toExternalForm());
      stage.setScene(signupScene);
      stage.setTitle("Sign Up");
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public boolean isValidEmail(String email) {
    return EmailValidator.getInstance().isValid(email);
  }
}
