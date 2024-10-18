package com.thangqt.libman.controller;

import atlantafx.base.controls.Card;
import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import com.thangqt.libman.model.User;
import com.thangqt.libman.service.ServiceFactory;
import com.thangqt.libman.service.UserManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Callback;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;
import java.util.List;

public class UserViewController {
    private UserManager userManager;

    @FXML
    GridPane userViewContainer;

    @FXML
    HBox userViewHeadlineContainer;

    @FXML
    VBox userViewTableContainer;

    @FXML
    public void initialize() throws SQLException {
        Tile tile = new Tile();
        tile.setTitle("Users");
        tile.setDescription("Manage and view user details");
        Button addBtn = new Button("Add user", new FontIcon(Feather.PLUS));
        addBtn.getStyleClass().add(Styles.ROUNDED);
        tile.setAction(addBtn);
        HBox.setHgrow(tile, Priority.ALWAYS);
        userViewHeadlineContainer.getChildren().add(tile);
        ServiceFactory serviceFactory = ServiceFactory.getInstance();
        userManager = serviceFactory.getUserManager();
        List<User> users = userManager.getAllUsers();
        TableView userTable = createUserTable(users, 10);
        Card userTableCard = new Card();
        userTableCard.setBody(userTable);
        userViewTableContainer.getChildren().add(userTableCard);
        Pagination pagination = new Pagination(users.size() / 10 + 1, 0);
        pagination.setMaxPageIndicatorCount(5);
        pagination.setPageFactory(pageIndex -> {
            int fromIndex = pageIndex * 10;
            int toIndex = Math.min(fromIndex + 10, users.size());
            userTable.getItems().setAll(users.subList(fromIndex, toIndex));
            return new StackPane();
        });
        userViewTableContainer.getChildren().add(pagination);
    }

    private TableView createUserTable(List<User> users, int pageSize) {
        TableView tableView = new TableView();
        var column1 = new TableColumn<User, String> ("ID");
        column1.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        column1.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1));

        var column2 = new TableColumn<User, String> ("Name");
        column2.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        column2.prefWidthProperty().bind(tableView.widthProperty().multiply(0.35));

        var column3 = new TableColumn<User, String> ("Email");
        column3.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        column3.prefWidthProperty().bind(tableView.widthProperty().multiply(0.35));

        var column4 = new TableColumn<User, String> ("Actions");
        column4.setCellFactory(new UserTableActionCellFactory());
        column4.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));

        tableView.getColumns().addAll(column1, column2, column3, column4);
        tableView.getItems().addAll(users.subList(0, Math.min(pageSize, users.size())));
        tableView.getStyleClass().addAll(Tweaks.EDGE_TO_EDGE, Styles.STRIPED, Styles.BORDERED);
        tableView.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
        return tableView;
    }
}

class UserTableActionCellFactory implements Callback<TableColumn<User, String>, TableCell<User, String>> {

    @Override
    public TableCell<User, String> call(TableColumn<User, String> param) {
        return new TableCell<User, String>() {
            MenuButton menuButton = new MenuButton("Actions");

            {
                MenuItem viewItem = new MenuItem("View details", new FontIcon(Feather.EYE));
                MenuItem editItem = new MenuItem("Edit", new FontIcon(Feather.EDIT));
                MenuItem deleteItem = new MenuItem("Delete", new FontIcon(Feather.TRASH));
                menuButton.getItems().addAll(viewItem, editItem, deleteItem);

                viewItem.setOnAction(event -> handleViewAction(getTableView().getItems().get(getIndex())));
                editItem.setOnAction(event -> handleEditAction(getTableView().getItems().get(getIndex())));
                deleteItem.setOnAction(event -> handleDeleteAction(getTableView().getItems().get(getIndex())));
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

    private void handleViewAction(User user) {
    }

    private void handleEditAction(User user) {

    }

    private void handleDeleteAction(User user) {

    }
}
