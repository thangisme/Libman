package com.thangqt.libman.service;

import com.thangqt.libman.DAO.LoanDAO;
import com.thangqt.libman.model.Loan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the LoanManager class.
 */
@ExtendWith(MockitoExtension.class)
public class LoanManagerTest {

    @Mock
    private LoanDAO loanDAO;
    @Mock
    private UserManager userManager;
    @Mock
    private MaterialManager materialManager;

    private LoanManager loanManager;

    /**
     * Sets up the test environment before each test.
     * Initializes the LoanManager with mocked dependencies.
     */
    @BeforeEach
    void setUp() {
        loanManager = new LoanManager(loanDAO, userManager, materialManager);
    }

    /**
     * Tests the addLoan method when the document is not issued.
     * Verifies that the loan is added and the material quantity is decreased.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void addLoan_WhenDocumentNotIssued_ShouldAddLoanAndDecreaseMaterialQuantity() throws SQLException {
        Loan loan = new Loan(1, 1, LocalDate.now(), LocalDate.now().plusDays(7));
        when(loanDAO.isDocumentIssued(1, 1)).thenReturn(false);

        loanManager.addLoan(loan);

        verify(loanDAO).add(loan);
        verify(materialManager).decreaseAvailableQuantity(1);
    }

    /**
     * Tests the addLoan method when the document is already issued.
     * Verifies that an exception is thrown and the material quantity is not decreased.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void addLoan_WhenDocumentAlreadyIssued_ShouldThrowException() throws SQLException {
        Loan loan = new Loan(1, 1, LocalDate.now(), LocalDate.now().plusDays(7));
        when(loanDAO.isDocumentIssued(1, 1)).thenReturn(true);

        assertThrows(SQLException.class, () -> loanManager.addLoan(loan));
        verify(materialManager, never()).decreaseAvailableQuantity(anyInt());
    }

    /**
     * Tests the returnLoan method.
     * Verifies that the loan is updated and the material quantity is increased.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void returnLoan_ShouldUpdateLoanAndIncreaseMaterialQuantity() throws SQLException {
        Loan loan = new Loan(1, 1, LocalDate.now(), LocalDate.now().plusDays(7));
        when(loanDAO.getById(1)).thenReturn(loan);

        loanManager.returnLoan(1);

        verify(loanDAO).returnLoan(1);
        verify(materialManager).increaseAvailableQuantity(1);
    }

    /**
     * Tests the getOverdueLoans method.
     * Verifies that the method returns a list of overdue loans.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void getOverdueLoans_ShouldReturnListOfOverdueLoans() throws SQLException {
        List<Loan> overdueLoans = Arrays.asList(
            new Loan(1, 1, LocalDate.now(), LocalDate.now().plusDays(7)),
            new Loan(2, 2, LocalDate.now(), LocalDate.now().plusDays(7))
        );
        when(loanDAO.getOverdueLoans()).thenReturn(overdueLoans);

        List<Loan> result = loanManager.getOverdueLoans();

        assertEquals(overdueLoans, result);
        verify(loanDAO).getOverdueLoans();
    }

    /**
     * Tests the getLoanById method.
     * Verifies that the method returns the expected Loan object.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void getLoanById_ShouldReturnLoan() throws SQLException {
        int loanId = 1;
        Loan expectedLoan = new Loan(loanId, 1, LocalDate.now(), LocalDate.now().plusDays(7));
        when(loanDAO.getById(loanId)).thenReturn(expectedLoan);

        Loan result = loanManager.getLoanById(loanId);

        assertEquals(expectedLoan, result);
        verify(loanDAO).getById(loanId);
    }

    /**
     * Tests the getAllLoans method.
     * Verifies that the method returns a list of all loans.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void getAllLoans_ShouldReturnAllLoans() throws SQLException {
        List<Loan> expectedLoans = Arrays.asList(
            new Loan(1, 1, LocalDate.now(), LocalDate.now().plusDays(7)),
            new Loan(2, 2, LocalDate.now(), LocalDate.now().plusDays(7))
        );
        when(loanDAO.getAll()).thenReturn(expectedLoans);

        List<Loan> result = loanManager.getAllLoans();

        assertEquals(expectedLoans, result);
        verify(loanDAO).getAll();
    }
}