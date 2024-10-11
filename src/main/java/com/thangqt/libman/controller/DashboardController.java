package com.thangqt.libman.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class DashboardController {

    @FXML
    private VBox sidebarPane;

    @FXML
    private VBox contentPane;

    @FXML
    private BorderPane rootPane;

    @FXML
    private SidebarController sidebarController;

    @FXML
    public void initialize() {
        if (sidebarController != null) {
            sidebarController.setDashboardController(this);
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(("/com/thangqt/libman/fxml/sidebar.fxml")));
            try {
                VBox sidebar = loader.load();
                rootPane.setLeft(sidebar);
                sidebarController = loader.getController();
                sidebarController.setDashboardController(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setContent("home.fxml");
    }

    public void setContent(String fxml) {
        try {
            VBox pane = FXMLLoader.load(getClass().getResource("/com/thangqt/libman/fxml/" + fxml));
            contentPane.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
