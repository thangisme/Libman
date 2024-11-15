package com.thangqt.libman.controller;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import com.thangqt.libman.model.Loan;
import com.thangqt.libman.service.LoanManager;
import com.thangqt.libman.service.ServiceFactory;
import com.thangqt.libman.service.SessionManager;
import com.thangqt.libman.view.GraphicalView.LoanTile;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.ColumnConstraints;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class UserLoanController {
  private LoanManager loanManager;

  @FXML private VBox userLoanHeader;
  @FXML private ScrollPane userLoanContainer;

  public UserLoanController() throws SQLException {
    loanManager = ServiceFactory.getInstance().getLoanManager();
  }

  @FXML
  public void initialize() throws SQLException {
    setUpTile();
    List<Loan> loans = loanManager.getLoansByUser(SessionManager.getCurrentUser().getId());
    loans.sort(Comparator.comparing(Loan::getBorrowDate).reversed());
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
        });
    searchField.setRight(clearIcon);
    searchField.setOnAction(event -> {});

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
          LoanTile loanTile = new LoanTile(loan);
          gridPane.add(loanTile, j, i);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }

    userLoanContainer.setContent(gridPane);
  }
}
