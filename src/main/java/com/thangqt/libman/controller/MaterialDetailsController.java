package com.thangqt.libman.controller;

import atlantafx.base.controls.ModalPane;
import atlantafx.base.layout.ModalBox;
import com.thangqt.libman.model.Loan;
import com.thangqt.libman.model.Material;
import com.thangqt.libman.service.LoanManager;
import com.thangqt.libman.service.MaterialManager;
import com.thangqt.libman.service.UserManager;
import com.thangqt.libman.view.GraphicalView.MaterialDetailsView;
import com.thangqt.libman.view.GraphicalView.MaterialEditView;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class MaterialDetailsController {
  private Material material;
  private LoanManager loanManager;
  private UserManager userManager;
  private MaterialManager materialManager;
  private MaterialViewController controller;

  public MaterialDetailsController(
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
  }

  public MaterialDetailsView createMaterialDetailsView() {
    Button issueBtn = createActionButton("Issue", Feather.ARCHIVE, e -> showIssueDialog());
    Button editBtn = createActionButton("Edit", Feather.EDIT, e -> showEditView(controller.getModalPane()));
    Button deleteBtn = createActionButton("Delete", Feather.TRASH_2, e -> showConfirmDeleteDialog());

    return new MaterialDetailsView(material, issueBtn, editBtn, deleteBtn);
  }

  private Button createActionButton(
          String text,
          Feather iconType,
          javafx.event.EventHandler<javafx.event.ActionEvent> eventHandler) {
    Button button = new Button(text, new FontIcon(iconType));
    button.setOnAction(eventHandler);
    return button;
  }

  public void showEditView(ModalPane modalPane) {
    ModalBox modalBox = new ModalBox(modalPane);
    modalBox.addContent(new MaterialEditView(material, materialManager, controller));
    modalBox.setMaxSize(420, 650);
    modalPane.show(modalBox);
  }

  public void showIssueDialog() {
    Dialog<Loan> dialog = createIssueDialog();
    dialog.showAndWait().ifPresent(this::issueLoan);
  }

  private Dialog<Loan> createIssueDialog() {
    Dialog<Loan> dialog = new Dialog<>();
    dialog.setTitle("Issue material");
    dialog.setHeaderText("Issue " + material.getTitle() + " to user");

    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    VBox form = createIssueForm();
    dialog.getDialogPane().setContent(form);

    dialog.setResultConverter(
        buttonType -> {
          if (buttonType == ButtonType.OK) {
            return createLoanFromForm(form);
          }
          return null;
        });

    return dialog;
  }

  private VBox createIssueForm() {
    Label userIdLabel = new Label("User ID");
    Label loanPeriodLabel = new Label("Loan period");

    TextField userId = new TextField();
    userId.setId("userIdField");
    TextField loanPeriod = new TextField("14");
    loanPeriod.setId("loanPeriodField");

    VBox form = new VBox(userIdLabel, userId, loanPeriodLabel, loanPeriod);
    form.setSpacing(10);
    return form;
  }

  private Loan createLoanFromForm(VBox form) {
    TextField userIdField = (TextField) form.lookup("#userIdField");
    TextField loanPeriodField = (TextField) form.lookup("#loanPeriodField");

    if (!validateLoan(userIdField.getText(), loanPeriodField.getText())) {
      return null;
    }

    int userId = Integer.parseInt(userIdField.getText());
    int loanDays = Integer.parseInt(loanPeriodField.getText());

    return new Loan(userId, material.getId(), LocalDate.now(), LocalDate.now().plusDays(loanDays));
  }

  private boolean validateLoan(String userId, String loanPeriod) {
    try {
      int id = Integer.parseInt(userId);
      int period = Integer.parseInt(loanPeriod);
      if (id <= 0 || period <= 0) {
        showErrorAlert("Invalid input", "User ID and loan period must be positive integers");
        return false;
      }
    } catch (NumberFormatException e) {
      showErrorAlert("Invalid input", "User ID and loan period must be positive integers");
      return false;
    }

    try {
      if (userManager.getUserById(Integer.parseInt(userId)) == null) {
        showErrorAlert("Invalid user ID", "User with ID " + userId + " does not exist");
        return false;
      }
    } catch (SQLException e) {
      showErrorAlert("Failed to validate user ID", "An error occurred while validating user ID");
    }
    return true;
  }

  private void issueLoan(Loan loan) {
    try {
      loanManager.addLoan(loan);
      showAlert(
          Alert.AlertType.INFORMATION,
          "Issue successful",
          "Material issued successfully",
          "Material "
              + material.getTitle()
              + " has been issued to user "
              + userManager.getUserById(loan.getUserId()).getName());
    } catch (SQLException e) {
      showErrorAlert("Failed to issue material", "An error occurred while issuing the material.");
    }
  }

  public void showConfirmDeleteDialog() {
    Alert alert =
        createConfirmationAlert(
            "Delete material",
            "Are you sure you want to delete " + material.getTitle() + "?",
            "This action cannot be undone");

    alert
        .showAndWait()
        .ifPresent(
            buttonType -> {
              System.out.println(buttonType);
              if (buttonType.getButtonData() == ButtonBar.ButtonData.YES) {
                deleteMaterial();
              }
            });
  }

  private void deleteMaterial() {
    try {
      if (!loanManager.getLoansByMaterial(material.getId()).isEmpty()) {
        showErrorAlert(
            "Cannot delete material",
            "Material " + material.getTitle() + " is currently issued and cannot be deleted");
        return;
      }
      materialManager.deleteMaterial(material.getId());
      showAlert(
          Alert.AlertType.INFORMATION,
          "Delete successful",
          "Material deleted successfully",
          "Material " + material.getTitle() + " has been deleted");
      controller.refreshMaterialsListing();
      controller.getModalPane().hide();
    } catch (SQLException e) {
      showErrorAlert("Failed to delete material", "An error occurred while deleting the material.");
    }
  }

  private Alert createConfirmationAlert(String title, String header, String content) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);

    ButtonType yesBtn = new ButtonType("Yes", ButtonBar.ButtonData.YES);
    ButtonType noBtn = new ButtonType("No", ButtonBar.ButtonData.NO);
    alert.getButtonTypes().setAll(yesBtn, noBtn);

    return alert;
  }

  private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);
    alert.showAndWait();
  }

  private void showErrorAlert(String title, String message) {
    showAlert(Alert.AlertType.ERROR, title, title, message);
  }

  public ModalPane getModalPane() {
    return controller.getModalPane();
  }
}
