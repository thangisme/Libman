package com.thangqt.libman.view.GraphicalView;

import com.thangqt.libman.model.Material;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class MaterialTile extends HBox {
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
        author.getStyleClass().addAll("text-small", "text-subtle");
        author.setWrappingWidth(200);

        Text description = new Text("");
        if (material.getDescription() != null) {
            description.setText(
                    material.getDescription().substring(0, Math.min(material.getDescription().length(), 170)) + "...");
        }
        description.getStyleClass().add("text-muted");
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