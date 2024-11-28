package com.thangqt.libman.view.GraphicalView;

import com.thangqt.libman.utils.ImageLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class MaterialFormView extends ScrollPane {
  protected VBox formContainer;
  private VBox titleContainer;
  private VBox authorContainer;
  private VBox descriptionContainer;
  private VBox publisherContainer;
  private VBox identifierContainer;
  private VBox coverImageContainer;
  private VBox totalCopiesContainer;
  private VBox availableCopiesContainer;
  private VBox coverImagePreviewContainer;
  private ImageView coverImagePreview;
  // The actual input fields
  private TextField titleField;
  private TextField authorField;
  private TextArea descriptionField;
  private TextField publisherField;
  private TextField identifierField;
  private TextField coverImageField;
  private TextField totalCopiesField;
  private TextField availableCopiesField;
  private Button saveBtn;
  private Image coverImage;

  public MaterialFormView() {
    setupForm();
  }

  protected void setupForm() {
    setPadding(new Insets(10));
    setMaxHeight(650);
    setFitToWidth(true);
    setVbarPolicy(ScrollBarPolicy.NEVER);
    setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

    formContainer = new VBox(10);

    titleField = new TextField();
    titleContainer = createFormField("Title", titleField);

    authorField = new TextField();
    authorContainer = createFormField("Author", authorField);

    descriptionField = new TextArea();
    descriptionField.setWrapText(true);
    descriptionField.setPrefHeight(250);
    descriptionContainer = createFormField("Description", descriptionField);

    publisherField = new TextField();
    publisherContainer = createFormField("Publisher", publisherField);

    identifierField = new TextField();
    identifierContainer = createFormField("Identifier", identifierField);

    coverImage =
        new Image(getClass().getResourceAsStream("/com/thangqt/libman/images/no_cover.png"));
    coverImagePreview = new ImageView(coverImage);
    coverImagePreview.setFitHeight(180);
    coverImagePreview.setPreserveRatio(true);
    coverImagePreviewContainer = new VBox(coverImagePreview);
    coverImagePreviewContainer.setAlignment(Pos.CENTER);

    coverImageField = new TextField();
    coverImageField.textProperty().addListener((obs, oldVal, newVal) -> {
      ImageLoader.loadImageAsync(newVal, coverImagePreview);
    });
    coverImageContainer = createFormField("Cover image URL", coverImageField);

    totalCopiesField = new TextField("0");
    totalCopiesContainer = createFormField("Total copies", totalCopiesField);

    availableCopiesField = new TextField("0");
    availableCopiesContainer = createFormField("Available copies", availableCopiesField);

    saveBtn = new Button("Save");

    formContainer
        .getChildren()
        .addAll(
            identifierContainer,
            titleContainer,
            authorContainer,
            descriptionContainer,
            publisherContainer,
            coverImagePreviewContainer,
            coverImageContainer,
            totalCopiesContainer,
            availableCopiesContainer,
            saveBtn);

    setContent(formContainer);
  }

  private <T extends Control> VBox createFormField(String labelText, T field) {
    VBox container = new VBox(5);
    Label label = new Label(labelText);
    container.getChildren().addAll(label, field);
    return container;
  }

  // Getters for the field containers
  public VBox getTitleContainer() {
    return titleContainer;
  }

  public VBox getAuthorContainer() {
    return authorContainer;
  }

  public VBox getDescriptionContainer() {
    return descriptionContainer;
  }

  public VBox getPublisherContainer() {
    return publisherContainer;
  }

  public VBox getIdentifierContainer() {
    return identifierContainer;
  }

  public VBox getCoverImageContainer() {
    return coverImageContainer;
  }

  public VBox getTotalCopiesContainer() {
    return totalCopiesContainer;
  }

  public VBox getAvailableCopiesContainer() {
    return availableCopiesContainer;
  }

  public VBox getCoverImagePreviewContainer() {
    return coverImagePreviewContainer;
  }

  // Getters for the actual input fields
  public TextField getTitleField() {
    return titleField;
  }

  public TextField getAuthorField() {
    return authorField;
  }

  public TextArea getDescriptionField() {
    return descriptionField;
  }

  public TextField getPublisherField() {
    return publisherField;
  }

  public TextField getIdentifierField() {
    return identifierField;
  }

  public ImageView getCoverImagePreview() {
    return coverImagePreview;
  }

  public TextField getCoverImageField() {
    return coverImageField;
  }

  public TextField getTotalCopiesField() {
    return totalCopiesField;
  }

  public TextField getAvailableCopiesField() {
    return availableCopiesField;
  }

  public Button getSaveBtn() {
    return saveBtn;
  }
}
