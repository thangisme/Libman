package com.thangqt.libman.controller;

import com.thangqt.libman.service.SessionManager;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class DashboardController {

  @FXML private VBox contentPane;

  @FXML private GridPane rootPane;

  @FXML private SidebarController sidebarController;
  @FXML private UserSidebarController userSidebarController;

  @FXML
  public void initialize() {
    loadSidebar();
    if (SessionManager.isAdmin()) {
      setContent("home.fxml");
    } else {
      setContent("home_user.fxml");
    }
  }

  private void loadSidebar() {
    String sidebarFxml = SessionManager.isAdmin() ? "/com/thangqt/libman/fxml/sidebar.fxml" : "/com/thangqt/libman/fxml/sidebar_user.fxml";
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(sidebarFxml));
      VBox sidebar = loader.load();
      sidebar.getStyleClass().add("sidebarPane");
      rootPane.add(sidebar, 0, 0);
      if (SessionManager.isAdmin()) {
        sidebarController = loader.getController();
        sidebarController.setDashboardController(this);
      } else {
        userSidebarController = loader.getController();
        userSidebarController.setDashboardController(this);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setContent(String fxml) {
    try {
      VBox pane = new VBox();
      pane.getChildren().add(FXMLLoader.load(getClass().getResource("/com/thangqt/libman/fxml/" + fxml)));
      contentPane.getChildren().setAll(pane);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}