package com.thangqt.libman.controller;

import com.thangqt.libman.model.Loan;
import com.thangqt.libman.model.Material;
import com.thangqt.libman.service.LoanManager;
import com.thangqt.libman.service.MaterialManager;
import com.thangqt.libman.service.SessionManager;
import com.thangqt.libman.service.UserManager;
import com.thangqt.libman.view.GraphicalView.MaterialDetailsView;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;

public class UserMaterialDetailsController {
  private Material material;
  private LoanManager loanManager;
  private UserManager userManager;
  private MaterialManager materialManager;
  private UserHomeController controller;

  public UserMaterialDetailsController(
      Material material,
      LoanManager loanManager,
      UserManager userManager,
      MaterialManager materialManager,
      UserHomeController controller) {
    this.material = material;
    this.loanManager = loanManager;
    this.userManager = userManager;
    this.materialManager = materialManager;
    this.controller = controller;
  }

  public MaterialDetailsView createMaterialDetailsView() {
    try {
      if (loanManager.isDocumentIssued(SessionManager.getCurrentUser().getId(), material.getId())) {
        Button returnBtn = createActionButton("Return", Feather.CHECK, e -> showReturnDialog());
        return new MaterialDetailsView(material, returnBtn);
      } else {
        Button borrowBtn = createActionButton("Borrow", Feather.ARCHIVE, e -> showBorrowDialog());
        return new MaterialDetailsView(material, borrowBtn);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new MaterialDetailsView(material);
  }

  private void showReturnDialog() {
    makeDialog(
            Alert.AlertType.CONFIRMATION,
            "Return Document",
            "Are you sure you want to return the document?",
            "You are about to return the document. Do you want to proceed?")
        .showAndWait()
        .ifPresent(this::returnDocument);
  }

  private void returnDocument(ButtonType buttonType) {
    if (buttonType == ButtonType.OK) {
      try {
        loanManager.returnLoan(SessionManager.getCurrentUser().getId(), material.getId());
        makeDialog(
                Alert.AlertType.INFORMATION,
                "Success",
                "Document returned successfully",
                "You have successfully returned the document.")
            .show();
      } catch (Exception e) {
        makeDialog(
                Alert.AlertType.ERROR,
                "Error",
                "Failed to return document",
                "An error occurred while trying to return the document.")
            .show();
      }
      controller.getInnerModalPane().hide();
    }
  }

  private void showBorrowDialog() {
    ChoiceDialog<String> dialog = new ChoiceDialog<>("7 days", "7 days", "14 days", "21 days");
    dialog.setTitle("Borrow Document");
    dialog.setHeaderText("Select the duration for borrowing the document");
    dialog.setContentText("Duration:");
    dialog.showAndWait().ifPresent(this::borrowDocument);
  }

  private void borrowDocument(String duration) {
    try {
      Loan loan =
          new Loan(
              SessionManager.getCurrentUser().getId(),
              material.getId(),
              LocalDate.now(),
              LocalDate.now().plusDays(Integer.parseInt(duration.split(" ")[0])));
      loanManager.addLoan(loan);
      makeDialog(
              Alert.AlertType.INFORMATION,
              "Success",
              "Document borrowed successfully",
              "You have successfully borrowed the document.")
          .show();
    } catch (Exception e) {
      makeDialog(
              Alert.AlertType.ERROR,
              "Error",
              "Failed to borrow document",
              "An error occurred while trying to borrow the document.")
          .show();
    }
    controller.getInnerModalPane().hide();
  }

  private Alert makeDialog(Alert.AlertType type, String title, String header, String content) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);
    return alert;
  }

  private Button createActionButton(
      String text,
      Feather iconType,
      javafx.event.EventHandler<javafx.event.ActionEvent> eventHandler) {
    Button button = new Button(text, new FontIcon(iconType));
    button.setOnAction(eventHandler);
    return button;
  }
}
