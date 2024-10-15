package com.thangqt.libman.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

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
    

}