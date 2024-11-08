package com.thangqt.libman.view.GraphicalView;

import com.thangqt.libman.model.Material;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class VerticalMaterialTile extends VBox {
  private Material material;
  private ImageView img;

  public VerticalMaterialTile(Material material) {
    this.material = material;
    initialize();
  }

  private void initialize() {
    this.setSpacing(10);
    img = new ImageView();
    img.setFitHeight(260);
    img.preserveRatioProperty().set(true);
    getChildren().add(img);

    VBox infoContainer = new VBox();
    infoContainer.setSpacing(5);

    Text title = new Text(material.getTitle());
    title.setWrappingWidth(200);

    Text author = new Text(material.getAuthor());
    author.getStyleClass().addAll("text-small", "text-muted");
    author.setWrappingWidth(200);

    infoContainer.getChildren().addAll(title, author);
    getChildren().add(infoContainer);

    loadImageAsync();
  }

  private void loadImageAsync() {
    Task<Image> loadImageTask =
        new Task<>() {
          @Override
          protected Image call() throws Exception {
            if (material.getCoverImageUrl() == null || material.getCoverImageUrl().isEmpty()) {
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
