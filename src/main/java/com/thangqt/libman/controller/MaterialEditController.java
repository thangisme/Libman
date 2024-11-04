package com.thangqt.libman.controller;

import com.thangqt.libman.model.Book;
import com.thangqt.libman.model.Magazine;
import com.thangqt.libman.model.Material;
import com.thangqt.libman.service.MaterialManager;
import javafx.scene.control.Alert;

public class MaterialEditController {
  private MaterialManager materialManager;
  private MaterialViewController parentController;

  public MaterialEditController(
      MaterialManager materialManager, MaterialViewController parentController) {
    this.materialManager = materialManager;
    this.parentController = parentController;
  }

  public void saveMaterial(
      Material material,
      String title,
      String author,
      String description,
      String publisher,
      String identifier,
      String coverImageUrl,
      int totalCopies,
      int availableCopies) {
    try {
      Material updatedMaterial =
          createMaterial(
              material,
              title,
              author,
              description,
              publisher,
              identifier,
              coverImageUrl,
              totalCopies,
              availableCopies);
      updatedMaterial.setId(material.getId());
      materialManager.updateMaterial(updatedMaterial);
      parentController.refreshMaterialsListing();
      parentController.getModalPane().hide();
      showSuccessAlert(updatedMaterial);
    } catch (NumberFormatException ex) {
      showErrorAlert("Invalid number format in copies fields");
    } catch (Exception ex) {
      ex.printStackTrace();
      showErrorAlert("An error occurred while updating the material");
    }
  }

  private Material createMaterial(
      Material material,
      String title,
      String author,
      String description,
      String publisher,
      String identifier,
      String coverImageUrl,
      int totalCopies,
      int availableCopies) {
    if (material instanceof Magazine) {
      return new Magazine(
          title,
          author,
          description,
          publisher,
          identifier,
          coverImageUrl,
          totalCopies,
          availableCopies,
          0,
          0);
    } else {
      return new Book(
          title,
          author,
          description,
          publisher,
          identifier,
          coverImageUrl,
          totalCopies,
          availableCopies);
    }
  }

  private void showSuccessAlert(Material material) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Update successful");
    alert.setHeaderText("Material updated successfully");
    alert.setContentText("Material " + material.getTitle() + " has been updated");
    alert.showAndWait();
  }

  private void showErrorAlert(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Update failed");
    alert.setHeaderText("Failed to update material");
    alert.setContentText(message);
    alert.showAndWait();
  }

  public boolean validateInputs(
      String title,
      String author,
      String publisher,
      String identifier,
      String coverImageUrl,
      String totalCopies,
      String availableCopies) {
    if (title.isEmpty()
        || author.isEmpty()
        || publisher.isEmpty()
        || identifier.isEmpty()
        || totalCopies.isEmpty()
        || availableCopies.isEmpty()) {
      showErrorAlert("All fields except cover image URL are required.");
      return false;
    }

    if (identifier.length() > 13) {
      showErrorAlert("Identifier should not be longer than 13 characters.");
      return false;
    }

    if (!identifier.matches("\\d+")) {
        showErrorAlert("Identifier should contain only digits.");
        return false;
    }

    try {
      int total = Integer.parseInt(totalCopies);
      int available = Integer.parseInt(availableCopies);

      if (total < 0 || available < 0) {
        showErrorAlert("Total copies and available copies should not be negative.");
        return false;
      }

      if (total < available) {
        showErrorAlert("Total copies should be greater than or equal to available copies.");
        return false;
      }
    } catch (NumberFormatException e) {
      showErrorAlert("Total copies and available copies should be integers.");
      return false;
    }

    if (!coverImageUrl.isEmpty() && !coverImageUrl.matches("^(http|https)://.*$")) {
      showErrorAlert("Cover image URL is not valid.");
      return false;
    }

    return true;
  }
}
