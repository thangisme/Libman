package com.thangqt.libman.model;

import java.time.LocalDate;
import java.util.Date;

public class Loan {
    private int id;
    private Material material;
    private User user;
    private int userId;
    private int materialId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private LocalDate dueDate;

    public Loan(int userId, int materialId, LocalDate borrowDate, LocalDate dueDate) {
        this.userId = userId;
        this.materialId = materialId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    public Loan(int userId, int materialId, LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate) {
        this.userId = userId;
        this.materialId = materialId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public int getMaterialId() {
        return materialId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public int getUserId() {
        return userId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
