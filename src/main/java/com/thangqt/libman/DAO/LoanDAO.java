package com.thangqt.libman.DAO;

import com.thangqt.libman.model.Loan;
import java.sql.SQLException;
import java.util.List;

public interface LoanDAO {
  void add(Loan loan) throws SQLException;

  Loan get(int userId, int materialId) throws SQLException;

  void update(Loan loan) throws SQLException;

  void delete(int id) throws SQLException;

  void returnLoan(int id) throws SQLException;

  void returnLoan(int userId, int materialId) throws SQLException;

  Loan getById(int id) throws SQLException;

  List<Loan> getAll() throws SQLException;

  List<Loan> getOverdueLoans() throws SQLException;

  List<Loan> getLoansByUser(int userId) throws SQLException;

  List<Loan> getLoansByMaterial(int materialId) throws SQLException;

  List<Loan> getBorrowedLoansWithinDayRange(int range) throws SQLException;

  List<Loan> getOverdueLoanWithinDayRange(int range) throws SQLException;

  List<Loan> getReturnedLoansWithinDayRange(int range) throws SQLException;

  List<Loan> getRecentlyBorrowedLoans(int i) throws SQLException;

  boolean isDocumentIssued(int userId, int materialId) throws SQLException;

  boolean isExist(int loanId) throws SQLException;

  int getTotalBorrowedNumber() throws SQLException;

  int getOverdueLoansNumber() throws SQLException;
}
