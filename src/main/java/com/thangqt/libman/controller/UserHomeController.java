package com.thangqt.libman.controller;

import atlantafx.base.controls.ModalPane;
import atlantafx.base.layout.InputGroup;
import atlantafx.base.layout.ModalBox;
import com.thangqt.libman.model.Material;
import com.thangqt.libman.service.LoanManager;
import com.thangqt.libman.service.MaterialManager;
import com.thangqt.libman.service.ServiceFactory;
import com.thangqt.libman.service.UserManager;
import com.thangqt.libman.view.GraphicalView.VerticalMaterialTile;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class UserHomeController {
  private UserManager userManager;
  private MaterialManager materialManager;
  private LoanManager loanManager;

  @FXML private HBox recentMaterialsContainer;
  @FXML private HBox popularMaterialsContainer;
  @FXML private HBox curatedMaterialsContainer;
  @FXML private ModalPane topModalPane;
  @FXML private ModalPane innerModalPane;
  @FXML private VBox searchFieldContainer;
  @FXML private TextField searchField;

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

    topModalPane.setInTransitionFactory(null);
    topModalPane.setOutTransitionFactory(null);
    innerModalPane.setInTransitionFactory(null);
    innerModalPane.setOutTransitionFactory(null);

    Platform.runLater(() -> searchFieldContainer.requestFocus());
  }

  public void setUpRecentlyAddedMaterials(int numMaterials) {
    try {
      List<Material> recentMaterials = materialManager.getRecentlyAddedMaterials(numMaterials);
      if (recentMaterials.isEmpty()) {
        recentMaterialsContainer.getChildren().add(new Text("No recently added materials found"));
        return;
      }
      recentMaterials.forEach(
          material -> {
            VerticalMaterialTile tile = new VerticalMaterialTile(material, this);
            tile.setOnMouseClicked(event -> showMaterialDetails(material));
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
      List<Material> popularMaterials = materialManager.getPopularMaterials(numMaterials, 30);
      if (popularMaterials.isEmpty()) {
        popularMaterialsContainer.getChildren().add(new Text("No popular materials found"));
        return;
      }
      popularMaterials.forEach(
          material -> {
            VerticalMaterialTile tile = new VerticalMaterialTile(material, this);
            tile.setOnMouseClicked(event -> showMaterialDetails(material));
            popularMaterialsContainer.getChildren().add(tile);
          });
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      popularMaterialsContainer.getChildren().add(new Text("Failed to load popular materials"));
    }
  }

  public void setUpCuratedMaterials(int numMaterials, int[] materialIds) {
    List<Material> materialList = new ArrayList<>();
    for (int id : materialIds) {
      try {
        Material material = materialManager.getMaterialById(id);
        if (material != null) materialList.add(material);
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }
    }
    if (materialList.isEmpty()) {
      curatedMaterialsContainer.getChildren().add(new Text("Failed to load curated materials"));
      return;
    }
    materialList.forEach(
        material -> {
          VerticalMaterialTile tile = new VerticalMaterialTile(material, this);
          tile.setOnMouseClicked(event -> showMaterialDetails(material));
          curatedMaterialsContainer.getChildren().add(tile);
        });
  }

  public void searchMaterials(ActionEvent actionEvent) {
    String query = searchField.getText();
    if (query.isEmpty()) {
      return;
    }
    ModalBox dialog = new ModalBox(topModalPane);
    VBox content = new VBox();
    content.setPadding(new Insets(30));
    content.setFillWidth(true);
    VBox.setVgrow(content, Priority.ALWAYS);
    content.setAlignment(Pos.CENTER);
    content.setMinWidth(1000);
    TextField dialogSearchField = new TextField();
    dialogSearchField.setText(query);
    Button searchButton = new Button("Search", new FontIcon(Feather.SEARCH));
    InputGroup actionContainer = new InputGroup();
    actionContainer.getChildren().addAll(dialogSearchField, searchButton);
    content.getChildren().add(actionContainer);
    VBox searchResultsContainer = new VBox();
    searchResultsContainer.setFillWidth(true);
    searchResultsContainer.setSpacing(20);
    content.getChildren().add(searchResultsContainer);
    dialogSearchField.setOnAction(
        event -> {
          searchResultsContainer.getChildren().clear();
          populateSearchResults(searchResultsContainer, dialogSearchField.getText());
        });
    searchButton.setOnAction(
        event -> {
          searchResultsContainer.getChildren().clear();
          populateSearchResults(searchResultsContainer, dialogSearchField.getText());
        });
    VBox.setMargin(actionContainer, new Insets(0, 0, 20, 0));
    populateSearchResults(searchResultsContainer, query);
    dialog.addContent(content);
    topModalPane.show(dialog);
  }

  void populateSearchResults(VBox content, String query) {
    if (query.isEmpty()) {
      return;
    }
    try {
      List<Material> results = materialManager.searchMaterials(query);
      if (results.isEmpty()) {
        content.getChildren().add(new Text("No results found"));
      } else {
        final int NUM_COLUMNS = 5;
        for (int i = 0; i < results.size(); i += NUM_COLUMNS) {
          HBox row = new HBox();
          row.setMaxWidth(Double.MAX_VALUE);
          row.setAlignment(Pos.CENTER);
          row.setSpacing(10);
          for (int j = 0; j < NUM_COLUMNS; j++) {
            if (i + j < results.size()) {
              Material material = results.get(i + j);
              VerticalMaterialTile entry = new VerticalMaterialTile(material, this);
              entry.setOnMouseClicked(event -> showMaterialDetails(material));
              HBox.setHgrow(entry, Priority.ALWAYS);
              entry.setMaxWidth(Double.MAX_VALUE);
              row.getChildren().add(entry);
            }
          }
          content.getChildren().add(row);
        }
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      content.getChildren().add(new Text("Failed to load search results"));
    }
  }

  public void showMaterialDetails(Material material) {
    ModalBox modalBox = new ModalBox(innerModalPane);
    UserMaterialDetailsController controller =
        new UserMaterialDetailsController(
            material, loanManager, userManager, materialManager, this);
    modalBox.addContent(controller.createMaterialDetailsView());
    modalBox.setMaxSize(420, 300);
    innerModalPane.show(modalBox);
  }

  public ModalPane getInnerModalPane() {
    return innerModalPane;
  }
}
