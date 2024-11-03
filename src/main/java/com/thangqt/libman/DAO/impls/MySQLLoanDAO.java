package com.thangqt.libman.DAO.impls;

import com.thangqt.libman.DAO.LoanDAO;
import com.thangqt.libman.model.Loan;
import com.thangqt.libman.service.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MySQLLoanDAO implements LoanDAO {
    private Connection conn;

    public MySQLLoanDAO() throws SQLException {
        this.conn = DatabaseConnection.getConnection();
    }

    @Override
    public void add(Loan loan) throws SQLException {
        String query = "INSERT INTO loans (user_id, material_id, borrow_date, due_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, loan.getUserId());
            stm.setInt(2, loan.getMaterialId());
            stm.setDate(3, java.sql.Date.valueOf(loan.getBorrowDate()));
            stm.setDate(4, java.sql.Date.valueOf(loan.getDueDate()));
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error adding loan: " + e.getMessage());
        }
    }

    @Override
    public void returnLoan(int id) throws SQLException {
        String query = "UPDATE loans SET return_date = CURDATE() WHERE id = ?";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, id);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error returning loan: " + e.getMessage());
        }
    }

    @Override
    public void update(Loan loan) throws SQLException {
        String query = "UPDATE loans SET user_id = ?, material_id = ?, borrow_date = ?, due_date = ? WHERE id = ?";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, loan.getUserId());
            stm.setInt(2, loan.getMaterialId());
            stm.setDate(3, java.sql.Date.valueOf(loan.getBorrowDate()));
            stm.setDate(4, java.sql.Date.valueOf(loan.getDueDate()));
            stm.setInt(5, loan.getId());
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error updating loan: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM loans WHERE id = ?";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, id);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error deleting loan: " + e.getMessage());
        }
    }

    private Loan createLoanFromResult(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int userId = rs.getInt("user_id");
        int materialId = rs.getInt("material_id");
        LocalDate borrowDate = rs.getDate("borrow_date").toLocalDate();
        LocalDate dueDate = rs.getDate("due_date").toLocalDate();
        LocalDate returnDate = rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null;
        Loan loan = new Loan(userId, materialId, borrowDate, dueDate, returnDate);
        loan.setId(id);
        return loan;
    }

    @Override
    public Loan getById(int id) throws SQLException {
        String query = "SELECT * FROM loans WHERE id = ?";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return createLoanFromResult(rs);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new SQLException("Error getting loan: " + e.getMessage());
        }
    }

    @Override
    public List<Loan> getAll() throws SQLException {
        String query = "SELECT * FROM loans";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            ResultSet rs = stm.executeQuery();
            List<Loan> loans = new ArrayList<>();
            while (rs.next()) {
                Loan loan = createLoanFromResult(rs);
                loans.add(loan);
            }
            return loans;
        } catch (SQLException e) {
            throw new SQLException("Error getting all loans: " + e.getMessage());
        }
    }

    @Override
    public List<Loan> getOverdueLoans() throws SQLException {
        String query = "SELECT * FROM loans WHERE due_date < CURDATE() AND return_date IS NULL";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            ResultSet rs = stm.executeQuery();
            List<Loan> loans = new ArrayList<>();
            while (rs.next()) {
                Loan loan = createLoanFromResult(rs);
                loans.add(loan);
            }
            return loans;
        } catch (SQLException e) {
            throw new SQLException("Error getting overdue loans: " + e.getMessage());
        }
    }

    @Override
    public List<Loan> getLoansByUser(int userID) throws SQLException {
        String query = "SELECT * FROM loans WHERE user_id = ?";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, userID);
            ResultSet rs = stm.executeQuery();
            List<Loan> loans = new ArrayList<>();
            while (rs.next()) {
                Loan loan = createLoanFromResult(rs);
                loans.add(loan);
            }
            return loans;
        } catch (SQLException e) {
            throw new SQLException("Error getting loans by user: " + e.getMessage());
        }
    }

    @Override
    public List<Loan> getLoansByMaterial(int materialID) throws SQLException {
        String query = "SELECT * FROM loans WHERE material_id = ?";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, materialID);
            ResultSet rs = stm.executeQuery();
            List<Loan> loans = new ArrayList<>();
            while (rs.next()) {
                Loan loan = createLoanFromResult(rs);
                loans.add(loan);
            }
            return loans;
        } catch (SQLException e) {
            throw new SQLException("Error getting loans by material: " + e.getMessage());
        }
    }

    @Override
    public boolean isDocumentIssued(int userId, int materialId) throws SQLException {
        String query = "SELECT * FROM loans WHERE user_id = ? AND material_id = ? AND (return_date IS NULL OR return_date > CURDATE())";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, userId);
            stm.setInt(2, materialId);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean isExist(int loanId) throws SQLException {
        String query = "SELECT * FROM loans WHERE id = ?";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, loanId);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public int getTotalBorrowedNumber() throws SQLException {
        String query = "SELECT COUNT(*) FROM loans WHERE return_date IS NULL";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        } catch (SQLException e) {
            return 0;
        }
    }

    @Override
    public int getOverdueLoansNumber() throws SQLException {
        String query = "SELECT COUNT(*) FROM loans WHERE due_date < CURDATE() AND return_date IS NULL";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        } catch (SQLException e) {
            return 0;
        }
    }

    @Override
    public List<Loan> getBorrowedLoansWithinDayRange(int range) throws SQLException {
        String query = "SELECT * FROM loans WHERE borrow_date >= CURDATE() - INTERVAL ? DAY";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, range);
            ResultSet rs = stm.executeQuery();
            List<Loan> loans = new ArrayList<>();
            while (rs.next()) {
                Loan loan = createLoanFromResult(rs);
                loans.add(loan);
            }
            return loans;
        } catch (SQLException e) {
            throw new SQLException("Error getting loans within day range: " + e.getMessage());
        }
    }

    @Override
    public List<Loan> getOverdueLoanWithinDayRange(int range) throws SQLException {
        String query = "SELECT * FROM loans WHERE due_date >= CURDATE() - INTERVAL ? DAY AND return_date IS NULL";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, range);
            ResultSet rs = stm.executeQuery();
            List<Loan> loans = new ArrayList<>();
            while (rs.next()) {
                Loan loan = createLoanFromResult(rs);
                loans.add(loan);
            }
            return loans;
        } catch (SQLException e) {
            throw new SQLException("Error getting overdue loans within day range: " + e.getMessage());
        }
    }

    @Override
    public List<Loan> getReturnedLoansWithinDayRange(int range) throws SQLException {
        String query = "SELECT * FROM loans WHERE return_date >= CURDATE() - INTERVAL ? DAY";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, range);
            ResultSet rs = stm.executeQuery();
            List<Loan> loans = new ArrayList<>();
            while (rs.next()) {
                Loan loan = createLoanFromResult(rs);
                loans.add(loan);
            }
            return loans;
        } catch (SQLException e) {
            throw new SQLException("Error getting returned loans within day range: " + e.getMessage());
        }
    }

    @Override
    public List<Loan> getRecentlyBorrowedLoans(int i) throws SQLException {
        String query = "SELECT * FROM loans ORDER BY borrow_date DESC LIMIT ?";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, i);
            ResultSet rs = stm.executeQuery();
            List<Loan> loans = new ArrayList<>();
            while (rs.next()) {
                Loan loan = createLoanFromResult(rs);
                loans.add(loan);
            }
            return loans;
        } catch (SQLException e) {
            throw new SQLException("Error getting recently borrowed loans: " + e.getMessage());
        }
    }
}
