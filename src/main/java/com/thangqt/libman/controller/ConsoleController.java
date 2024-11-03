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
                    deleteUser();
                    break;
                case 8:
                    issueDocument();
                    break;
                case 9:
                    returnDocument();
                    break;
                case 10:
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
        System.out.println("[7] Delete user");
        System.out.println("[8] Issue Document");
        System.out.println("[9] Return Document");
        System.out.println("[10] Display User Info");
    }

    private void addDocument() throws SQLException {
        System.out.println("\n--- Add Document ---");
        System.out.println("1. Add Book");
        System.out.println("2. Add Magazine");
        int choice = getIntInput("Enter your choice: ");

        String title = getStringInput("Enter title: ");
        String description = getStringInput("Enter description: ");
        int quantity = getIntInput("Enter quantity: ");
        if (quantity <= 0) {
            System.out.println("Quantity must be greater than 0.");
            return;
        }

        if (choice == 1) {
            String author = getStringInput("Enter author: ");
            String publisher = getStringInput("Enter publisher: ");
            String isbn = getStringInput("Enter ISBN: ");
            Book book = new Book(title, author, isbn);
            book.setDescription(description);
            book.setPublisher(publisher);
            book.setQuantity(quantity);
            book.setAvailableQuantity(quantity);
            materialManager.addMaterial(book);
            System.out.println("Book added successfully.");
        } else if (choice == 2) {
            String publisher = getStringInput("Enter publisher: ");
            String issn = getStringInput("Enter ISSN: ");
            Magazine magazine = new Magazine(title, publisher, issn);
            magazine.setQuantity(quantity);
            magazine.setAvailableQuantity(quantity);
            magazine.setDescription(description);
            materialManager.addMaterial(magazine);
            System.out.println("Magazine added successfully.");
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private int searchAndDisplayMaterial(String title) throws SQLException {
        List<Material> materials = materialManager.getMaterialsByTitle(title);
        if (materials.isEmpty()) {
            return -1;
        }
        int i = 1;
        for (Material material : materials) {
            System.out.println("[" + i + "] " + material.getTitle() + " - " + material.getAuthor());
            i++;
        }
        return materials.get(0).getId();
    }

    private void removeDocument() throws SQLException {
        System.out.println(("\n--- Remove Document ---"));
        System.out.println("[1] Remove by ID");
        System.out.println("[2] Remove by title");
        int choice = getIntInput("Enter your choice: ");
        if (choice == 1) {
            int id = getIntInput("Enter document ID to remove: ");
            if (materialManager.isMaterialExist(id)) {
                materialManager.deleteMaterial(id);
                System.out.println("Document removed successfully.");
            } else {
                System.out.println("Document not found.");
            }
        } else if (choice == 2) {
            String title = getStringInput("Enter document title to remove: ");
            int id = searchAndDisplayMaterial(title);
            if (id != -1) {
                materialManager.deleteMaterial(id);
                System.out.println("Document removed successfully.");
            } else {
                System.out.println("Document not found.");
            }
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private void updateDocument() throws SQLException {
        System.out.println("\n--- Update Document ---");
        System.out.println("[1] Update by ID");
        System.out.println("[2] Update by title");
        int choice = getIntInput("Enter your choice: ");
        int id;
        if (choice == 1) {
            id = getIntInput("Enter document ID to update: ");
            if (!materialManager.isMaterialExist(id)) {
                System.out.println("Document not found.");
                return;
            }
        } else if (choice == 2) {
            String title = getStringInput("Enter document title to update: ");
            id = searchAndDisplayMaterial(title);
            if (id == -1) {
                System.out.println("Document not found.");
                return;
            }
        } else {
            System.out.println("Invalid choice.");
            return;
        }
        Material material = materialManager.getMaterialById(id);
        if (material == null) {
            System.out.println("Document not found.");
            return;
        }

        String title = getStringInput("Enter new title (press enter to keep current): ");
        if (!title.isEmpty()) {
            material.setTitle(title);
        }

        String description = getStringInput("Enter new description (press enter to keep current): ");
        if (!description.isEmpty()) {
            material.setDescription(description);
        }

        int quantity = getIntInput("Enter new quantity (press enter to keep current): ");
        if (quantity < 0) {
            System.out.println("Quantity must not be negative.");
            return;
        }
        if (quantity != 0) {
            int currentQuantity = material.getQuantity();
            material.setQuantity(quantity);
            material.setAvailableQuantity(material.getAvailableQuantity() + quantity - currentQuantity);
        }

        if (material instanceof Book) {
            Book book = (Book) material;
            String author = getStringInput("Enter new author (press enter to keep current): ");
            if (!author.isEmpty()) {
                book.setAuthor(author);
            }
            String publisher = getStringInput("Enter new publisher (press enter to keep current): ");
            if (!publisher.isEmpty()) {
                book.setPublisher(publisher);
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
        System.out.println("\n--- Find Document ---");
        System.out.println("[1] Find by ID");
        System.out.println("[2] Find by title");
        int choice = getIntInput("Enter your choice: ");
        if (choice == 1) {
            int id = getIntInput("Enter document ID to find: ");
            displayDocument(id);
        } else if (choice == 2) {
            String title = getStringInput("Enter document title to find: ");
            int id = searchAndDisplayMaterial(title);
            if (id != -1) {
                displayDocument(id);
            } else {
                System.out.println("Document not found.");
            }
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private void displayDocument(int id) throws SQLException {
        Material material = materialManager.getMaterialById(id);
        if (material != null) {
            System.out.println("Title: " + material.getTitle());
            System.out.println("Description: " + material.getDescription());
            System.out.println("Quantity: " + material.getQuantity());
            if (material instanceof Book) {
                Book book = (Book) material;
                System.out.println("Author: " + book.getAuthor());
                System.out.println("Publisher: " + book.getPublisher());
                System.out.println("ISBN: " + book.getIsbn());
            } else if (material instanceof Magazine) {
                Magazine magazine = (Magazine) material;
                System.out.println("Publisher: " + magazine.getPublisher());
                System.out.println("ISSN: " + magazine.getIssn());
            }
        }
    }

    private void displayDocuments() throws SQLException {
        System.out.println("\n--- Display Documents ---");
        System.out.println("[1] All documents:");
        System.out.println("[2] Individual document:");
        int choice = getIntInput("Enter your choice: ");
        if (choice == 1) {
            List<Material> materials = materialManager.getAllMaterials();
            int i = 1;
            for (Material material : materials) {
                System.out.println("[" + i + "] " + material.getTitle() + " - " + material.getAuthor());
            }
            System.out.println("Enter document number to display in detail (0 to cancel): ");
            int docNumber = getIntInput("Enter document number: ");
            if (docNumber > 0 && docNumber <= materials.size()) {
                displayDocument(materials.get(docNumber - 1).getId());
            } else if (docNumber == 0) {
                return;
            } else {
                System.out.println("Invalid document number.");
            }
        } else if (choice == 2) {
            System.out.println("[1] Display by ID");
            System.out.println("[2] Display by title");
            int id;
            int subChoice = getIntInput("Enter your choice: ");
            if (subChoice == 1) {
                id = getIntInput("Enter document ID to display: ");
            } else if (subChoice == 2) {
                String title = getStringInput("Enter document title to display: ");
                id = searchAndDisplayMaterial(title);
            } else {
                System.out.println("Invalid choice.");
                return;
            }
            if (id != -1) {
                displayDocument(id);
            } else {
                System.out.println("Document not found.");
            }
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private void addUser() throws SQLException {
        System.out.println("\n--- Add User ---");
        String name = getStringInput("Enter user name: ");
        String email = getStringInput("Enter user email: ");

        if (!isValidEmail(email)) {
            System.out.println("Invalid email.");
            return;
        }

        if (userManager.isUserExist(email)) {
            System.out.println("User already exists.");
            return;
        }
        User user = new User(name, email);
        userManager.addUser(user);
        System.out.println("User added successfully.");
    }

    private int userIdFromInput(String input) throws SQLException {
        int userId;
        if (isValidEmail(input)) {
            User user = userManager.getUserByEmail(input);
            if (user == null) {
                return -1;
            } else {
                userId = user.getId();
            }
        } else {
            userId = Integer.parseInt(input);
        }
        return userId;
    }
    private void deleteUser() throws SQLException {
        System.out.println("\n--- Delete User ---");
        String input = getStringInput("Enter user ID or email: ");
        int userId = userIdFromInput(input);
        if (userId == -1) {
            System.out.println("User not found.");
            return;
        }
        if (userManager.isUserExist(userId)) {
            userManager.deleteUser(userId);
            System.out.println("User deleted successfully.");
        } else {
            System.out.println("User not found.");
        }
    }

    private void issueDocument() throws SQLException {
        System.out.println("\n--- Issue Document ---");
        int userId = getIntInput("Enter user ID: ");
        int materialId = getIntInput("Enter document ID: ");

        if (!userManager.isUserExist(userId)) {
            System.out.println("User not found.");
            return;
        }
        if (!materialManager.isMaterialExist(materialId)) {
            System.out.println("Document not found.");
            return;
        }
        if (loanManager.isDocumentIssued(userId, materialId)) {
            System.out.println("Document already issued to this user.");
            return;
        }
        if (!materialManager.isMaterialAvailable(materialId)) {
            System.out.println("No copies available.");
            return;
        }

        int loanPeriod = getIntInput("Enter loan period (days): ");
        if (loanPeriod <= 0) {
            System.out.println("Loan period must be greater than 0.");
            return;
        }
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(loanPeriod); // 2 weeks loan period
        Loan loan = new Loan(userId, materialId, borrowDate, dueDate);
        loanManager.addLoan(loan);
        System.out.println("Document issued successfully.");
    }

    private void returnDocument() throws SQLException {
        System.out.println("\n--- Return Document ---");
        System.out.println("[1] Return by loan ID");
        System.out.println("[2] Return by user");
        int loanId;
        int choice = getIntInput("Enter your choice: ");
        if (choice == 1) {
            loanId = getIntInput("Enter loan ID: ");
        } else if (choice == 2) {
            String input = getStringInput("Enter user ID or email: ");
            int userId;
            if (isValidEmail(input)) {
                User user = userManager.getUserByEmail(input);
                if (user == null) {
                    System.out.println("User not found.");
                    return;
                } else {
                    userId = user.getId();
                }
            } else {
                userId = Integer.parseInt(input);
            }
            List<Loan> loans = loanManager.getLoansByUser(userId);
            if (loans.isEmpty()) {
                System.out.println("No loans found for this user.");
                return;
            }
            int i = 1;
            for (Loan loan : loans) {
                Material material = materialManager.getMaterialById(loan.getMaterialId());
                System.out.println("[" + i + "] " + material.getTitle() + " (Due: " + loan.getDueDate() + ")");
                i++;
            }
            int loanNumber = getIntInput("Enter loan number to return (0 to cancel): ");
            if (loanNumber == 0) {
                return;
            }
            loanId = loans.get(loanNumber - 1).getId();
        } else {
            System.out.println("Invalid choice.");
            return;
        }
        if (!loanManager.isLoanExist(loanId)) {
            System.out.println("Loan not found.");
            return;
        }
        loanManager.returnLoan(loanId);
        System.out.println("Document returned successfully.");
    }

    private void displayUserInfo() throws SQLException {
        System.out.println("\n--- User Info ---");
        String input = getStringInput("Enter user ID or email: ");
        int userId = userIdFromInput(input);
        if (userId == -1) {
            System.out.println("User not found.");
            return;
        }
        User user = userManager.getUserById(userId);
        if (user != null) {
            System.out.println("Name: " + user.getName() + " - ID: " + user.getId());
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
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    return 0;
                }
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}