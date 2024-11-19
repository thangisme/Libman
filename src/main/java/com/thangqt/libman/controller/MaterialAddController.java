package com.thangqt.libman.controller;

import atlantafx.base.theme.Styles;
import com.thangqt.libman.model.Book;
import com.thangqt.libman.model.Magazine;
import com.thangqt.libman.model.Material;
import com.thangqt.libman.service.GoogleBooksService;
import com.thangqt.libman.service.MaterialManager;
import com.thangqt.libman.view.GraphicalView.MaterialAddView;
import javafx.scene.control.Alert;

public class MaterialAddController {
  private MaterialManager materialManager;
  private MaterialAddView view;
  private MaterialViewController parentController;

  public MaterialAddController(
      MaterialManager materialManager,
      MaterialAddView view,
      MaterialViewController parentController) {
    this.materialManager = materialManager;
    this.view = view;
    this.parentController = parentController;
  }

  public void saveMaterial(
      String type,
      String title,
      String author,
      String description,
      String publisher,
      String identifier,
      String coverImageUrl,
      int totalCopies,
      int availableCopies) {
    try {
      Material newMaterial =
          createMaterial(
              type,
              title,
              author,
              description,
              publisher,
              identifier,
              coverImageUrl,
              totalCopies,
              availableCopies);
      materialManager.addMaterial(newMaterial);
      parentController.refreshMaterialsListing();
      parentController.getModalPane().hide();
      showSuccessAlert(newMaterial);
    } catch (NumberFormatException ex) {
      showErrorAlert("Invalid number format in copies fields");
    } catch (Exception ex) {
      ex.printStackTrace();
      showErrorAlert("An error occurred while adding the material");
    }
  }

  private Material createMaterial(
      String type,
      String title,
      String author,
      String description,
      String publisher,
      String identifier,
      String coverImageUrl,
      int totalCopies,
      int availableCopies) {
    if (type.equals("Magazine")) {
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
    alert.setTitle("Add successful");
    alert.setHeaderText("Material added successfully");
    alert.setContentText("Material " + material.getTitle() + " has been added");
    alert.showAndWait();
  }

  private void showErrorAlert(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Add failed");
    alert.setHeaderText("Failed to add material");
    alert.setContentText(message);
    alert.showAndWait();
  }

  public void handleTypeChange(String value) {
    if (value.equals("Book")) {
      view.addFetchBookInfoContainer();
    } else {
      view.removeFetchBookInfoContainer();
    }
  }

  private void setInfoText(String text, String type) {
    view.getInfoText().setText(text);
    if (type.equals("error")) {
      view.getInfoText().getStyleClass().setAll(Styles.TEXT, Styles.DANGER);
    } else if (type.equals("success")) {
      view.getInfoText().getStyleClass().setAll(Styles.TEXT, Styles.SUCCESS);
    }
  }

  public void fetchBookInfo(String query) {
    if (view.getIdentifierField().getText() == null
        && view.getIdentifierField().getText().isEmpty()
        && view.getTitleField().getText() == null
        && view.getTitleField().getText().isEmpty()) {
      setInfoText("Please enter a title or an identifier", "error");
      return;
    }
    try {
      Book book = GoogleBooksService.fetchBookInfo(query);
      if (book == null) {
        setInfoText("No book found with the given query", "error");
        return;
      } else {
        view.getTitleField().setText(book.getTitle());
        view.getAuthorField().setText(book.getAuthor());
        view.getDescriptionField().setText(book.getDescription());
        view.getPublisherField().setText(book.getPublisher());
        view.getIdentifierField().setText(book.getIsbn());
        view.getCoverImageField().setText(book.getCoverImageUrl());
        setInfoText("Book info fetched successfully", "success");
      }
    } catch (Exception e) {
      e.printStackTrace();
      showErrorAlert("An error occurred while fetching book info: " + e.getMessage());
    }
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
