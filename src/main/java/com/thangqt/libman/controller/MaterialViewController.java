package com.thangqt.libman.controller;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.ModalPane;
import atlantafx.base.controls.Tile;
import atlantafx.base.layout.ModalBox;
import atlantafx.base.theme.Styles;
import com.thangqt.libman.model.Material;
import com.thangqt.libman.service.*;
import com.thangqt.libman.utils.QRHelper;
import com.thangqt.libman.view.GraphicalView.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class MaterialViewController {
  private MaterialManager materialManager;
  private LoanManager loanManager;
  private UserManager userManager;
  private MaterialGridLayout gridLayout;

  @FXML private HBox materialsViewHeadlineContainer;
  @FXML private VBox materialsListingContainer;
  @FXML private HBox materialsViewFooterContainer;
  @FXML private ModalPane modalPane;

  public MaterialViewController() throws SQLException {
    materialManager = ServiceFactory.getInstance().getMaterialManager();
    loanManager = ServiceFactory.getInstance().getLoanManager();
    userManager = ServiceFactory.getInstance().getUserManager();
  }

  @FXML
  public void initialize() throws SQLException {
    gridLayout = new MaterialGridLayout();
    materialsListingContainer.getChildren().add(gridLayout);
    setupTile();
    setupPagination(materialManager.getAllMaterials());
    modalPane.setInTransitionFactory(null);
    modalPane.setOutTransitionFactory(null);
  }

  private void setupTile() {
    Tile tile = new Tile();
    tile.setTitle("Materials");
    tile.setDescription("Manage and view material details");
    HBox actionContainer = new HBox();
    actionContainer.setSpacing(5);
    CustomTextField searchField = new CustomTextField();
    searchField.setPromptText("Search materials");
    searchField.getStyleClass().add(Styles.ROUNDED);
    FontIcon clearIcon = new FontIcon(Feather.X);
    clearIcon.setCursor(Cursor.HAND);
    clearIcon.setOnMouseClicked(
        event -> {
          searchField.clear();
          try {
            setupPagination(materialManager.getAllMaterials());
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });
    searchField.setRight(clearIcon);
    searchField.setOnAction(
        event -> {
          searchMaterial(searchField.getText());
        });
    HBox.setHgrow(searchField, Priority.ALWAYS);
    Button searchBtn = new Button("Search", new FontIcon(Feather.SEARCH));
    searchBtn.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.ROUNDED);
    searchBtn.setOnAction(
        event -> {
          searchMaterial(searchField.getText());
        });
    Button addMaterialBtn = new Button("Add material", new FontIcon(Feather.PLUS));
    addMaterialBtn.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.ROUNDED);
    addMaterialBtn.setOnAction(e -> showAddMaterialForm());
    actionContainer.getChildren().addAll(searchField, searchBtn, addMaterialBtn);
    tile.setAction(actionContainer);
    HBox.setHgrow(tile, Priority.ALWAYS);
    materialsViewHeadlineContainer.getChildren().add(tile);
  }

  private void setupPagination(List<Material> materials) {
    Pagination pagination = new Pagination((materials.size() / 9) + 1, 0);
    pagination.setMaxPageIndicatorCount(5);
    pagination.setPageFactory(
        pageIndex -> {
          int fromIndex = pageIndex * 9;
          int toIndex = Math.min(fromIndex + 9, materials.size());
          try {
            setupMaterialsListing(fromIndex, toIndex, materials);
          } catch (SQLException e) {
            e.printStackTrace();
          }
          return new VBox();
        });
    materialsViewFooterContainer.getChildren().clear();
    materialsViewFooterContainer.getChildren().add(pagination);
  }

  private void setupMaterialsListing(int startIndex, int endIndex, List<Material> materials)
      throws SQLException {
    gridLayout.displayMaterials(materials, startIndex, endIndex, this::showMaterialDetails);
  }

  public void refreshMaterialsListing() {
    try {
      materialsListingContainer.getChildren().clear();
      gridLayout = new MaterialGridLayout();
      materialsListingContainer.getChildren().add(gridLayout);
      setupPagination(materialManager.getAllMaterials());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public ModalPane getModalPane() {
    return modalPane;
  }

  private void showMaterialDetails(Material material) {
    MaterialDetailsController materialDetailsController =
        new MaterialDetailsController(material, loanManager, userManager, materialManager, this);
    MaterialDetailsView materialDetailsView = materialDetailsController.createMaterialDetailsView();

    ModalBox modalBox = new ModalBox(modalPane);
    modalBox.addContent(materialDetailsView);
    modalBox.setMaxSize(420, 100);
    modalPane.show(modalBox);
  }

  public void showEditMaterialForm(Material material) {
    MaterialEditView formView = new MaterialEditView(material, materialManager, this);
    ModalBox modalBox = new ModalBox(modalPane);
    modalBox.addContent(formView);
    modalBox.setMaxSize(420, 650);
    modalPane.show(modalBox);
  }

  public void showAddMaterialForm() {
    MaterialAddView addView = new MaterialAddView(materialManager, this);
    ModalBox modalBox = new ModalBox(modalPane);
    modalBox.addContent(addView);
    modalBox.setMaxSize(420, 650);
    modalPane.show(modalBox);
  }

  public void showMaterialQrCode(Material material) {
    ModalBox modalBox = new ModalBox(modalPane);
    VBox qrCodeContainer = new VBox();
    qrCodeContainer.setPadding(new Insets(20));
    qrCodeContainer.setSpacing(10);
    qrCodeContainer.setAlignment(Pos.CENTER);
    QRHelper qrHelper = new QRHelper();
    Image qrImage = qrHelper.generateQRCode(String.valueOf(material.getId()), 360, 360);
    ImageView qrImageView = new ImageView(qrImage);
    Button printBtn = new Button("Print QR code", new FontIcon(Feather.PRINTER));
    qrCodeContainer.getChildren().addAll(qrImageView, printBtn);
    modalBox.addContent(qrCodeContainer);
    modalBox.setMaxSize(400, 400);
    modalPane.show(modalBox);
  }

  private void searchMaterial(String query) {
    try {
      List<Material> results = new ArrayList<>();
      if (query.isEmpty()) {
        setupPagination(materialManager.getAllMaterials());
        return;
      } else {
        if (query.matches("\\d+")) {
          Material material = materialManager.getMaterialById(Integer.parseInt(query));
          if (material != null) {
            System.out.println(material.getTitle());
            results.add(material);
          }
        }
        results.addAll(materialManager.getMaterialsByTitle(query));
        results.addAll(materialManager.getMaterialsByAuthor(query));
        if (results.isEmpty()) {
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setTitle("Search result");
          alert.setHeaderText("No result found");
          alert.setContentText("No material found with the given search query");
          alert.showAndWait();
          return;
        }
      }
      setupPagination(results);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
