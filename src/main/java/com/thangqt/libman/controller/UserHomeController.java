package com.thangqt.libman.controller;

import com.thangqt.libman.model.Material;
import com.thangqt.libman.service.LoanManager;
import com.thangqt.libman.service.MaterialManager;
import com.thangqt.libman.service.ServiceFactory;
import com.thangqt.libman.service.UserManager;
import com.thangqt.libman.view.GraphicalView.VerticalMaterialTile;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class UserHomeController {
  private UserManager userManager;
  private MaterialManager materialManager;
  private LoanManager loanManager;

  @FXML private HBox recentMaterialsContainer;
  @FXML private HBox popularMaterialsContainer;
  @FXML private HBox curatedMaterialsContainer;

  public UserHomeController() throws SQLException {
    materialManager = ServiceFactory.getInstance().getMaterialManager();
    userManager = ServiceFactory.getInstance().getUserManager();
    loanManager = ServiceFactory.getInstance().getLoanManager();
  }

  public void initialize() {
    final int NUM_MATERIALS_TO_DISPLAY = 10;
    int[] curatedMaterialIds = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    setUpRecentlyAddedMaterials(NUM_MATERIALS_TO_DISPLAY);
    setUpPopularMaterials(NUM_MATERIALS_TO_DISPLAY);
    setUpCuratedMaterials(NUM_MATERIALS_TO_DISPLAY, curatedMaterialIds);
  }

  public void setUpRecentlyAddedMaterials(int numMaterials) {
    try {
      materialManager
          .getRecentlyAddedMaterials(numMaterials)
          .forEach(
              material -> {
                VerticalMaterialTile tile = new VerticalMaterialTile(material);
                recentMaterialsContainer.getChildren().add(tile);
              });
    } catch (SQLException e) {
        System.out.println(e.getMessage());
      recentMaterialsContainer
          .getChildren()
          .add(new Text("Failed to load recently added materials"));
    }
  }

    public void setUpPopularMaterials(int numMaterials) {
        try {
        materialManager
            .getPopularMaterials(numMaterials, 30)
            .forEach(
                material -> {
                    VerticalMaterialTile tile = new VerticalMaterialTile(material);
                    popularMaterialsContainer.getChildren().add(tile);
                });
        } catch (SQLException e) {
          System.out.println(e.getMessage());
        popularMaterialsContainer
            .getChildren()
            .add(new Text("Failed to load popular materials"));
        }
    }

    public void setUpCuratedMaterials(int numMaterials, int[] materialIds) {
        List<Material> materialList = new ArrayList<>();
        for (int id : materialIds) {
            try {
                Material material = materialManager.getMaterialById(id);
                materialList.add(material);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        materialList.forEach(
            material -> {
                VerticalMaterialTile tile = new VerticalMaterialTile(material);
                curatedMaterialsContainer.getChildren().add(tile);
            });
    }

  public void handleSearch(ActionEvent actionEvent) {
  }
}
