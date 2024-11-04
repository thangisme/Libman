package com.thangqt.libman;

import com.thangqt.libman.controller.ConsoleController;
import com.thangqt.libman.service.*;
import com.thangqt.libman.view.ConsoleView;
import com.thangqt.libman.view.GraphicalView.GraphicalView;
import javafx.application.Application;

import java.sql.SQLException;

public class Libman {
  public static void main(String[] args) throws SQLException {
    ServiceFactory serviceFactory = ServiceFactory.getInstance();
    UserManager userManager = serviceFactory.getUserManager();
    MaterialManager materialManager = serviceFactory.getMaterialManager();
    LoanManager loanManager = serviceFactory.getLoanManager();

    if (args.length > 0 && args[0].equalsIgnoreCase("cli")) {
      ConsoleController consoleController = new ConsoleController(userManager, materialManager, loanManager);
      ConsoleView consoleView = new ConsoleView(consoleController);
      consoleView.start();
    } else {
      Application.launch(GraphicalView.class, args);
    }
  }
}
