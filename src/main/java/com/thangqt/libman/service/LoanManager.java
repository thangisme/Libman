package com.thangqt.libman.service;

import com.thangqt.libman.DAO.LoanDAO;
import com.thangqt.libman.model.Loan;
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

    public void addLoan(Loan loan) {

    }

    public void updateLoan(Loan loan) {

    }

    public void deleteLoan(int id) {

    }

    public Loan getLoanById(int id) {
        return null;
    }

    public List<Loan> getAllLoans() {
        return null;
    }

    public List<Loan> getLoansByUser(int userId) {
        return null;
    }

    public List<Loan> getLoansByMaterial(int materialId) {
        return null;
    }

    public void returnLoan(int loanId) {

    }

    public List<Loan> getOverdueLoans() {
        return null;
    }

}
