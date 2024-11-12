package com.thangqt.libman.view.GraphicalView;

import atlantafx.base.theme.Styles;
import com.thangqt.libman.model.Material;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class MaterialDetailsView extends VBox {
  private final Material material;

  public MaterialDetailsView(Material material, Button... actionButtons) {
    this.material = material;
    initialize(actionButtons);
  }

  private void initialize(Button... actionButtons) {
    setPadding(new Insets(15));
    setSpacing(10);

    HBox topContainer = createTopContainer();
    HBox actionContainer = createActionContainer(actionButtons);

    getChildren().addAll(topContainer, actionContainer);
  }

  private HBox createTopContainer() {
    HBox topContainer = new HBox();

    ImageView coverImage = createCoverImage();
    VBox infoContainer = createInfoContainer();

    topContainer.getChildren().addAll(infoContainer, coverImage);
    return topContainer;
  }

  private ImageView createCoverImage() {
    ImageView coverImage = new ImageView();
    coverImage.setFitWidth(200);
    coverImage.setPreserveRatio(true);
    if (material.getCoverImageUrl() == null || material.getCoverImageUrl().isEmpty()) {
      coverImage.setImage(new Image(getClass().getResourceAsStream("/com/thangqt/libman/images/no_cover.png")));
    } else {
      coverImage.setImage(new Image(material.getCoverImageUrl()));
    }
    HBox.setMargin(coverImage, new Insets(0, 30, 0, 10));
    return coverImage;
  }

  private VBox createInfoContainer() {
    VBox infoContainer = new VBox();

    Text title = createTitleText();
    Text author = createAuthorText();
    TextFlow descriptionFlow = createDescriptionFlow();

    infoContainer.getChildren().addAll(title, author, descriptionFlow);
    return infoContainer;
  }

  private Text createTitleText() {
    Text title = new Text(material.getTitle());
    title.getStyleClass().add(Styles.TITLE_2);
    title.setStyle("-fx-font-size: 36px; -fx-font-weight: 500;");
    return title;
  }

  private Text createAuthorText() {
    Text author = new Text(material.getAuthor());
    author.getStyleClass().add(Styles.TEXT_SUBTLE);
    author.setStyle("-fx-font-size: 18px;");
    return author;
  }

  private TextFlow createDescriptionFlow() {
    TextFlow descriptionFlow = new TextFlow();
    descriptionFlow.setMaxWidth(540);
    VBox.setMargin(descriptionFlow, new Insets(10, 0, 0, 0));

    Text description = new Text(material.getDescription());
    description.getStyleClass().add(Styles.TEXT_MUTED);
    description.setWrappingWidth(540);
    descriptionFlow.getChildren().add(description);

    return descriptionFlow;
  }

  private HBox createActionContainer(Button... actionButtons) {
    HBox actionContainer = new HBox();
    actionContainer.setSpacing(10);
    actionContainer.getChildren().addAll(actionButtons);
    return actionContainer;
  }
}