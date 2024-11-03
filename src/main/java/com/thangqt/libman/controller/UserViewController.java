package com.thangqt.libman.controller;

import atlantafx.base.controls.Card;
import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import com.thangqt.libman.model.Loan;
import com.thangqt.libman.model.User;
import com.thangqt.libman.service.LoanManager;
import com.thangqt.libman.service.MaterialManager;
import com.thangqt.libman.service.ServiceFactory;
import com.thangqt.libman.service.UserManager;
import java.sql.SQLException;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Callback;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class UserViewController {
    private final UserManager userManager;
    private final LoanManager loanManager;
    private final MaterialManager materialManager;

    @FXML
    GridPane userViewContainer;

    @FXML
    HBox userViewHeadlineContainer;

    @FXML
    VBox userViewTableContainer;

    public UserViewController() throws SQLException {
        ServiceFactory serviceFactory = ServiceFactory.getInstance();
        this.userManager = serviceFactory.getUserManager();
        this.loanManager = serviceFactory.getLoanManager();
        this.materialManager = serviceFactory.getMaterialManager();
    }

    @FXML
    public void initialize() throws SQLException {
        setupTile();
        setupUserTable();
    }

    private void setupTile() {
        Tile tile = new Tile();
        tile.setTitle("Users");
        tile.setDescription("Manage and view user details");
        HBox actionContainer = new HBox();
        actionContainer.setSpacing(15);
        CustomTextField searchField = new CustomTextField();
        searchField.setPromptText("Search by ID or email");
        searchField.getStyleClass().add(Styles.ROUNDED);
        FontIcon clearIcon = new FontIcon(Feather.X);
        clearIcon.setCursor(Cursor.HAND);
        clearIcon.setOnMouseClicked(event -> searchField.clear());
        searchField.setRight(clearIcon);
        searchField.setOnAction(event -> {
            searchUser(searchField.getText());
        });
        HBox.setHgrow(searchField, Priority.ALWAYS);
        Button searchBtn = new Button("Search", new FontIcon(Feather.SEARCH));
        searchBtn.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.ROUNDED);
        searchBtn.setOnAction(event -> {
            searchUser(searchField.getText());
        });
        HBox searchInputGroup = new HBox(searchField, searchBtn);
        Button addBtn = new Button("Add user", new FontIcon(Feather.PLUS));
        addBtn.setOnAction(event -> showAddUserDialog());
        addBtn.getStyleClass().add(Styles.ROUNDED);
        actionContainer.getChildren().add(searchInputGroup);
        actionContainer.getChildren().add(addBtn);
        tile.setAction(actionContainer);
        HBox.setHgrow(tile, Priority.ALWAYS);
        userViewHeadlineContainer.getChildren().add(tile);
    }

    private void searchUser(String text) {
        if (!text.isEmpty()) {
            if (text.matches("\\d+")) {
                try {
                    User user = userManager.getUserById(Integer.parseInt(text));
                    if (user == null) {
                        showUserNotFoundDialog();
                    } else {
                        showUserDetailsDialog(user);
                    }
                } catch (SQLException e) {
                    showErrorDialog(e);
                }
            } else {
                try {
                    User user = userManager.getUserByEmail(text);
                    if (user == null) {
                        showUserNotFoundDialog();
                    } else {
                        showUserDetailsDialog(user);
                    }
                } catch (SQLException e) {
                    showErrorDialog(e);
                }
            }
        }
    }

    private void showUserNotFoundDialog() {
        Alert dialog = new Alert(Alert.AlertType.WARNING);
        dialog.setTitle("User not found");
        dialog.setHeaderText("User not found");
        dialog.setContentText("No user found with the provided ID or email");
        dialog.show();
    }

    private void setupUserTable() throws SQLException {
        List<User> users = userManager.getAllUsers();
        TableView userTable = createUserTable(users, 15);
        Card userTableCard = new Card();
        userTableCard.setBody(userTable);
        userViewTableContainer.getChildren().add(userTableCard);
        setupPagination(users, userTable);
    }

    private void refreshUserTable() throws SQLException {
        List<User> users = userManager.getAllUsers();
        TableView userTable = createUserTable(users, 10);
        Card userTableCard = new Card();
        userTableCard.setBody(userTable);
        userViewTableContainer.getChildren().clear();
        userViewTableContainer.getChildren().add(userTableCard);
        setupPagination(users, userTable);
    }

    private void setupPagination(List<User> users, TableView userTable) {
        Pagination pagination = new Pagination(users.size() / 15 + 1, 0);
        pagination.setMaxPageIndicatorCount(5);
        pagination.setPageFactory(pageIndex -> {
            int fromIndex = pageIndex * 15;
            int toIndex = Math.min(fromIndex + 15, users.size());
            userTable.getItems().setAll(users.subList(fromIndex, toIndex));
            return new StackPane();
        });
        userViewTableContainer.getChildren().add(pagination);
    }

    private TableView createUserTable(List<User> users, int pageSize) {
        TableView tableView = new TableView();
        tableView.setPrefHeight(680);

        var column1 = new TableColumn<User, String>("ID");
        column1.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        column1.prefWidthProperty().bind(tableView.widthProperty().multiply(0.05));

        var column2 = new TableColumn<User, String>("Name");
        column2.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        column2.prefWidthProperty().bind(tableView.widthProperty().multiply(0.35));

        var column3 = new TableColumn<User, String>("Email");
        column3.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        column3.prefWidthProperty().bind(tableView.widthProperty().multiply(0.35));

        var column4 = new TableColumn<User, String>("Actions");
        column4.setCellFactory(new UserTableActionCellFactory(this));
        column4.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));

        tableView.getColumns().addAll(column1, column2, column3, column4);
        tableView.getItems().addAll(users.subList(0, Math.min(pageSize, users.size())));
        tableView.getStyleClass().addAll(Tweaks.EDGE_TO_EDGE, Styles.STRIPED, Styles.BORDERED);
        return tableView;
    }

    private void showAddUserDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Add user");
        dialog.setHeaderText("Add a new user");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Label nameLabel = new Label("Name");
        Label emailLabel = new Label("Email");
        TextField nameField = new TextField();
        nameField.setPromptText("Your name");
        TextField emailField = new TextField();
        emailField.setPromptText("example@gmail.com");
        VBox form = new VBox(nameLabel, nameField, emailLabel, emailField);
        form.setSpacing(10);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new User(nameField.getText(), emailField.getText());
            }
            return null;
        });
        dialog.showAndWait().ifPresent(user -> addUser(user));
    }

    private void addUser(User user) {
        try {
            userManager.addUser(user);
            showSuccessDialog(user, "added");
            refreshUserTable();
        } catch (SQLException e) {
            showErrorDialog(e);
        }
    }

    private void showSuccessDialog(User user, String action) {
        Alert successDialog = new Alert(Alert.AlertType.INFORMATION);
        successDialog.setTitle("Success");
        successDialog.setHeaderText("User " + action + " successfully");
        successDialog.setContentText("User " + user.getName() + " has been " + action + " successfully");
        successDialog.show();
    }

    private void showErrorDialog(SQLException e) {
        Alert errorDialog = new Alert(Alert.AlertType.ERROR);
        errorDialog.setTitle("Error");
        errorDialog.setHeaderText("An error occurred");
        errorDialog.setContentText(e.getMessage());
        errorDialog.show();
    }

    public void handleEditAction(User user) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit user");
        dialog.setHeaderText("Edit user details");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Label nameLabel = new Label("Name");
        Label emailLabel = new Label("Email");
        TextField nameField = new TextField(user.getName());
        TextField emailField = new TextField(user.getEmail());
        VBox form = new VBox(nameLabel, nameField, emailLabel, emailField);
        form.setSpacing(10);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new User(user.getId(), nameField.getText(), emailField.getText());
            }
            return null;
        });
        dialog.showAndWait().ifPresent(updatedUser -> updateUser(updatedUser));
    }

    private void updateUser(User updatedUser) {
        try {
            userManager.updateUser(updatedUser);
            showSuccessDialog(updatedUser, "updated");
            refreshUserTable();
        } catch (SQLException e) {
            showErrorDialog(e);
        }
    }

    public void handleViewAction(User user) {
        try {
            showUserDetailsDialog(user);
        } catch (SQLException e) {
            showErrorDialog(e);
        }
    }

    private void showUserDetailsDialog(User user) throws SQLException {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("User details");
        dialog.setHeaderText("User details");
        String basicInfo = "ID: " + user.getId() + "\nName: " + user.getName() + "\nEmail: " + user.getEmail();
        List<Loan> loans = loanManager.getLoansByUser(user.getId());
        String currentLoans = "Current loans: " + loans.size() + "\n";
        for (Loan loan : loans) {
            currentLoans += "-------\nLoan ID: " + loan.getId() + "\nTitle: " + materialManager.getMaterialById(loan.getMaterialId()).getTitle() + "\nBorrowed Date:" + loan.getBorrowDate() + "\nDue date: " + loan.getDueDate() + "\n";
        }
        Label basicInfoLabel = new Label(basicInfo + "\n" + currentLoans);
        Button editBtn = new Button("Edit", new FontIcon(Feather.EDIT));
        editBtn.setOnAction(event -> handleEditAction(user));
        editBtn.getStyleClass().add(Styles.BUTTON_OUTLINED);
        Button deleteBtn = new Button("Delete", new FontIcon(Feather.TRASH));
        deleteBtn.setOnAction(event -> handleDeleteAction(user));
        deleteBtn.getStyleClass().add(Styles.BUTTON_OUTLINED);
        HBox actionContainer = new HBox(editBtn, deleteBtn);
        actionContainer.setSpacing(10);
        VBox content = new VBox(basicInfoLabel, actionContainer);
        content.setSpacing(10);
        dialog.getDialogPane().setContent(content);
        dialog.show();
    }

    public void handleDeleteAction(User user) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Delete user");
        dialog.setHeaderText("Are you sure you want to delete this user?");
        dialog.setContentText("User ID: " + user.getId() + "\nName: " + user.getName() + "\nEmail: " + user.getEmail() + "\n\n!!!THIS ACTION CANNOT BE UNDONE.");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.YES) {
                deleteUser(user);
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void deleteUser(User user) {
        try {
            userManager.deleteUser(user.getId());
            showSuccessDialog(user, "deleted");
            refreshUserTable();
        } catch (SQLException e) {
            showErrorDialog(e);
        }
    }
}

class UserTableActionCellFactory implements Callback<TableColumn<User, String>, TableCell<User, String>> {
    private UserViewController userViewController;

    public UserTableActionCellFactory(UserViewController userViewController) {
        this.userViewController = userViewController;
    }

    @Override
    public TableCell<User, String> call(TableColumn<User, String> param) {
        return new TableCell<User, String>() {
            MenuButton menuButton = new MenuButton("Actions");

            {
                MenuItem viewItem = new MenuItem("View details", new FontIcon(Feather.EYE));
                MenuItem editItem = new MenuItem("Edit", new FontIcon(Feather.EDIT));
                MenuItem deleteItem = new MenuItem("Delete", new FontIcon(Feather.TRASH));
                menuButton.getItems().addAll(viewItem, editItem, deleteItem);

                viewItem.setOnAction(event -> userViewController.handleViewAction(getTableView().getItems().get(getIndex())));
                editItem.setOnAction(event -> userViewController.handleEditAction(getTableView().getItems().get(getIndex())));
                deleteItem.setOnAction(event -> userViewController.handleDeleteAction(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(menuButton);
                }
            }
        };
    }
}
