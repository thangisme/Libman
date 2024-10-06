package com.thangqt.libman.service;

import com.thangqt.libman.DAO.LoanDAO;
import com.thangqt.libman.model.Loan;

import java.sql.SQLException;
import java.util.List;

public class LoanManager {
    private LoanDAO loanDAO;
    private UserManager userManager;
    private MaterialManager materialManager;

    public LoanManager(LoanDAO loanDAO, UserManager userManager, MaterialManager materialManager) {
        this.loanDAO = loanDAO;
        this.userManager = userManager;
        this.materialManager = materialManager;

    }

    public void addLoan(Loan loan) throws SQLException {
        loanDAO.add(loan);
    }

    public void updateLoan(Loan loan) throws SQLException {
        loanDAO.update(loan);
    }

    public void deleteLoan(int id) throws SQLException {
        loanDAO.delete(id);
    }

    public Loan getLoanById(int id) throws SQLException {
        return loanDAO.getById(id);
    }

    public List<Loan> getAllLoans() throws SQLException {
        return loanDAO.getAll();
    }

    public List<Loan> getLoansByUser(int userId) throws SQLException {
        return loanDAO.getLoansByUser(userId);
    }

    public List<Loan> getLoansByMaterial(int materialId) throws SQLException {
        return loanDAO.getLoansByMaterial(materialId);
    }

    public void returnLoan(int loanId) throws SQLException {
        loanDAO.returnLoan(loanId);
    }

    public List<Loan> getOverdueLoans() throws SQLException {
        return loanDAO.getOverdueLoans();
    }

}
