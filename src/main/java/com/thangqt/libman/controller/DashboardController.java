package com.thangqt.libman.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class DashboardController {

  @FXML private VBox sidebarPane;

  @FXML private VBox contentPane;

  @FXML private GridPane rootPane;

  @FXML private SidebarController sidebarController;

  @FXML
  public void initialize() {
    if (sidebarController != null) {
      sidebarController.setDashboardController(this);
    } else {
      FXMLLoader loader =
          new FXMLLoader(getClass().getResource(("/com/thangqt/libman/fxml/sidebar.fxml")));
      try {
        VBox sidebar = loader.load();
        rootPane.add(sidebar, 0, 0);
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
      VBox pane = new VBox();
      pane.getChildren()
          .add(FXMLLoader.load(getClass().getResource("/com/thangqt/libman/fxml/" + fxml)));
      contentPane.getChildren().setAll(pane);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
