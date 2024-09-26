package com.thangqt.libman.model;

import java.util.List;

public class User {
    private int id;
    private String name;
    private String email;
    List<Loan> loadHistory;
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
