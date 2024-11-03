package com.thangqt.libman.controller;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.Tile;
import atlantafx.base.layout.ModalBox;
import atlantafx.base.theme.Styles;
import com.thangqt.libman.model.*;
import com.thangqt.libman.service.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import atlantafx.base.controls.ModalPane;
import javafx.scene.text.TextFlow;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MaterialViewController {
  private MaterialManager materialManager;
  private LoanManager loanManager;
  private UserManager userManager;

  @FXML private ModalPane modalPane;

  @FXML private HBox materialsViewHeadlineContainer;

  @FXML private VBox materialsListingContainer;

  @FXML private HBox materialsViewFooterContainer;

  public MaterialViewController() throws SQLException {
    ServiceFactory serviceFactory = ServiceFactory.getInstance();
    materialManager = serviceFactory.getMaterialManager();
    loanManager = serviceFactory.getLoanManager();
    userManager = serviceFactory.getUserManager();
  }

  @FXML
  public void initialize() throws SQLException {
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
    addMaterialBtn.setOnAction(e -> showAddMaterialDialog());
    actionContainer.getChildren().addAll(searchField, searchBtn, addMaterialBtn);
    tile.setAction(actionContainer);
    HBox.setHgrow(tile, Priority.ALWAYS);
    materialsViewHeadlineContainer.getChildren().add(tile);
  }

  private void searchMaterial(String text) {
    try {
      List<Material> results = new ArrayList<>();
      if (text.isEmpty()) {
        setupPagination(materialManager.getAllMaterials());
        return;
      } else {
        if (text.matches("\\d+")) {
          Material material = materialManager.getMaterialById(Integer.parseInt(text));
          if (material != null) {
            System.out.println(material.getTitle());
            results.add(material);
          }
        }
        results.addAll(materialManager.getMaterialsByTitle(text));
        results.addAll(materialManager.getMaterialsByAuthor(text));
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
            throw new RuntimeException(e);
          }
          return new StackPane();
        });
    materialsViewFooterContainer.getChildren().clear();
    materialsViewFooterContainer.getChildren().add(pagination);
  }

  private void setupMaterialsListing(int startIndex, int endIndex, List<Material> materials)
      throws SQLException {
    materialsListingContainer.getChildren().clear();
    materialsListingContainer.setMinHeight(580);
    GridPane gridPane = new GridPane();
    gridPane.setHgap(10);
    gridPane.setVgap(10);
    int columns = 3;
    for (int i = 0; i < columns; i++) {
      ColumnConstraints columnConstraints = new ColumnConstraints();
      columnConstraints.setPercentWidth(100.0 / columns);
      gridPane.getColumnConstraints().add(columnConstraints);
    }

    int row = 0;
    int col = 0;
    for (Material material : materials.subList(startIndex, endIndex)) {
      MaterialTile materialTile = new MaterialTile(material);
      materialTile.setOnMouseClicked(e -> showMaterialDetails(material));

      gridPane.add(materialTile, col, row);
      col++;
      if (col == columns) {
        col = 0;
        row++;
      }
    }
    materialsListingContainer.getChildren().add(gridPane);
  }

  private void showAddMaterialDialog() {
    ModalBox modalBox = new ModalBox(modalPane);
    modalBox.addContent(new MaterialAddView(materialManager));
    modalBox.setMaxSize(420, 650);
    modalPane.show(modalBox);
  }

  private void showMaterialDetails(Material material) {
    ModalBox modalBox = new ModalBox(modalPane);
    modalBox.addContent(new MaterialDetailsView(material, loanManager, userManager));
    modalBox.setMaxSize(760, 360);
    modalPane.show(modalBox);
  }

  class MaterialAddView extends ScrollPane {
    private MaterialManager materialManager;

    public MaterialAddView(MaterialManager materialManager) {
      this.materialManager = materialManager;
      initialize();
    }

    private void initialize() {
      setPadding(new Insets(10));
      setMaxHeight(650);
      setFitToWidth(true);
      setVbarPolicy(ScrollBarPolicy.NEVER);
      setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

      HBox typeContainer = new HBox();
      typeContainer.setAlignment(Pos.CENTER_LEFT);
      typeContainer.setSpacing(10);

      Label typeLabel = new Label("Type: ");
      ChoiceBox<String> materialType = new ChoiceBox<>();
      materialType.getItems().addAll("Book", "Magazine");
      materialType.setValue("Book");
      typeContainer.getChildren().addAll(typeLabel, materialType);
      Label identifierLabel = new Label("Identifier");
      TextField identifierField = new TextField();
      if (materialType.getValue().equals("Magazine")) {
        identifierLabel.setText("ISSN Number");
      } else {
        identifierLabel.setText("ISBN Number");
      }
      Button fetchBookInfoBtn = new Button("Fetch book info", new FontIcon(Feather.DOWNLOAD));
      HBox identifierContainer = new HBox(identifierField);
      if (materialType.getValue().equals("Book")) {
        identifierContainer.getChildren().add(fetchBookInfoBtn);
      }
      identifierContainer.setSpacing(10);
      HBox.setHgrow(identifierField, Priority.ALWAYS);
      Text infoText = new Text();
      Label titleLabel = new Label("Title");
      TextField titleField = new TextField();
      Label authorLabel = new Label("Author");
      TextField authorField = new TextField();
      Label publisherLabel = new Label("Publisher");
      TextField publisherField = new TextField();
      Label descriptionLabel = new Label("Description");
      TextArea descriptionField = new TextArea();
      descriptionField.setWrapText(true);
      descriptionField.setPrefHeight(250);
      Label coverImageLabel = new Label("Cover image URL");
      TextField coverImageField = new TextField();
      Label totalCopiesLabel = new Label("Total copies");
      TextField totalCopiesField = new TextField();
      Label availableCopiesLabel = new Label("Available copies");
      TextField availableCopiesField = new TextField();
      materialType
          .getSelectionModel()
          .selectedItemProperty()
          .addListener(
              (observable, oldValue, newValue) -> {
                if (newValue.equals("Magazine")) {
                  identifierLabel.setText("ISSN Number");
                  fetchBookInfoBtn.setVisible(false);
                } else {
                  identifierLabel.setText("ISBN Number");
                }
              });
      totalCopiesField
          .textProperty()
          .addListener(
              (observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                  String value = newValue.replaceAll("[^\\d]", "");
                  totalCopiesField.setText(value);
                  if (availableCopiesField.getText().isEmpty()
                      || Integer.parseInt(availableCopiesField.getText())
                          > Integer.parseInt(value)) {
                    availableCopiesField.setText(value);
                  }
                }
              });
      fetchBookInfoBtn.setOnAction(
          e -> {
            if (identifierField.getText().isEmpty() && titleField.getText().isEmpty()) {
              infoText.setText("Please enter at least the identifier or title");
              infoText.getStyleClass().setAll(Styles.TEXT, Styles.DANGER);
              return;
            }
            String query = "";
            if (!identifierField.getText().isEmpty()) {
              query = identifierField.getText();
            } else {
              query = titleField.getText() + " - " + authorField.getText();
            }
            Book book = fetchBookInfo(query);
            if (book == null) {
              infoText.setText("Failed to fetch book info");
              infoText.getStyleClass().setAll(Styles.TEXT, Styles.DANGER);
              return;
            }
            titleField.setText(book.getTitle());
            authorField.setText(book.getAuthor());
            publisherField.setText(book.getPublisher());
            descriptionField.setText(book.getDescription());
            coverImageField.setText(book.getCoverImageUrl());
            infoText.setText("Book info fetched successfully");
            infoText.getStyleClass().setAll(Styles.TEXT, Styles.SUCCESS);
          });
      Button saveBtn = new Button("Save", new FontIcon(Feather.CHECK));
      saveBtn.setOnAction(
          e -> {
            modalPane.hide();
            Material newMaterial;
            if (materialType.getValue().equals("Magazine")) {
              newMaterial =
                  new Magazine(
                      titleField.getText(),
                      authorField.getText(),
                      descriptionField.getText(),
                      publisherField.getText(),
                      identifierField.getText(),
                      coverImageField.getText(),
                      Integer.parseInt(totalCopiesField.getText()),
                      Integer.parseInt(availableCopiesField.getText()),
                      0,
                      0);
            } else {
              newMaterial =
                  new Book(
                      titleField.getText(),
                      authorField.getText(),
                      descriptionField.getText(),
                      publisherField.getText(),
                      identifierField.getText(),
                      coverImageField.getText(),
                      Integer.parseInt(
                          totalCopiesField.getText() == "" ? "0" : totalCopiesField.getText()),
                      Integer.parseInt(
                          availableCopiesField.getText() == ""
                              ? "0"
                              : availableCopiesField.getText()));
            }
            try {
              materialManager.addMaterial(newMaterial);
              Alert alert = new Alert(Alert.AlertType.INFORMATION);
              alert.setTitle("Added successful");
              alert.setHeaderText("Material added successfully");
              alert.setContentText("Material " + newMaterial.getTitle() + " has been added");
              alert.showAndWait();
              materialsListingContainer.getChildren().clear();
              setupPagination(materialManager.getAllMaterials());
            } catch (SQLException ex) {
              ex.printStackTrace();
              Alert alert = new Alert(Alert.AlertType.ERROR);
              alert.setTitle("Added failed");
              alert.setHeaderText("Failed to added material");
              alert.setContentText("An error occurred while adding the material");
              alert.showAndWait();
            }
          });

      VBox formContainer = new VBox();
      formContainer.setSpacing(10);
      formContainer
          .getChildren()
          .addAll(
              typeContainer,
              identifierLabel,
              identifierContainer,
              infoText,
              titleLabel,
              titleField,
              authorLabel,
              authorField,
              publisherLabel,
              publisherField,
              descriptionLabel,
              descriptionField,
              coverImageLabel,
              coverImageField,
              totalCopiesLabel,
              totalCopiesField,
              availableCopiesLabel,
              availableCopiesField,
              saveBtn);
      setContent(formContainer);
    }

    Book fetchBookInfo(String query) {
      try {
        Book book = GoogleBooksService.fetchBookInfo(query);
        if (book == null) {
          return null;
        }
        return book;
      } catch (Exception e) {
        System.out.println("Failed to fetch book info: " + e.getMessage());
        return null;
      }
    }
  }

  class MaterialDetailsView extends VBox {
    private Material material;
    private LoanManager loanManager;
    private UserManager userManager;

    public MaterialDetailsView(
        Material material, LoanManager loanManager, UserManager userManager) {
      this.material = material;
      this.loanManager = loanManager;
      this.userManager = userManager;
      initialize();
    }

    private void initialize() {
      setPadding(new Insets(15));
      setSpacing(10);
      HBox topContainer = new HBox();

      ImageView coverImage = new ImageView();
      coverImage.setFitWidth(200);
      HBox.setMargin(coverImage, new Insets(0, 30, 0, 10));
      coverImage.preserveRatioProperty().set(true);
      if (material.getCoverImageUrl() != null) {
        coverImage.setImage(new Image(material.getCoverImageUrl()));
      } else {
        coverImage.setImage(
            new Image(getClass().getResourceAsStream("/com/thangqt/libman/images/no_cover.png")));
      }

      VBox infoContainer = new VBox();

      Text title = new Text(material.getTitle());
      title.getStyleClass().add(Styles.TITLE_2);
      title.setStyle("-fx-font-size: 36px; -fx-font-weight: 500;");

      Text author = new Text(material.getAuthor());
      author.getStyleClass().addAll(Styles.TEXT_SUBTLE);
      author.setStyle("-fx-font-size: 18px;");

      TextFlow descriptionScrollPane = new TextFlow();
      descriptionScrollPane.setMaxWidth(540);
      VBox.setMargin(descriptionScrollPane, new Insets(10, 0, 0, 0));
      Text description = new Text(material.getDescription());
      description.getStyleClass().add(Styles.TEXT_MUTED);
      description.setWrappingWidth(540);
      descriptionScrollPane.getChildren().add(description);

      infoContainer.getChildren().addAll(title, author, descriptionScrollPane);
      topContainer.getChildren().addAll(infoContainer, coverImage);

      HBox actionContainer = new HBox();
      actionContainer.setSpacing(10);
      Button issueBtn = new Button("Issue", new FontIcon(Feather.ARCHIVE));
      issueBtn.setOnAction(e -> showIssueDialog());
      Button editBtn = new Button("Edit", new FontIcon(Feather.EDIT));
      editBtn.setOnAction(e -> showEditView());
      Button deleteBtn = new Button("Delete", new FontIcon(Feather.TRASH_2));
      deleteBtn.setOnAction(e -> showConfirmDeleteDialog(material));
      actionContainer.getChildren().addAll(issueBtn, editBtn, deleteBtn);

      getChildren().addAll(topContainer, actionContainer);
    }

    private void showEditView() {
      ModalBox modalBox = new ModalBox(modalPane);
      modalBox.addContent(new MaterialEditView(material, materialManager));
      modalBox.setMaxSize(420, 650);
      modalPane.show(modalBox);
    }

    private void showIssueDialog() {
      Dialog<Loan> dialog = new Dialog<>();
      dialog.setTitle("Issue material");
      dialog.setHeaderText("Issue" + material.getTitle() + " to user");
      dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
      Label userIdLabel = new Label("User ID");
      Label loanPeriodLabel = new Label("Loan period");
      TextField userId = new TextField();
      TextField loanPeriod = new TextField("14");
      VBox form = new VBox(userIdLabel, userId, loanPeriodLabel, loanPeriod);
      form.setSpacing(10);
      dialog.getDialogPane().setContent(form);
      dialog.setResultConverter(
          buttonType -> {
            if (buttonType == ButtonType.OK) {
              LocalDate issueDate = LocalDate.now();
              LocalDate returnDate = issueDate.plusDays(Integer.parseInt(loanPeriod.getText()));
              return new Loan(
                  Integer.parseInt(userId.getText()), material.getId(), issueDate, returnDate);
            }
            return null;
          });
      dialog.showAndWait().ifPresent(newLoan -> issueLoan(newLoan));
    }

    private void issueLoan(Loan loan) {
      try {
        User user = userManager.getUserById(loan.getUserId());
        if (user == null) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Issue failed");
          alert.setHeaderText("User not found");
          alert.setContentText("No user found with the given ID");
          alert.showAndWait();
          return;
        }
        loanManager.addLoan(loan);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Issue successful");
        alert.setHeaderText("Material issued successfully");
        alert.setContentText(
            "Material " + material.getTitle() + " has been issued to " + user.getName());
        alert.showAndWait();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    private void showConfirmDeleteDialog(Material material) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Delete material");
      alert.setHeaderText("Are you sure you want to delete " + material.getTitle() + "?");
      alert.setContentText("This action cannot be undone");
      ButtonType yesBtn = new ButtonType("Yes", ButtonBar.ButtonData.YES);
      ButtonType noBtn = new ButtonType("No", ButtonBar.ButtonData.NO);
      alert.getButtonTypes().setAll(yesBtn, noBtn);
      alert
          .showAndWait()
          .ifPresent(
              buttonType -> {
                if (buttonType == yesBtn) {
                  modalPane.hide();
                  try {
                    if (loanManager.getLoansByMaterial(material.getId()).size() > 0) {
                      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                      errorAlert.setTitle("Delete failed");
                      errorAlert.setHeaderText("Material has active loans");
                      errorAlert.setContentText("Cannot delete material with active loans");
                      errorAlert.showAndWait();
                      return;
                    }
                    materialManager.deleteMaterial(material.getId());
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Delete successful");
                    successAlert.setHeaderText("Material deleted successfully");
                    successAlert.setContentText(
                        "Material " + material.getTitle() + " has been deleted");
                    successAlert.showAndWait();
                    materialsListingContainer.getChildren().clear();
                    setupPagination(materialManager.getAllMaterials());
                  } catch (SQLException e) {
                    e.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Delete failed");
                    errorAlert.setHeaderText("Failed to delete material");
                    errorAlert.setContentText("An error occurred while deleting the material");
                    errorAlert.showAndWait();
                  }
                }
              });
    }
  }

  class MaterialEditView extends ScrollPane {
    private Material material;
    private MaterialManager materialManager;

    public MaterialEditView(Material material, MaterialManager materialManager) {
      this.material = material;
      this.materialManager = materialManager;
      initialize();
    }

    private void initialize() {
      setPadding(new Insets(10));
      setMaxHeight(650);
      setFitToWidth(true);
      setVbarPolicy(ScrollBarPolicy.NEVER);
      setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

      HBox typeContainer = new HBox();
      typeContainer.setAlignment(Pos.CENTER_LEFT);
      typeContainer.setSpacing(10);

      Label typeLabel = new Label("Type: ");
      ChoiceBox<String> materialType = new ChoiceBox<>();
      materialType.getItems().addAll("Book", "Magazine");
      materialType.setValue(material.getType());
      typeContainer.getChildren().addAll(typeLabel, materialType);
      Label identifierLabel = new Label("Identifier");
      TextField identifierField = new TextField();
      if (materialType.getValue().equals("Magazine")) {
        identifierLabel.setText("ISSN Number");
        identifierField.setText(((Magazine) material).getIssn());
      } else {
        identifierLabel.setText("ISBN Number");
        identifierField.setText(((Book) material).getIsbn());
      }
      materialType
          .getSelectionModel()
          .selectedItemProperty()
          .addListener(
              (observable, oldValue, newValue) -> {
                if (newValue.equals("Magazine")) {
                  identifierLabel.setText("ISSN Number");
                  identifierField.setText(((Magazine) material).getIssn());
                } else {
                  identifierLabel.setText("ISBN Number");
                  identifierField.setText(((Book) material).getIsbn());
                }
              });
      Label titleLabel = new Label("Title");
      TextField titleField = new TextField(material.getTitle());
      Label authorLabel = new Label("Author");
      TextField authorField = new TextField(material.getAuthor());
      Label publisherLabel = new Label("Publisher");
      TextField publisherField = new TextField(material.getPublisher());
      Label descriptionLabel = new Label("Description");
      TextArea descriptionField = new TextArea(material.getDescription());
      descriptionField.setWrapText(true);
      descriptionField.setPrefHeight(250);
      Label coverImageLabel = new Label("Cover image URL");
      TextField coverImageField = new TextField(material.getCoverImageUrl());
      Label totalCopiesLabel = new Label("Total copies");
      TextField totalCopiesField = new TextField(String.valueOf(material.getQuantity()));
      Label availableCopiesLabel = new Label("Available copies");
      TextField availableCopiesField =
          new TextField(String.valueOf(material.getAvailableQuantity()));
      Button saveBtn = new Button("Save", new FontIcon(Feather.CHECK));
      saveBtn.setOnAction(
          e -> {
            modalPane.hide();
            Material updatedMaterial;
            if (materialType.getValue().equals("Magazine")) {
              updatedMaterial =
                  new Magazine(
                      titleField.getText(),
                      authorField.getText(),
                      descriptionField.getText(),
                      publisherField.getText(),
                      identifierField.getText(),
                      coverImageField.getText(),
                      Integer.parseInt(totalCopiesField.getText()),
                      Integer.parseInt(availableCopiesField.getText()),
                      0,
                      0);
            } else {
              updatedMaterial =
                  new Book(
                      titleField.getText(),
                      authorField.getText(),
                      descriptionField.getText(),
                      publisherField.getText(),
                      identifierField.getText(),
                      coverImageField.getText(),
                      Integer.parseInt(totalCopiesField.getText()),
                      Integer.parseInt(availableCopiesField.getText()));
            }
            updatedMaterial.setId(material.getId());
            try {
              materialManager.updateMaterial(updatedMaterial);
              Alert alert = new Alert(Alert.AlertType.INFORMATION);
              alert.setTitle("Update successful");
              alert.setHeaderText("Material updated successfully");
              alert.setContentText("Material " + updatedMaterial.getTitle() + " has been updated");
              alert.showAndWait();
              materialsListingContainer.getChildren().clear();
              setupPagination(materialManager.getAllMaterials());
            } catch (SQLException ex) {
              ex.printStackTrace();
              Alert alert = new Alert(Alert.AlertType.ERROR);
              alert.setTitle("Update failed");
              alert.setHeaderText("Failed to update material");
              alert.setContentText("An error occurred while updating the material");
              alert.showAndWait();
            }
          });

      VBox formContainer = new VBox();
      formContainer.setSpacing(10);
      formContainer
          .getChildren()
          .addAll(
              typeContainer,
              identifierLabel,
              identifierField,
              titleLabel,
              titleField,
              authorLabel,
              authorField,
              publisherLabel,
              publisherField,
              descriptionLabel,
              descriptionField,
              coverImageLabel,
              coverImageField,
              totalCopiesLabel,
              totalCopiesField,
              availableCopiesLabel,
              availableCopiesField,
              saveBtn);
      setContent(formContainer);
    }
  }

  class MaterialTile extends HBox {
    private Material material;
    private ImageView img;

    public MaterialTile(Material material) {
      this.material = material;
      initialize();
    }

    private void initialize() {
      this.setSpacing(10);
      img = new ImageView();
      img.setFitWidth(120);
      img.preserveRatioProperty().set(true);
      getChildren().add(img);

      VBox infoContainer = new VBox();
      infoContainer.setSpacing(5);

      Text title = new Text(material.getTitle());
      title.setWrappingWidth(200);

      Text author = new Text(material.getAuthor());
      author.getStyleClass().addAll(Styles.TEXT_SMALL, Styles.TEXT_SUBTLE);
      author.setWrappingWidth(200);

      Text description = new Text("");
      if (material.getDescription() != null) {
        description.setText(
            material
                    .getDescription()
                    .substring(0, Math.min(material.getDescription().length(), 170))
                + "...");
      }
      description.getStyleClass().add(Styles.TEXT_MUTED);
      description.setWrappingWidth(200);

      infoContainer.getChildren().addAll(title, author, description);
      getChildren().add(infoContainer);

      loadImageAsync();
    }

    private void loadImageAsync() {
      Task<Image> loadImageTask =
          new Task<>() {
            @Override
            protected Image call() throws Exception {
              if (material.getCoverImageUrl() == null) {
                return new Image(
                    getClass().getResourceAsStream("/com/thangqt/libman/images/no_cover.png"));
              } else {
                return new Image(material.getCoverImageUrl());
              }
            }

            @Override
            protected void succeeded() {
              Platform.runLater(() -> img.setImage(getValue()));
            }

            @Override
            protected void failed() {
              getException().printStackTrace();
            }
          };

      Thread loadImageThread = new Thread(loadImageTask);
      loadImageThread.setDaemon(true);
      loadImageThread.start();
    }
  }
}
