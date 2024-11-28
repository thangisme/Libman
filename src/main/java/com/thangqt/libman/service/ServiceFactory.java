package com.thangqt.libman.service;

import com.thangqt.libman.DAO.DAOFactory;
import java.sql.SQLException;

public class ServiceFactory {
  private static ServiceFactory instance;
  private UserManager userManager;
  private MaterialManager materialManager;
  private LoanManager loanManager;
  private ReviewManager reviewManager;

  private ServiceFactory() throws SQLException {
    DAOFactory daoFactory = DAOFactory.getInstance();

    userManager = new UserManager(daoFactory.getUserDAO());
    materialManager = new MaterialManager(daoFactory.getMaterialDAO());
    loanManager = new LoanManager(daoFactory.getLoanDAO(), userManager, materialManager);
    reviewManager = new ReviewManager(daoFactory.getReviewDAO());
  }

  public static ServiceFactory getInstance() throws SQLException {
    if (instance == null) {
      instance = new ServiceFactory();
    }
    return instance;
  }

  public UserManager getUserManager() {
    return userManager;
  }

  public MaterialManager getMaterialManager() {
    return materialManager;
  }

  public LoanManager getLoanManager() {
    return loanManager;
  }

  public ReviewManager getReviewManager() {
    return reviewManager;
  }
}
