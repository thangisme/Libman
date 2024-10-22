package com.thangqt.libman.controller;

import atlantafx.base.controls.Card;
import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import com.thangqt.libman.model.Loan;
import com.thangqt.libman.model.User;
import com.thangqt.libman.service.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanViewController {
    private LoanManager loanManager;
    private UserManager userManager;
    private MaterialManager materialManager;

    @FXML
    private HBox loanViewHeadlineContainer;

    @FXML
    private VBox loanViewTableContainer;

    public LoanViewController() throws SQLException {
        ServiceFactory serviceFactory = ServiceFactory.getInstance();
        this.loanManager = serviceFactory.getLoanManager();
        this.userManager = serviceFactory.getUserManager();
        this.materialManager = serviceFactory.getMaterialManager();
    }

    @FXML
    public void initialize() throws SQLException {
        setupTile();
        setupLoanTable();
    }

    private void setupLoanTable() throws SQLException {
        List<Loan> loans = loanManager.getAllLoans();
        TableView userTable = createLoansTable(loans, 10);
        Card userTableCard = new Card();
        userTableCard.setBody(userTable);
        loanViewTableContainer.getChildren().add(userTableCard);
        setupPagination(loans, userTable);
    }

    private void setupPagination(List<Loan> loans, TableView loanTable) {
        Pagination pagination = new Pagination((loans.size() / 10) + 1, 0);
        pagination.setMaxPageIndicatorCount(5);
        pagination.setPageFactory(pageIndex -> {
            int fromIndex = pageIndex * 10;
            int toIndex = Math.min(fromIndex + 10, loans.size());
            loanTable.getItems().setAll(loans.subList(fromIndex, toIndex));
            return new StackPane();
        });
        loanViewTableContainer.getChildren().add(pagination);
    }

    private TableView createLoansTable(List<Loan> loans, int pageSize) {
        TableView tableView = new TableView();
        var column1 = new TableColumn<Loan, String>("ID");
        column1.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        column1.prefWidthProperty().bind(tableView.widthProperty().multiply(0.05));

        var column2 = new TableColumn<Loan, String>("Borrower");
        column2.setCellValueFactory(c -> {
            try {
                return new SimpleStringProperty(userManager.getUserById(c.getValue().getUserId()).getName());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        column2.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));

        var column3 = new TableColumn<Loan, String>("Title");
        column3.setCellValueFactory(c -> {
            try {
                return new SimpleStringProperty(materialManager.getMaterialById(c.getValue().getMaterialId()).getTitle());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        column3.prefWidthProperty().bind(tableView.widthProperty().multiply(0.3));

        var column4 = new TableColumn<Loan, String>("Author");
        column4.setCellValueFactory(c -> {
            try {
                String author = materialManager.getMaterialById(c.getValue().getMaterialId()).getAuthor();
                return new SimpleStringProperty(author == null ? "N/A" : author);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        column4.prefWidthProperty().bind(tableView.widthProperty().multiply(0.10));

        var column5 = new TableColumn<Loan, String>("Borrowed Date");
        column5.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBorrowDate().toString()));
        column5.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1));

        var column6 = new TableColumn<Loan, String>("Return Date");
        column6.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReturnDate() == null ? "N/A" : c.getValue().getReturnDate().toString()));
        column6.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1));

        var column7 = new TableColumn<Loan, String>("Status");
        column7.setCellFactory(new loanStatusCellFactory(this));
        column7.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1));

        var column8 = new TableColumn<Loan, String>("Actions");
        column8.setCellFactory(new LoanActionCellFactory(this));
        column8.prefWidthProperty().bind(tableView.widthProperty().multiply(0.08));

        tableView.getColumns().addAll(column1, column2, column3, column4, column5, column6, column7, column8);
        tableView.getItems().addAll(loans.subList(0, Math.min(pageSize, loans.size())));
        tableView.getStyleClass().addAll(Tweaks.EDGE_TO_EDGE, Styles.STRIPED, Styles.BORDERED);

        return tableView;
    }

    private void setupTile() {
        Tile tile = new Tile();
        tile.setTitle("Loans");
        tile.setDescription("Manage and view loan details");
        HBox actionContainer = new HBox();
        actionContainer.setSpacing(5);
        TextField searchField = new TextField();
        searchField.setPromptText("Search loan");
        searchField.getStyleClass().add(Styles.ROUNDED);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        Button searchBtn = new Button("Search", new FontIcon(Feather.SEARCH));
        searchBtn.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.ROUNDED);
        searchBtn.setOnAction(event -> {
            searchLoan(searchField.getText());
        });
        actionContainer.getChildren().addAll(searchField, searchBtn);
        tile.setAction(actionContainer);
        HBox.setHgrow(tile, Priority.ALWAYS);
        loanViewHeadlineContainer.getChildren().add(tile);
    }

    private void searchLoan(String text) {
        try {
            List<Loan> loans = new ArrayList<>();
            if (text.isBlank()) {
                loanViewTableContainer.getChildren().clear();
                setupLoanTable();
                return;
            } else {
                if (text.matches("\\d+")) {
                    loans.add(loanManager.getLoanById(Integer.parseInt(text)));
                    loans.addAll(loanManager.getLoansByUser(Integer.parseInt(text)));
                } else {
                    User user = userManager.getUserByEmail(text);
                    if (user != null) {
                        loans = loanManager.getLoansByUser(user.getId());
                    }
                }
            }
            if (loans != null) {
                TableView tableView = createLoansTable(loans, 100);
                loanViewTableContainer.getChildren().clear();
                Card userTableCard = new Card();
                userTableCard.setBody(tableView);
                loanViewTableContainer.getChildren().add(userTableCard);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Loan not found");
                alert.setContentText("No loan found with the provided information");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    private void returnLoan(Loan loan) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Return Loan");
        confirmation.setHeaderText("Are you sure you want to return this loan?");
        try {
            confirmation.setContentText("Loan ID: " + loan.getId() + "\nTitle: " + materialManager.getMaterialById(loan.getMaterialId()).getTitle() + "\nBorrower: " + userManager.getUserById(loan.getUserId()).getName() + "\nThis action cannot be undone");
        } catch (SQLException e) {
            e.printStackTrace();
            confirmation.setContentText("Loan ID: " + loan.getId());
        }
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    loanManager.returnLoan(loan.getId());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("Loan returned successfully");
                    alert.showAndWait();
                    refreshLoanTable();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Failed to return loan");
                    alert.setContentText("An error occurred while returning the loan");
                    alert.showAndWait();
                }
            }
        });
    }

    private void renewLoan(Loan loan) {
        var dialog = new TextInputDialog();
        dialog.setTitle("Renew Loan");
        dialog.setHeaderText("Current due date : " + loan.getDueDate() + "\nEnter the new due date");
        dialog.setContentText("New due date:");
        dialog.showAndWait().ifPresent(newReturnDate -> {
            try {
                loan.setDueDate(LocalDate.parse(newReturnDate));
                loanManager.updateLoan(loan);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Loan renewed successfully");
                alert.showAndWait();
                refreshLoanTable();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to renew loan");
                alert.setContentText("An error occurred while renewing the loan");
                alert.showAndWait();
            }
        });
    }

    private void refreshLoanTable() {
        try {
            loanViewTableContainer.getChildren().clear();
            setupLoanTable();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to refresh loan table");
            alert.setContentText("An error occurred while refreshing the loan table");
            alert.showAndWait();
        }
    }

    class loanStatusCellFactory implements Callback<TableColumn<Loan, String>, TableCell<Loan, String>> {
        private LoanViewController loanViewController;

        public loanStatusCellFactory(LoanViewController loanViewController) {
            this.loanViewController = loanViewController;
        }

        @Override
        public TableCell<Loan, String> call(TableColumn<Loan, String> param) {
            return new TableCell<>() {
                Label statusLabel = new Label();

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Loan loan = getTableView().getItems().get(getIndex());

                        if (loan.getReturnDate() == null) {
                            if (loan.getDueDate().isBefore(LocalDate.now())) {
                                statusLabel.setText("Overdue");
                                statusLabel.getStyleClass().setAll("label", Styles.DANGER);
                            } else {
                                statusLabel.setText("Borrowed");
                                statusLabel.getStyleClass().setAll("label", Styles.WARNING);
                            }
                        } else {
                            statusLabel.setText("Returned");
                            statusLabel.getStyleClass().setAll("label", Styles.SUCCESS);
                        }
                        setGraphic(statusLabel);
                    }
                }
            };
        }
    }

    class LoanActionCellFactory implements Callback<TableColumn<Loan, String>, TableCell<Loan, String>> {
        private LoanViewController loanViewController;

        public LoanActionCellFactory(LoanViewController loanViewController) {
            this.loanViewController = loanViewController;
        }

        @Override
        public TableCell<Loan, String> call(TableColumn<Loan, String> param) {
            return new TableCell<Loan, String>() {
                MenuButton menuButton = new MenuButton("", new FontIcon(Feather.MORE_HORIZONTAL));

                {
                    menuButton.getStyleClass().add(Styles.BUTTON_ICON);

                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Loan loan = getTableView().getItems().get(getIndex());
                        if (loan.getReturnDate() == null) {
                            menuButton.getItems().clear();
                            MenuItem returnItem = new MenuItem("Return", new FontIcon(Feather.CHECK));
                            returnItem.setOnAction(event -> loanViewController.returnLoan(loan));
                            MenuItem renewItem = new MenuItem("Renew", new FontIcon(Feather.REFRESH_CW));
                            renewItem.setOnAction(event -> loanViewController.renewLoan(loan));
                            menuButton.getItems().addAll(returnItem, renewItem);
                            setGraphic(menuButton);
                        } else {
                            setGraphic(null);
                        }
                    }
                }
            };
        }
    }
}
