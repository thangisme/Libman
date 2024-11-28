package com.thangqt.libman.view.GraphicalView;

import com.thangqt.libman.controller.MaterialEditController;
import com.thangqt.libman.controller.MaterialViewController;
import com.thangqt.libman.model.*;
import com.thangqt.libman.model.Material;
import com.thangqt.libman.service.MaterialManager;
import com.thangqt.libman.utils.ImageLoader;
import javafx.scene.image.Image;

public class MaterialEditView extends MaterialFormView {
  private Material material;
  private MaterialEditController controller;

  public MaterialEditView(
      Material material, MaterialManager materialManager, MaterialViewController parentController) {
    this.material = material;
    this.controller = new MaterialEditController(materialManager, parentController);
    populateFields();
    setupSaveAction();
  }

  private void populateFields() {
    getTitleField().setText(material.getTitle());
    getAuthorField().setText(material.getAuthor());
    getDescriptionField().setText(material.getDescription());
    getPublisherField().setText(material.getPublisher());
    if (material.getType().equals("Book")) {
      getIdentifierField().setText(((Book) material).getIsbn());
    } else {
      getIdentifierField().setText(((Magazine) material).getIssn());
    }
    ImageLoader.loadImageAsync(material.getCoverImageUrl(), getCoverImagePreview());
    getCoverImageField().setText(material.getCoverImageUrl());
    getTotalCopiesField().setText(String.valueOf(material.getQuantity()));
    getAvailableCopiesField().setText(String.valueOf(material.getAvailableQuantity()));
  }

  private void setupSaveAction() {
    getSaveBtn()
        .setOnAction(
            e -> {
              String title = getTitleField().getText();
              String author = getAuthorField().getText();
              String description = getDescriptionField().getText();
              String publisher = getPublisherField().getText();
              String identifier = getIdentifierField().getText();
              String coverImageUrl = getCoverImageField().getText();
              int totalCopies = Integer.parseInt(getTotalCopiesField().getText());
              int availableCopies = Integer.parseInt(getAvailableCopiesField().getText());
              if (!controller.validateInputs(
                  title,
                  author,
                  publisher,
                  identifier,
                  coverImageUrl,
                  getTotalCopiesField().getText(),
                  getAvailableCopiesField().getText())) {
                return;
              }
              controller.saveMaterial(
                  material,
                  title,
                  author,
                  description,
                  publisher,
                  identifier,
                  coverImageUrl,
                  totalCopies,
                  availableCopies);
            });
  }
}
