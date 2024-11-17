package com.thangqt.libman.controller;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import com.thangqt.libman.model.Loan;
import com.thangqt.libman.service.LoanManager;
import com.thangqt.libman.service.MaterialManager;
import com.thangqt.libman.service.ServiceFactory;
import com.thangqt.libman.service.SessionManager;
import com.thangqt.libman.view.GraphicalView.LoanTile;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.ColumnConstraints;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.*;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class UserLoanController {
  private LoanManager loanManager;
  private MaterialManager materialManager;
  private List<Loan> loans;

  @FXML private VBox userLoanHeader;
  @FXML private ScrollPane userLoanContainer;

  public UserLoanController() throws SQLException {
    loanManager = ServiceFactory.getInstance().getLoanManager();
    materialManager = ServiceFactory.getInstance().getMaterialManager();
  }

  @FXML
  public void initialize() throws SQLException {
    setUpTile();
    loans =
        loanManager.getLoansByUser(SessionManager.getCurrentUser().getId()).stream()
            .sorted(
                Comparator.comparing((Loan loan) -> loan.getReturnDate() == null ? 1 : 0)
                    .thenComparing(Loan::getBorrowDate)
                    .reversed())
            .toList(); // Prioritize active loans then sort by borrow date
    setUpLoanListings(loans);
  }

  private void setUpTile() {
    Tile tile = new Tile();
    tile.setTitle("My loans");
    tile.setDescription("View and manage your loans");
    CustomTextField searchField = new CustomTextField();
    searchField.setPromptText("Search loan");
    searchField.getStyleClass().add(Styles.ROUNDED);
    FontIcon clearIcon = new FontIcon(Feather.X);
    clearIcon.setCursor(Cursor.HAND);
    clearIcon.setOnMouseClicked(
        event -> {
          searchField.clear();
          refreshLoanListings();
        });
    searchField.setRight(clearIcon);
    searchField.setOnAction(event -> searchLoan(searchField.getText()));

    HBox.setHgrow(searchField, Priority.ALWAYS);
    Button searchBtn = new Button("Search", new FontIcon(Feather.SEARCH));
    searchBtn.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.ROUNDED);
    searchBtn.setOnAction(event -> {});

    HBox searchInputGroup = new HBox(searchField, searchBtn);
    searchInputGroup.setSpacing(10);
    tile.setAction(searchInputGroup);
    HBox.setHgrow(tile, Priority.ALWAYS);
    userLoanHeader.getChildren().add(tile);
  }

  private void setUpLoanListings(List<Loan> loans) {
    final int COLUMN_COUNT = 2;
    int rowCount = (int) Math.ceil((double) loans.size() / COLUMN_COUNT);

    GridPane gridPane = new GridPane();
    gridPane.setHgap(10);
    gridPane.setVgap(10);

    for (int i = 0; i < COLUMN_COUNT; i++) {
      ColumnConstraints column = new ColumnConstraints();
      column.setPercentWidth(100.0 / COLUMN_COUNT);
      gridPane.getColumnConstraints().add(column);
    }

    for (int i = 0; i < rowCount; i++) {
      for (int j = 0; j < COLUMN_COUNT; j++) {
        int loanIndex = i * COLUMN_COUNT + j;
        if (loanIndex >= loans.size()) {
          break;
        }
        Loan loan = loans.get(loanIndex);
        try {
          LoanTile loanTile = new LoanTile(loan, this);
          gridPane.add(loanTile, j, i);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }

    userLoanContainer.setContent(gridPane);
  }

  private void searchLoan(String query) {
    List<Loan> searchResults =
        loans.stream()
            .filter(
                loan -> {
                  try {
                    String materialTitle =
                        materialManager
                            .getMaterialById(loan.getMaterialId())
                            .getTitle()
                            .toLowerCase();
                    String materialAuthor =
                        materialManager
                            .getMaterialById(loan.getMaterialId())
                            .getAuthor()
                            .toLowerCase();
                    return materialTitle.contains(query.toLowerCase())
                        || materialAuthor.contains(query.toLowerCase());
                  } catch (SQLException e) {
                    throw new RuntimeException(e);
                  }
                })
            .sorted(Comparator.comparing(Loan::getBorrowDate).reversed())
            .toList();

    if (!searchResults.isEmpty()) {
      setUpLoanListings(searchResults);
    } else {
      VBox noResults = new VBox();
      noResults.setAlignment(Pos.CENTER);
      noResults.getChildren().add(new Label("No results found"));
      userLoanContainer.setContent(noResults);
    }
  }

  private void refreshLoanListings() {
    try {
      loans =
          loanManager.getLoansByUser(SessionManager.getCurrentUser().getId()).stream()
              .sorted(
                  Comparator.comparing((Loan loan) -> loan.getReturnDate() == null ? 1 : 0)
                      .thenComparing(Loan::getBorrowDate)
                      .reversed())
              .toList(); // Prioritize active loans then sort by borrow date
      setUpLoanListings(loans);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void returnLoan(Loan loan) {
    makeDialog(
            Alert.AlertType.CONFIRMATION,
            "Return loan",
            "Are you sure you want to return this loan?",
            "This action cannot be undone.")
        .showAndWait()
        .filter(response -> response == ButtonType.OK)
        .ifPresent(
            response -> {
              try {
                loanManager.returnLoan(loan.getId());
                makeDialog(
                        Alert.AlertType.INFORMATION,
                        "Loan returned",
                        "Loan has been successfully returned.",
                        "")
                    .showAndWait();
                refreshLoanListings();
              } catch (SQLException e) {
                e.printStackTrace();
                makeDialog(
                        Alert.AlertType.ERROR,
                        "Error",
                        "An error occurred while returning the loan.",
                        e.getMessage())
                    .showAndWait();
              }
            });
  }

  public void renewLoan(Loan loan) {
    // TODO: set a limit on the number of times a loan can be renewed
    makeDialog(
            Alert.AlertType.CONFIRMATION,
            "Renew loan",
            "Are you sure you want to renew this loan?",
            "The loan will be renewed for another 14 days.")
        .showAndWait()
        .filter(response -> response == ButtonType.OK)
        .ifPresent(
            response -> {
              try {
                loan.setDueDate(loan.getDueDate().plusDays(14));
                loanManager.updateLoan(loan);
                makeDialog(
                        Alert.AlertType.INFORMATION,
                        "Loan renewed",
                        "Loan has been successfully renewed.",
                        "The new due date is " + loan.getDueDate())
                    .showAndWait();
                refreshLoanListings();
              } catch (SQLException e) {
                e.printStackTrace();
                makeDialog(
                        Alert.AlertType.ERROR,
                        "Error",
                        "An error occurred while renewing the loan.",
                        e.getMessage())
                    .showAndWait();
              }
            });
  }

  private Alert makeDialog(Alert.AlertType type, String title, String headerText, String content) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(headerText);
    alert.setContentText(content);
    return alert;
  }
}
