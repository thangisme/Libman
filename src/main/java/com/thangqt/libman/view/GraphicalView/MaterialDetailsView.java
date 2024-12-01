package com.thangqt.libman.view.GraphicalView;

import atlantafx.base.controls.ModalPane;
import atlantafx.base.layout.ModalBox;
import atlantafx.base.theme.Styles;
import com.thangqt.libman.model.Material;
import com.thangqt.libman.model.Review;
import com.thangqt.libman.service.ReviewManager;
import com.thangqt.libman.service.ServiceFactory;
import com.thangqt.libman.service.UserManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.fluentui.*;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;
import java.util.List;

public class MaterialDetailsView extends HBox {
  private final Material material;
  private final ModalPane modalPane;
  private List<Review> reviews;

  public MaterialDetailsView(Material material, ModalPane modalPane, Button... actionButtons) {
    this.material = material;
    this.modalPane = modalPane;
    initialize(actionButtons);
  }

  private void initialize(Button... actionButtons) {
    setPadding(new Insets(15));
    setSpacing(10);

    try {
      ReviewManager reviewManager = ServiceFactory.getInstance().getReviewManager();
      reviews = reviewManager.getReviewsByMaterialId(material.getId());
    } catch (SQLException e) {
      e.printStackTrace();
    }

    VBox topContainer = createMainContainer(actionButtons);
    VBox sideContainer = createSideContainer();

    getChildren().addAll(topContainer, sideContainer);
  }

  private VBox createMainContainer(Button... actionButtons) {
    VBox topContainer = new VBox();
    topContainer.setSpacing(15);

    VBox infoContainer = createInfoContainer();
    VBox space = new VBox();
    VBox.setVgrow(space, Priority.ALWAYS);
    HBox actionContainer = createActionContainer(actionButtons);

    topContainer.getChildren().addAll(infoContainer, space, actionContainer);
    return topContainer;
  }

  private ImageView createCoverImage() {
    ImageView coverImage = new ImageView();
    coverImage.setFitWidth(200);
    coverImage.setPreserveRatio(true);
    if (material.getCoverImageUrl() == null || material.getCoverImageUrl().isEmpty()) {
      coverImage.setImage(
          new Image(getClass().getResourceAsStream("/com/thangqt/libman/images/no_cover.png")));
    } else {
      coverImage.setImage(new Image(material.getCoverImageUrl()));
    }
    HBox.setMargin(coverImage, new Insets(0, 30, 0, 10));
    return coverImage;
  }

  private VBox createReviewContainer() {
    VBox reviewContainer = new VBox();
    reviewContainer.setSpacing(5);

    Text title = new Text("Reviews");
    HBox space = new HBox();
    HBox.setHgrow(space, Priority.ALWAYS);
    Button addReviewBtn = new Button("Add Review", new FontIcon(Feather.PEN_TOOL));
    addReviewBtn.getStyleClass().addAll(Styles.FLAT, Styles.BUTTON_ICON);
    addReviewBtn.setOnAction(e -> showReviewModal());
    HBox header = new HBox(title, space, addReviewBtn);
    header.setAlignment(Pos.CENTER);

    reviewContainer.getChildren().add(header);

    if (reviews == null || reviews.isEmpty()) {
      Text noReviews = new Text("No reviews yet");
      noReviews.setTextAlignment(TextAlignment.CENTER);
      noReviews.getStyleClass().addAll(Styles.TEXT_SMALL, Styles.TEXT_MUTED);
      reviewContainer.getChildren().add(noReviews);

    } else {
      Review review = reviews.get(0);
      if (review != null) {
        VBox individualReviewContainer = createIndividualReviewContainer(review);
        reviewContainer.getChildren().add(individualReviewContainer);
      }
      Label viewAllReviews = new Label("Read more ...");
      viewAllReviews.setUnderline(true);
      viewAllReviews.setOnMouseClicked(e -> showReviewModal());
      reviewContainer.getChildren().add(viewAllReviews);
    }

    return reviewContainer;
  }

  private HBox createAverageRatingContainer() {
    try {
      ReviewManager reviewManager = ServiceFactory.getInstance().getReviewManager();
      int averageRating = (int) reviewManager.getAverageRating(material.getId());
      HBox ratingContainer = createRatingContainer(averageRating);
      HBox averageRatingContainer = new HBox(new Label("Rating: "), ratingContainer);
      averageRatingContainer.setAlignment(Pos.CENTER);
      return averageRatingContainer;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  private VBox createIndividualReviewContainer(Review review) {
    VBox individualReviewContainer = new VBox();

    try {
      UserManager userManager = ServiceFactory.getInstance().getUserManager();
      Text name = new Text(userManager.getUserById(review.getUserId()).getName());
      name.getStyleClass().add(Styles.TEXT_MUTED);
      HBox ratingContainer = createRatingContainer(review.getRating());
      HBox.setMargin(ratingContainer, new Insets(0, 0, 10, 0));
      Text content =
          new Text(
              review.getContent().strip().length() > 160
                  ? review.getContent().strip().substring(0, 160) + "..."
                  : review.getContent().strip());
      content.getStyleClass().addAll(Styles.TEXT_MUTED, Styles.TEXT_SMALL);
      content.setWrappingWidth(200);

      individualReviewContainer.getChildren().addAll(name, ratingContainer, content);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return individualReviewContainer;
  }

  private HBox createRatingContainer(int rating) {
    HBox ratingContainer = new HBox();

    for (int i = 0; i < 5; i++) {
      FontIcon star = new FontIcon(FluentUiFilledMZ.STAR_12);
      star.getStyleClass().add("rating-star");
      if (i < rating) {
        star.getStyleClass().add(Styles.WARNING);
      } else {
        star.getStyleClass().add(Styles.TEXT_MUTED);
      }
      ratingContainer.getChildren().add(star);
    }
    return ratingContainer;
  }

  private VBox createSideContainer() {
    VBox sideContainer = new VBox();
    sideContainer.setSpacing(10);

    sideContainer.getChildren().addAll(createCoverImage(), createReviewContainer());
    if (reviews.size() > 0) sideContainer.getChildren().add(1, createAverageRatingContainer());
    return sideContainer;
  }

  public void showReviewModal() {
    ModalBox modalBox = new ModalBox(modalPane);
    modalBox.setMaxSize(1080, 500);
    ReviewModal reviewModal = new ReviewModal(material.getId());
    modalBox.addContent(reviewModal);
    modalPane.show(modalBox);
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
