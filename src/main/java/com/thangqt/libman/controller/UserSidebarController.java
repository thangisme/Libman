package com.thangqt.libman.controller;

import javafx.fxml.FXML;

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
  private void exitApp() {
    System.exit(0);
  }
}
