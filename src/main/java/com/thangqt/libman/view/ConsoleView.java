package com.thangqt.libman.view;

import com.thangqt.libman.controller.ConsoleController;
import java.sql.SQLException;

public class ConsoleView {
    private ConsoleController consoleController;

    public ConsoleView(ConsoleController consoleController) {
        this.consoleController = consoleController;
    }

    public void start() throws SQLException {
        consoleController.start();
    }
}