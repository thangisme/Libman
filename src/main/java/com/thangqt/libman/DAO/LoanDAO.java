package com.thangqt.libman.DAO;

import com.thangqt.libman.model.Loan;

import java.sql.SQLException;
import java.util.List;

public interface LoanDAO {
    void add(Loan loan) throws SQLException;
    void update(Loan loan) throws SQLException;
    void delete(int id) throws SQLException;
    Loan getById(int id) throws SQLException;
    List<Loan> getAll() throws SQLException;
    List<Loan> getOverdueLoans() throws SQLException;
    List<Loan> getLoansByUser(int userId) throws SQLException;
    List<Loan> getLoansByMaterial(int materialId) throws SQLException;
}