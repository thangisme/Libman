package com.thangqt.libman.controller;

import atlantafx.base.theme.Styles;
import com.thangqt.libman.model.User;
import com.thangqt.libman.service.ServiceFactory;
import com.thangqt.libman.service.SessionManager;
import com.thangqt.libman.service.UserManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class UserProfileController {
    private UserManager userManager;

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmNewPasswordField;
    @FXML private Text profileInfoText;
    @FXML private Text passwordInfoText;

    public UserProfileController() throws SQLException {
        this.userManager = ServiceFactory.getInstance().getUserManager();
    }

    @FXML
    public void initialize() {
        nameField.setText(SessionManager.getCurrentUser().getName());
        emailField.setText(SessionManager.getCurrentUser().getEmail());
    }

    public void updateProfile() throws SQLException {
        if (nameField.getText().isEmpty()) {
            setProfileInfoText("Name cannot be empty", "error");
            return;
        }

        if (emailField.getText().isEmpty()) {
            setProfileInfoText("Email cannot be empty", "error");
            return;
        }

        if (userManager.isUserExist(emailField.getText())) {
            setProfileInfoText("Email already exists", "error");
            return;
        }

        User user = SessionManager.getCurrentUser();
        user.getEmail();
        user.setName(nameField.getText());
        SessionManager.setCurrentUser(user);
        userManager.updateUser(user);
        setProfileInfoText("Profile updated successfully", "success");
    }

    public void updatePassword() throws SQLException {
        if (currentPasswordField.getText().isEmpty() || newPasswordField.getText().isEmpty() || confirmNewPasswordField.getText().isEmpty()) {
            setPasswordInfoText("All field are required", "error");
            return;
        }

        if (!newPasswordField.getText().equals(confirmNewPasswordField.getText())) {
            setPasswordInfoText("Password does not match", "error");
            return;
        }

        String password_hash = SessionManager.getCurrentUser().getPasswordHash();
        if (!BCrypt.checkpw(currentPasswordField.getText(), password_hash)) {
            setPasswordInfoText("Current password is incorrect", "error");
            return;
        }

        User user = SessionManager.getCurrentUser();
        user.setPasswordHash(BCrypt.hashpw(newPasswordField.getText(), BCrypt.gensalt()));
        userManager.updateUser(user);
        SessionManager.setCurrentUser(user);
        setPasswordInfoText("Password updated successfully", "success");
    }

    public void setProfileInfoText(String info, String type) {
        profileInfoText.getStyleClass().clear();
        if (type.equals("error")) {
            profileInfoText.getStyleClass().addAll(Styles.TEXT, Styles.DANGER);
        }
        if (type.equals("success")) {
            profileInfoText.getStyleClass().addAll(Styles.TEXT, Styles.SUCCESS);
        }
        profileInfoText.setText(info);
    }

    public void setPasswordInfoText(String info, String type) {
        passwordInfoText.getStyleClass().clear();
        if (type.equals("error")) {
            passwordInfoText.getStyleClass().addAll(Styles.TEXT, Styles.DANGER);
        }
        if (type.equals("success")) {
            passwordInfoText.getStyleClass().addAll(Styles.TEXT, Styles.SUCCESS);
        }
        passwordInfoText.setText(info);
    }
}
