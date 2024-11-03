package com.thangqt.libman;

import com.thangqt.libman.controller.ConsoleController;
import com.thangqt.libman.service.*;
import com.thangqt.libman.view.ConsoleView;
import java.sql.SQLException;

public class LibraryManagementApp {
    public static void main(String[] args) throws SQLException {
        ServiceFactory serviceFactory = ServiceFactory.getInstance();
        UserManager userManager = serviceFactory.getUserManager();
        MaterialManager materialManager = serviceFactory.getMaterialManager();
        LoanManager loanManager = serviceFactory.getLoanManager();

        ConsoleController consoleController = new ConsoleController(userManager, materialManager, loanManager);

        // Create and start the ConsoleView
        ConsoleView consoleView = new ConsoleView(consoleController);
        consoleView.start();
    }
}