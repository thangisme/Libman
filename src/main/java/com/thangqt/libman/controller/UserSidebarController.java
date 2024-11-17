package com.thangqt.libman.controller;

import com.thangqt.libman.service.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class UserSidebarController {

  private DashboardController dashboardController;

  public void setDashboardController(DashboardController dashboardController) {
    this.dashboardController = dashboardController;
  }

  @FXML
  private void loadHomeView() {
    dashboardController.setContent("home_user.fxml");
  }

  @FXML
  private void loadLoansView() {
    dashboardController.setContent("loans_user.fxml");
  }

  @FXML
  public void loadProfileView() {
    dashboardController.setContent("profile.fxml");
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
