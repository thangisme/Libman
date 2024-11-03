package com.thangqt.libman.view.GraphicalView;

import atlantafx.base.controls.ModalPane;
import atlantafx.base.layout.ModalBox;
import atlantafx.base.theme.Styles;
import com.thangqt.libman.controller.MaterialViewController;
import com.thangqt.libman.model.Loan;
import com.thangqt.libman.model.Material;
import com.thangqt.libman.service.LoanManager;
import com.thangqt.libman.service.MaterialManager;
import com.thangqt.libman.service.UserManager;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class MaterialDetailsView extends VBox {
  private Material material;
  private LoanManager loanManager;
  private UserManager userManager;
  private MaterialManager materialManager;
  private MaterialViewController controller;
  private ModalPane modalPane;

  public MaterialDetailsView(
      Material material,
      LoanManager loanManager,
      UserManager userManager,
      MaterialManager materialManager,
      MaterialViewController controller) {
    this.material = material;
    this.loanManager = loanManager;
    this.userManager = userManager;
    this.materialManager = materialManager;
    this.controller = controller;
    modalPane = controller.getModalPane();
    initialize();
  }

  private void initialize() {
    setPadding(new Insets(15));
    setSpacing(10);
    HBox topContainer = new HBox();

    ImageView coverImage = new ImageView();
    coverImage.setFitWidth(200);
    HBox.setMargin(coverImage, new Insets(0, 30, 0, 10));
    coverImage.preserveRatioProperty().set(true);
    if (material.getCoverImageUrl() != null) {
      coverImage.setImage(new Image(material.getCoverImageUrl()));
    } else {
      coverImage.setImage(
          new Image(getClass().getResourceAsStream("/com/thangqt/libman/images/no_cover.png")));
    }

    VBox infoContainer = new VBox();

    Text title = new Text(material.getTitle());
    title.getStyleClass().add(Styles.TITLE_2);
    title.setStyle("-fx-font-size: 36px; -fx-font-weight: 500;");

    Text author = new Text(material.getAuthor());
    author.getStyleClass().addAll(Styles.TEXT_SUBTLE);
    author.setStyle("-fx-font-size: 18px;");

    TextFlow descriptionScrollPane = new TextFlow();
    descriptionScrollPane.setMaxWidth(540);
    VBox.setMargin(descriptionScrollPane, new Insets(10, 0, 0, 0));
    Text description = new Text(material.getDescription());
    description.getStyleClass().add(Styles.TEXT_MUTED);
    description.setWrappingWidth(540);
    descriptionScrollPane.getChildren().add(description);

    infoContainer.getChildren().addAll(title, author, descriptionScrollPane);
    topContainer.getChildren().addAll(infoContainer, coverImage);

    HBox actionContainer = new HBox();
    actionContainer.setSpacing(10);
    Button issueBtn = new Button("Issue", new FontIcon(Feather.ARCHIVE));
    issueBtn.setOnAction(e -> showIssueDialog());
    Button editBtn = new Button("Edit", new FontIcon(Feather.EDIT));
    editBtn.setOnAction(e -> showEditView());
    Button deleteBtn = new Button("Delete", new FontIcon(Feather.TRASH_2));
    deleteBtn.setOnAction(e -> showConfirmDeleteDialog(material));
    actionContainer.getChildren().addAll(issueBtn, editBtn, deleteBtn);

    getChildren().addAll(topContainer, actionContainer);
  }

  private void showEditView() {
    ModalBox modalBox = new ModalBox(modalPane);
    modalBox.addContent(new MaterialEditView(material, materialManager, controller));
    modalBox.setMaxSize(420, 650);
    modalPane.show(modalBox);
  }

  private void showIssueDialog() {
    Dialog<Loan> dialog = new Dialog<>();
    dialog.setTitle("Issue material");
    dialog.setHeaderText("Issue" + material.getTitle() + " to user");
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    Label userIdLabel = new Label("User ID");
    Label loanPeriodLabel = new Label("Loan period");
    TextField userId = new TextField();
    TextField loanPeriod = new TextField("14");
    VBox form = new VBox(userIdLabel, userId, loanPeriodLabel, loanPeriod);
    form.setSpacing(10);
    dialog.getDialogPane().setContent(form);
    dialog.setResultConverter(
        buttonType -> {
          if (buttonType == ButtonType.OK) {
            return new Loan(
                Integer.parseInt(userId.getText()),
                material.getId(),
                LocalDate.now(),
                LocalDate.now().plusDays(Integer.parseInt(loanPeriod.getText())));
          }
          return null;
        });
    dialog.showAndWait().ifPresent(newLoan -> issueLoan(newLoan));
  }

  private void issueLoan(Loan loan) {
    try {
      loanManager.addLoan(loan);
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Issue successful");
      alert.setHeaderText("Material issued successfully");
      alert.setContentText(
          "Material "
              + material.getTitle()
              + " has been issued to user "
              + userManager.getUserById(loan.getUserId()).getName());
      alert.showAndWait();
    } catch (SQLException e) {
      e.printStackTrace();
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Issue failed");
      alert.setHeaderText("Failed to issue material");
      alert.setContentText("An error occurred while issuing the material");
      alert.showAndWait();
    }
  }

  private void showConfirmDeleteDialog(Material material) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Delete material");
    alert.setHeaderText("Are you sure you want to delete " + material.getTitle() + "?");
    alert.setContentText("This action cannot be undone");
    ButtonType yesBtn = new ButtonType("Yes", ButtonBar.ButtonData.YES);
    ButtonType noBtn = new ButtonType("No", ButtonBar.ButtonData.NO);
    alert.getButtonTypes().setAll(yesBtn, noBtn);
    alert
        .showAndWait()
        .ifPresent(
            buttonType -> {
              if (buttonType == yesBtn) {
                try {
                  materialManager.deleteMaterial(material.getId());
                  Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                  successAlert.setTitle("Delete successful");
                  successAlert.setHeaderText("Material deleted successfully");
                  successAlert.setContentText(
                      "Material " + material.getTitle() + " has been deleted");
                  successAlert.showAndWait();
                  controller.refreshMaterialsListing();
                  modalPane.hide();
                } catch (SQLException e) {
                  e.printStackTrace();
                  Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                  errorAlert.setTitle("Delete failed");
                  errorAlert.setHeaderText("Failed to delete material");
                  errorAlert.setContentText("An error occurred while deleting the material");
                  errorAlert.showAndWait();
                }
              }
            });
  }
}
