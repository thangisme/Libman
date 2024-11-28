package com.thangqt.libman.utils;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageLoader {
  public static void loadImageAsync(String imageUrl, ImageView imageView) {
    Task<Image> loadImageTask =
        new Task<>() {
          @Override
          protected Image call() throws Exception {
            if (imageUrl == null || imageUrl.isEmpty()) {
              return new Image(
                  getClass().getResourceAsStream("/com/thangqt/libman/images/no_cover.png"));
            } else {
              Image image = new Image(imageUrl);
              if (image.isError()) {
                return new Image(
                    getClass().getResourceAsStream("/com/thangqt/libman/images/no_cover.png"));
              }
              return image;
            }
          }

          @Override
          protected void succeeded() {
            Platform.runLater(() -> imageView.setImage(getValue()));
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
