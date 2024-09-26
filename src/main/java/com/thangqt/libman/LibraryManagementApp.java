package com.thangqt.libman;

import com.thangqt.libman.service.*;
import com.thangqt.libman.view.cli.ConsoleView;

import java.sql.SQLException;

public class LibraryManagementApp {
    public static void main(String[] args) throws SQLException {
        // Initialize the ServiceFactory
        ServiceFactory serviceFactory = ServiceFactory.getInstance();

        // Get the managers
        UserManager userManager = serviceFactory.getUserManager();
        MaterialManager materialManager = serviceFactory.getMaterialManager();
        LoanManager loanManager = serviceFactory.getLoanManager();

        // Create and start the ConsoleView
        ConsoleView consoleView = new ConsoleView(userManager, materialManager, loanManager);
        consoleView.start();
    }
}
