package com.thangqt.libman;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class GUIController {
    @FXML
    private Label welcomeText;

    @FXML
    private Text actiontarget;

    @FXML
    protected void login() {
        actiontarget.setText("Sign in button pressed");
    }
}