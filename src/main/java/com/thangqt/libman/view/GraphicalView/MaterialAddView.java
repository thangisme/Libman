package com.thangqt.libman.view.GraphicalView;

import com.thangqt.libman.controller.MaterialAddController;
import com.thangqt.libman.controller.MaterialViewController;
import com.thangqt.libman.service.MaterialManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class MaterialAddView extends MaterialFormView {
  private MaterialAddController controller;
  private ChoiceBox<String> materialType;

  public MaterialAddView(MaterialManager materialManager, MaterialViewController parentController) {
    this.controller = new MaterialAddController(materialManager, this, parentController);

    addTypeSelector();
    setupSaveAction();
  }

  private void addTypeSelector() {
    materialType = new ChoiceBox<>();
    materialType.getItems().addAll("Book", "Magazine");
    materialType.setValue("Book");
    materialType.setOnAction(e -> controller.handleTypeChange(materialType.getValue()));

    HBox typeContainer = new HBox(10);
    typeContainer.setAlignment(Pos.CENTER_LEFT);
    typeContainer.setSpacing(10);
    typeContainer.getChildren().addAll(new Label("Type: "), materialType);

    formContainer.getChildren().add(0, typeContainer);
    addFetchBookInfoContainer();
  }

  public void addFetchBookInfoContainer() {
    HBox fetchBookInfoContainer = new HBox(10);
    HBox.setHgrow(fetchBookInfoContainer, Priority.ALWAYS);
    fetchBookInfoContainer.setAlignment(Pos.CENTER_RIGHT);
    fetchBookInfoContainer.setSpacing(10);
    Text infoText = new Text("");
    Button fetchBookInfoBtn = new Button("Fetch info", new FontIcon(Feather.INFO));
    fetchBookInfoBtn.setOnAction(
        e -> {
          String query = "";
          if (getIdentifierField().getText() != null && !getIdentifierField().getText().isEmpty()) {
            query = getIdentifierField().getText();
          } else {
            query = getTitleField().getText() + " - " + getAuthorField().getText();
          }
          controller.fetchBookInfo(query);
        });
    fetchBookInfoContainer.getChildren().addAll(infoText, fetchBookInfoBtn);
    formContainer.getChildren().add(1, fetchBookInfoContainer);
  }

  public Text getInfoText() {
    return (Text) ((HBox) formContainer.getChildren().get(1)).getChildren().get(0);
  }

  public void removeFetchBookInfoContainer() {
    formContainer.getChildren().remove(1);
  }

  private void setupSaveAction() {
    getSaveBtn()
        .setOnAction(
            e -> {
              String type = materialType.getValue();
              String title = getTitleField().getText();
              String author = getAuthorField().getText();
              String description = getDescriptionField().getText();
              if (description == null) description = "";
              String publisher = getPublisherField().getText();
              if (publisher == null || publisher.isEmpty()) {
                publisher = "Unknown";
              }
              String identifier = getIdentifierField().getText();
              if (identifier == null) identifier = "";
              String coverImageUrl = getCoverImageField().getText();
              if (coverImageUrl == null) coverImageUrl = "";
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
                  type,
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
