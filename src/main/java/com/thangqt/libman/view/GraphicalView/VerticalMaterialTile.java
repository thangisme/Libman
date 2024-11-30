package com.thangqt.libman.view.GraphicalView;

import com.thangqt.libman.controller.UserHomeController;
import com.thangqt.libman.model.Material;
import com.thangqt.libman.service.ReviewManager;
import com.thangqt.libman.service.ServiceFactory;
import com.thangqt.libman.utils.ImageLoader;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.SQLException;

public class VerticalMaterialTile extends VBox {
  private Material material;
  private ImageView img;
  private UserHomeController controller;

  public VerticalMaterialTile(Material material, UserHomeController controller) {
    this.material = material;
    this.controller = controller;
    initialize();
  }

  private void initialize() {
    this.setSpacing(10);
    img = new ImageView();
    img.setFitHeight(260);
    img.preserveRatioProperty().set(true);
    img.getStyleClass().add("image-view");
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

    ImageLoader.loadImageAsync(material.getCoverImageUrl(), img);
  }
}
