package com.thangqt.libman.model;

import java.util.List;

enum Role {
  ADMIN,
  USER
}

public class User {
  private int id;
  private String name;
  private String email;
  private String role;
  private String passwordHash;

  public User(String name, String email) {
    this.name = name;
    this.email = email;
    this.role = Role.USER.toString();
  }

  public User(String name, String email, String role, String passwordHash) {
    this.name = name;
    this.email = email;
    this.role = role;
    this.passwordHash = passwordHash;
  }

  public User(int id, String name, String email) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.role = Role.USER.toString();
  }

  public User(int id, String name, String email, String role, String passwordHash) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.role = role;
    this.passwordHash = passwordHash;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public boolean isAdmin() {
    return role.equals(Role.ADMIN.toString());
  }
}
