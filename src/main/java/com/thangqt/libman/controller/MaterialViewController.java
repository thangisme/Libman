package com.thangqt.libman.controller;

import atlantafx.base.controls.Card;
import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import com.thangqt.libman.model.Material;
import com.thangqt.libman.service.LoanManager;
import com.thangqt.libman.service.MaterialManager;
import com.thangqt.libman.service.ServiceFactory;
import com.thangqt.libman.service.UserManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaterialViewController {
    private MaterialManager materialManager;
    private LoanManager loanManager;
    private UserManager userManager;

    @FXML
    private HBox materialsViewHeadlineContainer;

    @FXML
    private VBox materialsListingContainer;

    @FXML
    private HBox materialsViewFooterContainer;

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
        clearIcon.setOnMouseClicked(event -> searchField.clear());
        searchField.setRight(clearIcon);
        searchField.setOnAction(event -> {
            searchMaterial(searchField.getText());
        });
        HBox.setHgrow(searchField, Priority.ALWAYS);
        Button searchBtn = new Button("Search", new FontIcon(Feather.SEARCH));
        searchBtn.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.ROUNDED);
        searchBtn.setOnAction(event -> {
            searchMaterial(searchField.getText());
        });
        actionContainer.getChildren().addAll(searchField, searchBtn);
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
        pagination.setPageFactory(pageIndex -> {
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

    private void setupMaterialsListing(int startIndex, int endIndex, List<Material> materials) throws SQLException {
        materialsListingContainer.getChildren().clear();
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
            gridPane.add(materialTile, col, row);
            col++;
            if (col == columns) {
                col = 0;
                row++;
            }
        }
        materialsListingContainer.getChildren().add(gridPane);
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

            Text description = new Text(material.getDescription());
            description.getStyleClass().add(Styles.TEXT_MUTED);
            description.setWrappingWidth(200);

            infoContainer.getChildren().addAll(title, author, description);
            getChildren().add(infoContainer);

            loadImageAsync();
        }

        private void loadImageAsync() {
            Task<Image> loadImageTask = new Task<>() {
                @Override
                protected Image call() throws Exception {
                    if (material.getCoverImageUrl() == null) {
                        return new Image(getClass().getResourceAsStream("/com/thangqt/libman/images/no_cover.png"));
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
