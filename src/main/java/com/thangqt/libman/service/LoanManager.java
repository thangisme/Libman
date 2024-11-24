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
    if (loanDAO.isDocumentIssued(loan.getUserId(), loan.getMaterialId())) {
      throw new SQLException("Document already issued to the user");
    }
    loanDAO.add(loan);
    materialManager.decreaseAvailableQuantity(loan.getMaterialId());
  }

    public Loan getLoan(int userId, int materialId) throws SQLException {
        return loanDAO.get(userId, materialId);
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
    materialManager.increaseAvailableQuantity(loanDAO.getById(loanId).getMaterialId());
  }

    public void returnLoan(int userId, int materialId) throws SQLException {
        loanDAO.returnLoan(userId, materialId);
        materialManager.increaseAvailableQuantity(materialId);
    }

  public List<Loan> getOverdueLoans() throws SQLException {
    return loanDAO.getOverdueLoans();
  }

  public List<Loan> getBorrowedLoansWithinDayRange(int range) throws SQLException {
    return loanDAO.getBorrowedLoansWithinDayRange(range);
  }

  public List<Loan> getOverdueLoanWithinDayRange(int range) throws SQLException {
    return loanDAO.getOverdueLoanWithinDayRange(range);
  }

  public List<Loan> getReturnedLoansWithinDayRange(int range) throws SQLException {
    return loanDAO.getReturnedLoansWithinDayRange(range);
  }

  public boolean isDocumentIssued(int userId, int materialId) throws SQLException {
    return loanDAO.isDocumentIssued(userId, materialId);
  }

  public boolean isLoanExist(int loanId) throws SQLException {
    return loanDAO.isExist(loanId);
  }

  public int getTotalBorrowedNumber() throws SQLException {
    return loanDAO.getTotalBorrowedNumber();
  }

  public int getTotalOverdueNumber() throws SQLException {
    return loanDAO.getOverdueLoansNumber();
  }

  public List<Loan> getRecentlyBorrowedLoans(int i) throws SQLException {
    return loanDAO.getRecentlyBorrowedLoans(i);
  }
}
