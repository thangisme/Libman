package com.thangqt.libman.view.GraphicalView;

import atlantafx.base.theme.Styles;
import atlantafx.base.controls.Card;
import com.thangqt.libman.controller.UserLoanController;
import com.thangqt.libman.model.Material;
import com.thangqt.libman.service.LoanManager;
import com.thangqt.libman.service.MaterialManager;
import com.thangqt.libman.service.ServiceFactory;
import com.thangqt.libman.utils.ImageLoader;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import com.thangqt.libman.model.Loan;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;
import java.time.LocalDate;

public class LoanTile extends Card {
  private Loan loan;
  private Material material;
  private LoanManager loanManager;
  private MaterialManager materialManager;
  private ImageView img;
  private UserLoanController controller;

  public LoanTile(Loan loan, UserLoanController controller) throws SQLException {
    this.loan = loan;
    this.controller = controller;
    this.loanManager = ServiceFactory.getInstance().getLoanManager();
    this.materialManager = ServiceFactory.getInstance().getMaterialManager();
    this.material = materialManager.getMaterialById(loan.getMaterialId());
    initialize();
  }

  private void initialize() {
    HBox container = new HBox();
    container.setAlignment(Pos.CENTER_LEFT);
    container.setSpacing(15);
    container.setPadding(new Insets(10, 20, 10, 20));
    img = new ImageView();
    img.setFitWidth(120);
    img.preserveRatioProperty().set(true);
    container.getChildren().add(img);

    VBox infoContainer = new VBox();
    infoContainer.setSpacing(5);
    infoContainer.setPadding(new javafx.geometry.Insets(10));

    Label status = new Label();
    status.setStyle("-fx-font-size: 0.8em");
    LocalDate dueDate = loan.getDueDate();
    boolean isReturned = loan.getReturnDate() != null;

    if (isReturned) {
      status.setText("Returned");
      status.getStyleClass().add(Styles.ACCENT);
      status.setGraphic(new FontIcon(Feather.ARCHIVE));
    } else if (dueDate.isBefore(LocalDate.now())) {
      status.setText("Overdue");
      status.getStyleClass().add(Styles.DANGER);
      status.setGraphic(new FontIcon(Feather.ALERT_TRIANGLE));
    } else if (dueDate.isEqual(LocalDate.now())) {
      status.setText("Due today");
      status.getStyleClass().add(Styles.WARNING);
      status.setGraphic(new FontIcon(Feather.ALERT_CIRCLE));
    } else if (dueDate.isBefore(LocalDate.now().plusDays(3))) {
      status.setText("Due soon");
      status.getStyleClass().add(Styles.WARNING);
      status.setGraphic(new FontIcon(Feather.ALERT_CIRCLE));
    } else {
      status.setText("Active");
      status.getStyleClass().add(Styles.SUCCESS);
      status.setGraphic(new FontIcon(Feather.CHECK_CIRCLE));
    }

    Text title = new Text(material.getTitle());
    title.setStyle("-fx-font-size: 1.5em");
    Text author = new Text(material.getAuthor());
    author.getStyleClass().addAll("text-small", "text-subtle");
    GridPane dateGrid = new GridPane();
    dateGrid.setHgap(10);

    Text borrowDateLabel = new Text("Borrowed on:");
    Text borrowDateText = new Text(loan.getBorrowDate().toString());

    Text dueDateLabel = new Text("Due on:");
    Text dueDateText = new Text(loan.getDueDate().toString());

    dateGrid.add(borrowDateLabel, 0, 0);
    dateGrid.add(borrowDateText, 1, 0);
    dateGrid.add(dueDateLabel, 0, 1);
    dateGrid.add(dueDateText, 1, 1);

    if (isReturned) {
      Text returnDateLabel = new Text("Returned on:");
      Text returnDateText = new Text(loan.getReturnDate().toString());
      dateGrid.add(returnDateLabel, 0, 2);
      dateGrid.add(returnDateText, 1, 2);
    }

    HBox actionContainer = new HBox();
    actionContainer.setPadding(new javafx.geometry.Insets(10, 0, 0, 0));
    actionContainer.setSpacing(10);
    if (!isReturned) {
      Button returnBtn = new Button("Return", new FontIcon(Feather.CORNER_LEFT_UP));
      returnBtn.setOnAction(e -> controller.returnLoan(loan));
      Button renewBtn = new Button("Renew", new FontIcon(Feather.REFRESH_CW));
      renewBtn.setOnAction(e -> controller.renewLoan(loan));
      actionContainer.getChildren().add(returnBtn);
      if (dueDate.isAfter(LocalDate.now())) {
        actionContainer.getChildren().add(renewBtn);
      }
    }

    infoContainer.getChildren().addAll(status, title, author, dateGrid, actionContainer);
    container.getChildren().add(infoContainer);

    this.setBody(container);

    ImageLoader.loadImageAsync(material.getCoverImageUrl(), img);
  }
}
