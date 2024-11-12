package com.thangqt.libman.controller;

import com.thangqt.libman.service.SessionManager;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class SidebarController {

  private DashboardController dashboardController;

  public void setDashboardController(DashboardController dashboardController) {
    this.dashboardController = dashboardController;
  }

  @FXML
  private void loadHomeView() {
    dashboardController.setContent("home.fxml");
  }

  @FXML
  private void loadUsersView() {
    dashboardController.setContent("users.fxml");
  }

  @FXML
  private void loadMaterialsView() {
    dashboardController.setContent("materials.fxml");
  }

  @FXML
  private void loadLoansView() {
    dashboardController.setContent("loans.fxml");
  }

  @FXML
  private void exitApp() {
    System.exit(0);
  }

  @FXML
  private void logOut() {
    SessionManager.clearSession();
    Stage stage = (Stage) dashboardController.getRootPane().getScene().getWindow();
    stage.close();
    dashboardController.showLoginStage();
  }
}
