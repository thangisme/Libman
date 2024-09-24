package com.thangqt.libman.DAO;

import com.thangqt.libman.model.Loan;
import com.thangqt.libman.service.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MySQLLoanDAO implements LoanDAO {
    private Connection conn;

    public MySQLLoanDAO() throws SQLException {
        this.conn = DatabaseConnection.getConnection();
    }

    @Override
    public void add(Loan loan) throws SQLException {

    }

    @Override
    public void update(Loan loan) throws SQLException {

    }

    @Override
    public void delete(int id) throws SQLException {

    }

    @Override
    public Loan getById(int id) throws SQLException {
        return null;
    }

    @Override
    public List<Loan> getAll() throws SQLException {
        return null;
    }

    @Override
    public List<Loan> getOverdueLoans() throws SQLException {
        return null;
    }

    @Override
    public List<Loan> getLoansByUser(int userID) throws SQLException {
        return null;
    }

    @Override
    public List<Loan> getLoansByMaterial(int materialID) throws SQLException {
        return null;
    }
}
