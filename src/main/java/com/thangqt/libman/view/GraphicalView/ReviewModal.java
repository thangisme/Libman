package com.thangqt.libman.view.GraphicalView;

import atlantafx.base.theme.Styles;
import com.thangqt.libman.model.Review;
import com.thangqt.libman.service.ReviewManager;
import com.thangqt.libman.service.ServiceFactory;
import com.thangqt.libman.service.SessionManager;
import com.thangqt.libman.service.UserManager;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.fluentui.FluentUiFilledMZ;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.util.List;

public class ReviewModal extends VBox {
  private ReviewManager reviewManager;
  private UserManager userManager;
  private int materialId;
  private List<Review> reviews;
  private VBox reviewForm;
  private Button addReviewButton;

  public ReviewModal(int materialId) {
    try {
      ServiceFactory serviceFactory = ServiceFactory.getInstance();
      this.reviewManager = serviceFactory.getReviewManager();
      this.userManager = serviceFactory.getUserManager();
      this.materialId = materialId;
      this.reviews = reviewManager.getReviewsByMaterialId(materialId);
      initialize();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void initialize() {
    setFillWidth(true);
    setSpacing(10);
    setPadding(new Insets(15, 30, 15, 30));

    createHeader();
    createBody();
  }

  public void createHeader() {
    HBox header = new HBox();
    Label title = new Label("Reviews");
    title.getStyleClass().add(Styles.TITLE_3);
    header.getChildren().add(title);
    getChildren().add(header);
  }

  public void createBody() {
    VBox body = new VBox();
    ScrollPane reviewScrollPane = new ScrollPane();
    reviewScrollPane.setFitToWidth(true);
    reviewScrollPane.setMaxHeight(640);
    reviewScrollPane.setPadding(new Insets(10, 0, 0, 0));
    reviewScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    VBox reviewList = new VBox();
    reviewScrollPane.setContent(reviewList);
    Review currentReview =
        reviewManager.getReview(SessionManager.getCurrentUser().getId(), materialId);
    if (currentReview == null) {
      makeReviewForm();
      addReviewButton = new Button("Add review");
      addReviewButton.setOnAction(e -> toggleReviewForm());
      body.getChildren().add(reviewForm);
      body.getChildren().add(addReviewButton);
    } else {
      VBox reviewContainer = makeIndividualReviewContainer(currentReview);
      reviewList.getChildren().add(reviewContainer);
    }
    if (reviews.size() == 0) {
      Label noReviews = new Label("No reviews yet.");
      noReviews.setPadding(new Insets(10, 0, 0, 0));
      body.getChildren().add(noReviews);
    } else {
      for (Review review : reviews) {
        if (review.getUserId() == SessionManager.getCurrentUser().getId()) continue;
        reviewList.getChildren().add(makeIndividualReviewContainer(review));
      }
    }
    body.getChildren().add(reviewScrollPane);
    getChildren().add(body);
  }

  public void makeReviewForm() {
    reviewForm = new VBox();
    reviewForm.setVisible(false);
    reviewForm.setManaged(false);
    reviewForm.setSpacing(10);
    Label title = new Label("Rating");
    HBox ratingContainer = makeRatingableContainer();
    Label content = new Label("Content");
    TextArea contentField = new TextArea();
    contentField.setPromptText("Write your review here");
    contentField.setWrapText(true);
    Button submitButton = new Button("Submit");
    submitButton.getStyleClass().add(Styles.ACCENT);
    submitButton.setOnAction(
        e -> {
          int rating = getRatingCount(ratingContainer);
          addReview(rating, contentField.getText());
        });
    Button cancelButton = new Button("Cancel");
    cancelButton.setOnAction(e -> toggleReviewForm());
    HBox buttonContainer = new HBox(submitButton, cancelButton);
    buttonContainer.setSpacing(10);
    reviewForm.getChildren().addAll(title, ratingContainer, content, contentField, buttonContainer);
  }

  private VBox makeIndividualReviewContainer(Review review) {
    VBox individualReviewContainer = new VBox();
    individualReviewContainer.setPadding(new Insets(0, 0, 10, 0));
    try {
      UserManager userManager = ServiceFactory.getInstance().getUserManager();
      Label name = new Label(userManager.getUserById(review.getUserId()).getName());
      name.getStyleClass().addAll(Styles.TITLE_4);
      HBox ratingContainer = makeRatingContainer(review.getRating());
      Label content = new Label(review.getContent().strip());
      content.getStyleClass().addAll(Styles.TEXT_MUTED);
      content.setWrapText(true);
      content.setMaxWidth(700);
      individualReviewContainer.getChildren().addAll(name, ratingContainer, content);
      boolean isEditable =
          review.getUserId() == SessionManager.getCurrentUser().getId()
              || SessionManager.getCurrentUser().isAdmin();
      if (isEditable) {
        HBox actionContainer = makeEditActions(review);
        individualReviewContainer.getChildren().add(actionContainer);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return individualReviewContainer;
  }

  private HBox makeEditActions(Review review) {
    Label edit = new Label("Edit");
    edit.setUnderline(true);
    edit.getStyleClass().addAll(Styles.TEXT_SUBTLE, Styles.TEXT_SMALL);
    edit.setOnMouseClicked(e -> showEditReviewDialog(review));
    Label delete = new Label("Delete");
    delete.setUnderline(true);
    delete.getStyleClass().addAll(Styles.TEXT_SUBTLE, Styles.TEXT_SMALL);
    delete.setOnMouseClicked(e -> showDeleteReviewConfirmation(review));
    HBox actionContainer = new HBox(edit, delete);
    actionContainer.setSpacing(10);
    return actionContainer;
  }

  private void showEditReviewDialog(Review review) {
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Edit Review");
    VBox reviewForm = new VBox();
    reviewForm.setPadding(new Insets(15));
    Label title = new Label("Rating");
    HBox ratingContainer = makeRatingableContainer();
    for (int i = 0; i < review.getRating(); i++) {
      ratingContainer.getChildren().get(i).getStyleClass().remove(Styles.TEXT_MUTED);
      ratingContainer.getChildren().get(i).getStyleClass().add(Styles.WARNING);
    }
    Label content = new Label("Content");
    TextArea contentField = new TextArea();
    contentField.setText(review.getContent());
    contentField.setWrapText(true);
    reviewForm.getChildren().addAll(title, ratingContainer, content, contentField);
    dialog.getDialogPane().setContent(reviewForm);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    dialog
        .showAndWait()
        .ifPresent(
            result -> {
              if (result == ButtonType.OK) {
                int rating = getRatingCount(ratingContainer);
                if (reviewManager.updateReview(review.getId(), rating, contentField.getText())) {
                  showDialog(
                      Alert.AlertType.INFORMATION,
                      "Success",
                      "Review updated successfully",
                      "The review has been updated.");
                  reviews = reviewManager.getReviewsByMaterialId(materialId);
                  getChildren().remove(1);
                  createBody();
                } else {
                  showDialog(
                      Alert.AlertType.ERROR,
                      "Error",
                      "Failed to update review",
                      "An error occurred while trying to update the review.");
                }
              }
            });
  }

  private void showDeleteReviewConfirmation(Review review) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Delete Review");
    alert.setHeaderText("Are you sure you want to delete this review?");
    alert.setContentText("This action cannot be undone.");
    alert
        .showAndWait()
        .ifPresent(
            result -> {
              if (result == ButtonType.OK) {
                if (reviewManager.deleteReview(review.getId())) {
                  showDialog(
                      Alert.AlertType.INFORMATION,
                      "Success",
                      "Review deleted successfully",
                      "The review has been deleted.");
                  reviews = reviewManager.getReviewsByMaterialId(materialId);
                  getChildren().remove(1);
                  createBody();
                } else {
                  showDialog(
                      Alert.AlertType.ERROR,
                      "Error",
                      "Failed to delete review",
                      "An error occurred while trying to delete the review.");
                }
              }
            });
  }

  private HBox makeRatingableContainer() {
    HBox ratingableContainer = new HBox();
    for (int i = 0; i < 5; i++) {
      FontIcon star = new FontIcon(FluentUiFilledMZ.STAR_28);
      star.getStyleClass().add("rating-star-big");
      star.getStyleClass().add(Styles.TEXT_MUTED);
      int index = i;
      star.setOnMouseClicked(
          e -> {
            for (int j = 0; j < 5; j++) {
              ObservableList<String> starClass =
                  ratingableContainer.getChildren().get(j).getStyleClass();
              if (j <= index) {
                if (starClass.contains(Styles.TEXT_MUTED)) starClass.remove(Styles.TEXT_MUTED);
                if (!starClass.contains(Styles.WARNING)) starClass.add(Styles.WARNING);
              } else {
                if (starClass.contains(Styles.WARNING)) starClass.remove(Styles.WARNING);
                if (!starClass.contains(Styles.TEXT_MUTED)) starClass.add(Styles.TEXT_MUTED);
              }
            }
          });
      ratingableContainer.getChildren().add(star);
    }
    return ratingableContainer;
  }

  private HBox makeRatingContainer(int rating) {
    HBox ratingContainer = new HBox();

    for (int i = 0; i < 5; i++) {
      FontIcon star = new FontIcon(FluentUiFilledMZ.STAR_16);
      star.getStyleClass().add("rating-star-medium");
      if (i < rating) {
        star.getStyleClass().add(Styles.WARNING);
      } else {
        star.getStyleClass().add(Styles.TEXT_MUTED);
      }
      ratingContainer.getChildren().add(star);
    }
    return ratingContainer;
  }

  private void toggleReviewForm() {
    reviewForm.setVisible(!reviewForm.isVisible());
    reviewForm.setManaged(!reviewForm.isManaged());
    addReviewButton.setVisible(!addReviewButton.isVisible());
  }

  private void addReview(int rating, String content) {
    Review review =
        new Review(
            SessionManager.getCurrentUser().getId(), materialId, rating, content, LocalDate.now());
    if (reviewManager.addReview(review)) {
      reviews.add(review);
      getChildren().remove(1);
      createBody();
    }
  }

  private void showDialog(Alert.AlertType type, String title, String header, String content) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);
    alert.showAndWait();
  }

  private int getRatingCount(HBox ratingContainer) {
    int rating = 0;
    for (int i = 4; i >= 0; i--) {
      if (ratingContainer.getChildren().get(i).getStyleClass().contains(Styles.WARNING)) {
        rating = i + 1;
        break;
      }
    }
    return rating;
  }
}
