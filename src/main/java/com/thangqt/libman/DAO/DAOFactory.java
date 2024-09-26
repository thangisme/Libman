package com.thangqt.libman.DAO;

import java.sql.SQLException;

public class DAOFactory {
    private static DAOFactory instance;
    private MaterialDAO materialDAO;
    private UserDAO userDAO;
    private LoanDAO loanDAO;

    private DAOFactory() throws SQLException {
        materialDAO = new MySQLMaterialDAO();
        userDAO = new MySQLUserDAO();
        loanDAO = new MySQLLoanDAO();
    }

    public static DAOFactory getInstance() throws SQLException {
        if (instance == null) {
            instance = new DAOFactory();
        }
        return instance;
    }

    public MaterialDAO getMaterialDAO() {
        return materialDAO;
    }
    public UserDAO getUserDAO() {
        return userDAO;
    }
    public LoanDAO getLoanDAO() {
        return loanDAO;
    }
}
