package com.thangqt.libman.controller;

import com.thangqt.libman.model.*;
import com.thangqt.libman.service.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class ConsoleController {
    private UserManager userManager;
    private MaterialManager materialManager;
    private LoanManager loanManager;
    private Scanner scanner;

    public ConsoleController(UserManager userManager, MaterialManager materialManager, LoanManager loanManager) {
        this.userManager = userManager;
        this.materialManager = materialManager;
        this.loanManager = loanManager;
        this.scanner = new Scanner(System.in);
    }

    public void start() throws SQLException {
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 0:
                    running = false;
                    System.out.println("Exiting the application. Goodbye!");
                    break;
                case 1:
                    addDocument();
                    break;
                case 2:
                    removeDocument();
                    break;
                case 3:
                    updateDocument();
                    break;
                case 4:
                    findDocument();
                    break;
                case 5:
                    displayDocuments();
                    break;
                case 6:
                    addUser();
                    break;
                case 7:
                    borrowDocument();
                    break;
                case 8:
                    returnDocument();
                    break;
                case 9:
                    displayUserInfo();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private void displayMenu() {
        System.out.println("\nWelcome to My Application!");
        System.out.println("[0] Exit");
        System.out.println("[1] Add Document");
        System.out.println("[2] Remove Document");
        System.out.println("[3] Update Document");
        System.out.println("[4] Find Document");
        System.out.println("[5] Display Documents");
        System.out.println("[6] Add User");
        System.out.println("[7] Borrow Document");
        System.out.println("[8] Return Document");
        System.out.println("[9] Display User Info");
    }

    private void addDocument() throws SQLException {
        System.out.println("\n--- Add Document ---");
        System.out.println("1. Add Book");
        System.out.println("2. Add Magazine");
        int choice = getIntInput("Enter your choice: ");

        String title = getStringInput("Enter title: ");

        if (choice == 1) {
            String author = getStringInput("Enter author: ");
            String isbn = getStringInput("Enter ISBN: ");
            Book book = new Book(title, author, isbn);
            materialManager.addMaterial(book);
            System.out.println("Book added successfully.");
        } else if (choice == 2) {
            String publisher = getStringInput("Enter publisher: ");
            String issn = getStringInput("Enter ISSN: ");
            Magazine magazine = new Magazine(title, publisher, issn);
            materialManager.addMaterial(magazine);
            System.out.println("Magazine added successfully.");
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private void removeDocument() throws SQLException {
        int id = getIntInput("Enter document ID to remove: ");
        materialManager.deleteMaterial(id);
        System.out.println("Document removed successfully.");
    }

    private void updateDocument() throws SQLException {
        int id = getIntInput("Enter document ID to update: ");
        Material material = materialManager.getMaterialById(id);
        if (material == null) {
            System.out.println("Document not found.");
            return;
        }

        String title = getStringInput("Enter new title (press enter to keep current): ");
        if (!title.isEmpty()) {
            material.setTitle(title);
        }

        if (material instanceof Book) {
            Book book = (Book) material;
            String author = getStringInput("Enter new author (press enter to keep current): ");
            if (!author.isEmpty()) {
                book.setAuthor(author);
            }
            String isbn = getStringInput("Enter new ISBN (press enter to keep current): ");
            if (!isbn.isEmpty()) {
                book.setIsbn(isbn);
            }
            materialManager.updateMaterial(book);
        } else if (material instanceof Magazine) {
            Magazine magazine = (Magazine) material;
            String publisher = getStringInput("Enter new publisher (press enter to keep current): ");
            if (!publisher.isEmpty()) {
                magazine.setPublisher(publisher);
            }
            String issn = getStringInput("Enter new ISSN (press enter to keep current): ");
            if (!issn.isEmpty()) {
                magazine.setIssn(issn);
            }
            materialManager.updateMaterial(magazine);
        }

        System.out.println("Document updated successfully.");
    }

    private void findDocument() throws SQLException {
        int id = getIntInput("Enter document ID to find: ");
        Material material = materialManager.getMaterialById(id);
        if (material != null) {
            System.out.println(material);
        } else {
            System.out.println("Document not found.");
        }
    }

    private void displayDocuments() throws SQLException {
        List<Material> materials = materialManager.getAllMaterials();
        for (Material material : materials) {
            System.out.println(material);
        }
    }

    private void addUser() throws SQLException {
        String name = getStringInput("Enter user name: ");
        String email = getStringInput("Enter user email: ");
        User user = new User(name, email);
        userManager.addUser(user);
        System.out.println("User added successfully.");
    }

    private void borrowDocument() {
        int userId = getIntInput("Enter user ID: ");
        int materialId = getIntInput("Enter document ID: ");
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(14); // 2 weeks loan period
        Loan loan = new Loan(userId, materialId, borrowDate, dueDate);
        loanManager.addLoan(loan);
        System.out.println("Document borrowed successfully.");
    }

    private void returnDocument() {
        int loanId = getIntInput("Enter loan ID: ");
        loanManager.returnLoan(loanId);
        System.out.println("Document returned successfully.");
    }

    private void displayUserInfo() throws SQLException {
        int userId = getIntInput("Enter user ID: ");
        User user = userManager.getUserById(userId);
        if (user != null) {
            System.out.println(user);
            List<Loan> loans = loanManager.getLoansByUser(userId);
            System.out.println("Current loans:");
            for (Loan loan : loans) {
                if (loan.getReturnDate() == null) {
                    Material material = materialManager.getMaterialById(loan.getMaterialId());
                    System.out.println("- " + material.getTitle() + " (Due: " + loan.getDueDate() + ")");
                }
            }
        } else {
            System.out.println("User not found.");
        }
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}