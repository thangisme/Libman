package com.thangqt.libman.controller;

import atlantafx.base.theme.Tweaks;
import com.thangqt.libman.model.Loan;
import com.thangqt.libman.service.LoanManager;
import com.thangqt.libman.service.MaterialManager;
import com.thangqt.libman.service.ServiceFactory;
import com.thangqt.libman.service.UserManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.TableView;
import javafx.geometry.Pos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;

public class HomeController {
    private MaterialManager materialManager;
    private UserManager userManager;
    private LoanManager loanManager;

    @FXML
    private Text totalMaterialsNum;

    @FXML
    private Text borrowedNum;

    @FXML
    private Text overdueNum;

    @FXML
    private Text totalUsersNum;

    @FXML
    private VBox checkoutStatsContainer;

    @FXML
    private VBox overdueTableContainer;

    public void initialize() throws SQLException {
        ServiceFactory serviceFactory = ServiceFactory.getInstance();
        userManager = serviceFactory.getUserManager();
        materialManager = serviceFactory.getMaterialManager();
        loanManager = serviceFactory.getLoanManager();

        int totalMaterialsNum = getTotalMaterialsNum();
        int totalUsersNum = getTotalUsersNum();
        int borrowedNum = getTotalBorrowedNum();
        int overdueNum = getTotalOverdueNum();

        this.totalMaterialsNum.setText(String.valueOf(totalMaterialsNum));
        this.totalUsersNum.setText(String.valueOf(totalUsersNum));
        this.borrowedNum.setText(String.valueOf(borrowedNum));
        this.overdueNum.setText(String.valueOf(overdueNum));

        checkoutStatsContainer.getChildren().add(createCheckoutStatsChart());
        if (overdueNum > 0) {
            overdueTableContainer.getChildren().add(createOverdueTable());
        } else {
            overdueTableContainer.setAlignment(Pos.CENTER);
            overdueTableContainer.getChildren().add(new Text("No overdue loans"));
        }
    }

    private int getTotalMaterialsNum() throws SQLException {
        return materialManager.getTotalMaterialsNumber();
    }

    private int getTotalUsersNum() throws SQLException {
        return userManager.getTotalUsersNumber();
    }

    private int getTotalBorrowedNum() throws SQLException {
        return loanManager.getTotalBorrowedNumber();
    }

    private int getTotalOverdueNum() throws SQLException {
        return loanManager.getTotalOverdueNumber();
    }


    private LineChart<String, Number> createCheckoutStatsChart() throws SQLException {
        List<Loan> borrowedLoans = loanManager.getBorrowedLoansWithinDayRange(7);
        List<Loan> returnedLoans = loanManager.getReturnedLoansWithinDayRange(7);

        var x = new CategoryAxis();
        x.setLabel("Date");

        var y = new NumberAxis();
        y.setLabel("Number of Books");

        var borrowedNum = new XYChart.Series<String, Number>();
        borrowedNum.setName("Borrowed");

        var returnedNum = new XYChart.Series<String, Number>();
        returnedNum.setName("Returned");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        Map<String, Integer> borrowedCount = new TreeMap<>();
        Map<String, Integer> returnedCount = new TreeMap<>();
        IntStream.range(1, 8).forEach(i -> {
            String date = LocalDate.now().minusDays(i).format(formatter);
            borrowedCount.put(date, 0);
            returnedCount.put(date, 0);
        });

        for (Loan loan : borrowedLoans) {
            String borrowDate = loan.getBorrowDate().format(formatter);
            borrowedCount.put(borrowDate, borrowedCount.get(borrowDate) + 1);
        }

        for (Loan loan : returnedLoans) {
            String returnDate = loan.getReturnDate().format(formatter);
            returnedCount.put(returnDate, returnedCount.get(returnDate) + 1);
        }

        borrowedCount.forEach((date, count) -> borrowedNum.getData().add(new XYChart.Data<>(date, count)));
        returnedCount.forEach((date, count) -> returnedNum.getData().add(new XYChart.Data<>(date, count)));

        var chart = new LineChart<>(x, y);
        chart.setTitle("Loan stats for the last 7 days");
        chart.setMinHeight(300);
        chart.getData().addAll(borrowedNum, returnedNum);

        return chart;
    }

    private TableView<Loan> createOverdueTable() throws SQLException {
        var col1 = new TableColumn<Loan, String>("Title");
        col1.setCellValueFactory(
                c -> {
                    try {
                        return new SimpleStringProperty(materialManager.getMaterialById(c.getValue().getMaterialId()).getTitle());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return new SimpleStringProperty("Error");
                    }
                }
        );

        var col2 = new TableColumn<Loan, String>("Author");
        col2.setCellValueFactory(
                c -> {
                    try {
                        return new SimpleStringProperty(materialManager.getMaterialById(c.getValue().getMaterialId()).getAuthor());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return new SimpleStringProperty("Error");
                    }
                }
        );

        var col3 = new TableColumn<Loan, String>("Due Date");
        col3.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().getDueDate().toString())
        );

        var col4 = new TableColumn<Loan, String>("Borrower");
        col4.setCellValueFactory(
                c -> {
                    try {
                        return new SimpleStringProperty(userManager.getUserById(c.getValue().getUserId()).getName() + " (#" + c.getValue().getUserId() + ")");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        var table = new TableView<Loan>();
        table.getColumns().setAll(col1, col2, col3, col4);
        table.getItems().addAll(loanManager.getOverdueLoans());
        table.getStyleClass().add(Tweaks.EDGE_TO_EDGE);
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
        return table;
    }
}