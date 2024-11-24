package com.thangqt.libman.view.GraphicalView;

import atlantafx.base.controls.ModalPane;
import atlantafx.base.layout.ModalBox;
import atlantafx.base.theme.Styles;
import com.thangqt.libman.controller.LoanViewController;
import com.thangqt.libman.model.Loan;
import com.thangqt.libman.model.Material;
import com.thangqt.libman.model.User;
import com.thangqt.libman.service.*;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ReturnLoanModalView {
  private final ModalPane modalPane;
  private final UserManager userManager;
  private final MaterialManager materialManager;
  private final LoanManager loanManager;
  private final LoanViewController loanViewController;
  private final ModalBox modalBox;

  private TextField borrowerIdField;
  private TextField materialIdField;
  private Label borrowerInfoLabel;
  private Label materialInfoLabel;
  private Label loanInfoLabel;
  private Button addLoanBtn;
  private ImageView cameraCapture;
  private CameraHelper cameraHelper;
  private QRHelper qrHelper;
  private volatile boolean isProcessingLoan;

  public ReturnLoanModalView(
      LoanViewController loanViewController,
      ModalPane modalPane,
      UserManager userManager,
      MaterialManager materialManager,
      LoanManager loanManager) {
    this.loanViewController = loanViewController;
    this.modalPane = modalPane;
    this.userManager = userManager;
    this.materialManager = materialManager;
    this.loanManager = loanManager;
    this.modalBox = new ModalBox(modalPane);
    this.cameraHelper = new CameraHelper(360, 360);
    this.qrHelper = new QRHelper();
    isProcessingLoan = false;
  }

  public void show() {
    try {
      VBox container = createMainContainer();
      HBox bodyContainer = createBodyContainer();
      setupCamera(bodyContainer);

      container.getChildren().addAll(createHeadline(), bodyContainer, addLoanBtn);
      modalBox.addContent(container);
      modalBox.setMaxSize(400, 300);
      modalPane.show(modalBox);
      modalBox.setOnClose(event -> cleanup());
    } catch (SQLException e) {
      showErrorAlert(
          "Failed to load materials and users",
          "An error occurred while loading materials and users");
    }
  }

  private VBox createMainContainer() {
    VBox container = new VBox();
    container.setPadding(new Insets(30));
    container.setAlignment(Pos.CENTER);
    container.setSpacing(20);
    return container;
  }

  private Label createHeadline() {
    Label headline = new Label("Return Loan");
    headline.getStyleClass().add(Styles.TITLE_3);
    return headline;
  }

  private HBox createBodyContainer() throws SQLException {
    VBox form = createForm();
    HBox bodyContainer = new HBox();
    bodyContainer.setSpacing(15);

    cameraCapture = new ImageView();
    cameraCapture.setFitWidth(360);
    cameraCapture.setPreserveRatio(true);
    cameraCapture.setSmooth(true);

    bodyContainer.getChildren().addAll(form, new Separator(Orientation.VERTICAL), cameraCapture);
    return bodyContainer;
  }

  private VBox createForm() throws SQLException {
    VBox form = new VBox();
    form.setSpacing(10);
    form.setMinWidth(360);
    form.setMaxWidth(360);

    createFormFields();
    setupFormValidation();
    setupFieldListeners();

    VBox infoContainer = createInfoContainer();
    form.getChildren()
        .addAll(
            new Label("Borrower"),
            borrowerIdField,
            new Label("Material"),
            materialIdField,
            infoContainer);

    return form;
  }

  private void createFormFields() {
    borrowerIdField = new TextField();
    borrowerIdField.setPromptText("Borrower ID");

    materialIdField = new TextField();
    materialIdField.setPromptText("Material ID");

    addLoanBtn = new Button("Return Loan");
    addLoanBtn.setDisable(true);
    addLoanBtn.setOnAction(event -> handleAddLoan());
  }

  private VBox createInfoContainer() {
    VBox infoContainer = new VBox();
    infoContainer.setSpacing(10);
    infoContainer.setPadding(new Insets(20, 0, 0, 0));

    borrowerInfoLabel = new Label();
    borrowerInfoLabel.setWrapText(true);
    borrowerInfoLabel.setMaxWidth(300);

    materialInfoLabel = new Label();
    materialInfoLabel.setWrapText(true);
    materialInfoLabel.setMaxWidth(300);

    loanInfoLabel = new Label();
    loanInfoLabel.getStyleClass().add(Styles.DANGER);
    loanInfoLabel.setWrapText(true);
    loanInfoLabel.setMaxWidth(300);

    infoContainer.getChildren().addAll(borrowerInfoLabel, materialInfoLabel, loanInfoLabel);
    return infoContainer;
  }

  private void setupCamera(HBox bodyContainer) {
    if (!cameraHelper.isCameraOpen()) {
      return;
    }

    Thread cameraThread = new Thread(this::processCameraFeed);
    cameraThread.setDaemon(true);
    cameraThread.start();
  }

  private void processCameraFeed() {
    while (!Thread.currentThread().isInterrupted()) {
      Image frame = cameraHelper.captureFrame();
      if (frame != null) {
        updateCameraFrame(frame);
        processQRCode(frame);
      }

      try {
        Thread.sleep(1000 / 30);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }

  private void updateCameraFrame(Image frame) {
    Platform.runLater(() -> cameraCapture.setImage(frame));
  }

  private void processQRCode(Image frame) {
    String qrCode = qrHelper.decodeQRCode(frame);
    if (qrCode == null || !qrCode.matches("\\d+") || isProcessingLoan) {
      return;
    }

    Platform.runLater(
        () -> {
          materialIdField.setText(qrCode);
          validateAndProcessQRCode(qrCode);
        });
  }

  private void validateAndProcessQRCode(String qrCode) {
    if (isProcessingLoan) {
      return;
    }

    try {
      Material material = materialManager.getMaterialById(Integer.parseInt(qrCode));
      User user = userManager.getUserById(Integer.parseInt(borrowerIdField.getText()));

      if (material != null
          && user != null
          && loanManager.isDocumentIssued(user.getId(), material.getId())) {
        isProcessingLoan = true;
        showConfirmationAndCreateLoan(user, material);
        cameraHelper.releaseCamera();
      }
    } catch (SQLException e) {
      e.printStackTrace();
      isProcessingLoan = false;
    }
  }

  private void setupFormValidation() {
    ChangeListener<String> inputListener =
        (observable, oldValue, newValue) -> {
          boolean isBorrowerValid =
              !borrowerIdField.getText().isEmpty()
                  && borrowerInfoLabel.getText().startsWith("Name:");
          boolean isMaterialValid =
              !materialIdField.getText().isEmpty()
                  && materialInfoLabel.getText().startsWith("Title:");
          boolean isLoanExist = false;
          try {
            isLoanExist =
                loanManager.isDocumentIssued(
                    Integer.parseInt(borrowerIdField.getText()),
                    Integer.parseInt(materialIdField.getText()));
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
          updateLoanInfo(borrowerIdField.getText(), materialIdField.getText());

          addLoanBtn.setDisable(!(isBorrowerValid && isMaterialValid && isLoanExist));
        };

    borrowerIdField.textProperty().addListener(inputListener);
    materialIdField.textProperty().addListener(inputListener);
  }

  private void setupFieldListeners() {
    borrowerIdField
        .textProperty()
        .addListener((observable, oldValue, newValue) -> updateBorrowerInfo(newValue));
    materialIdField
        .textProperty()
        .addListener((observable, oldValue, newValue) -> updateMaterialInfo(newValue));
  }

  private void updateBorrowerInfo(String borrowerId) {
    try {
      User user = userManager.getUserById(Integer.parseInt(borrowerId));
      if (user != null) {
        borrowerInfoLabel.setText(
            String.format("Name: %s%nEmail: %s", user.getName(), user.getEmail()));
      } else {
        borrowerInfoLabel.setText("User not found");
      }
    } catch (NumberFormatException | SQLException e) {
      borrowerInfoLabel.setText("Invalid ID");
    }
  }

  private void updateMaterialInfo(String materialId) {
    try {
      Material material = materialManager.getMaterialById(Integer.parseInt(materialId));
      if (material != null) {
        materialInfoLabel.setText(
            String.format("Title: %s%nAuthor: %s", material.getTitle(), material.getAuthor()));
      } else {
        materialInfoLabel.setText("Material not found");
      }
    } catch (NumberFormatException | SQLException e) {
      materialInfoLabel.setText("Invalid ID");
    }
  }

  private void updateLoanInfo(String borrowerId, String materialId) {
    try {
      Loan loan = loanManager.getLoan(Integer.parseInt(borrowerId), Integer.parseInt(materialId));
      if (loan != null) {
        loanInfoLabel.setText(
            String.format(
                "Borrowed on: %s%nDue date: %s",
                loan.getBorrowDate().toString(), loan.getDueDate().toString()));
      } else {
        loanInfoLabel.setText("Loan not found");
      }
    } catch (SQLException e) {
      loanInfoLabel.setText("Cannot retrieve loan information");
    }
  }

  private void handleAddLoan() {
    if (isProcessingLoan) {
      return;
    }

    try {
      User user = userManager.getUserById(Integer.parseInt(borrowerIdField.getText()));
      Material material =
          materialManager.getMaterialById(Integer.parseInt(materialIdField.getText()));

      if (user != null && material != null) {
        isProcessingLoan = true;
        showConfirmationAndCreateLoan(user, material);
      }
    } catch (SQLException e) {
      showErrorAlert("Failed to add loan", "An error occurred while adding the loan");
      isProcessingLoan = false;
    }
  }

  private void showConfirmationAndCreateLoan(User user, Material material) {
    Alert confirmation = createConfirmationAlert(user, material);
    confirmation
        .showAndWait()
        .ifPresent(
            response -> {
              if (response == ButtonType.OK) {
                returnLoan(user, material);
              }
              isProcessingLoan = false;
            });
  }

  private Alert createConfirmationAlert(User user, Material material) {
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    confirmation.setTitle("Return Loan");
    confirmation.setHeaderText("Are you sure you want to return this loan?");
    confirmation.setContentText(
        String.format(
            "Borrower: %s%nMaterial: %s%nThis action cannot be undone",
            user.getName(), material.getTitle()));
    return confirmation;
  }

  private void returnLoan(User user, Material material) {
    try {
      Loan loan = loanManager.getLoan(user.getId(), material.getId());
      loan.setReturnDate(LocalDate.now());
      loanManager.updateLoan(loan);

      showSuccessAlert();
      modalPane.hide();
      refreshLoanTable();
    } catch (SQLException e) {
      showErrorAlert("Failed to add loan", "An error occurred while adding the loan");
    } finally {
      isProcessingLoan = false;
    }
  }

  private void showSuccessAlert() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Success");
    alert.setHeaderText("Loan returned successfully");
    alert.showAndWait();
  }

  private void showErrorAlert(String header, String content) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(header);
    alert.setContentText(content);
    alert.showAndWait();
  }

  private void refreshLoanTable() {
    loanViewController.refreshLoanTable();
  }

  public void cleanup() {
    if (cameraHelper != null) {
      cameraHelper.releaseCamera();
    }
  }
}
