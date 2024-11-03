package com.thangqt.libman.model;

public class Magazine extends Material {
  private int issueNumber;
  private int currentIssue;
  private String issn;

  public Magazine(String title, String publisher, String issn) {
    this.title = title;
    this.publisher = publisher;
    this.issn = issn;
    this.isAvailable = true;
    this.type = MaterialType.Magazine;
  }

  public Magazine(
      String title,
      String author,
      String description,
      String publisher,
      String issn,
      int issueNumber,
      int currentIssue) {
    this.title = title;
    this.author = author;
    this.description = description;
    this.publisher = publisher;
    this.issn = issn;
    this.issueNumber = issueNumber;
    this.currentIssue = currentIssue;
    this.isAvailable = true;
    this.type = MaterialType.Magazine;
  }

  public Magazine(
      String title,
      String author,
      String description,
      String publisher,
      String issn,
      String coverImageUrl,
      int quantity,
      int availableQuantity,
      int issueNumber,
      int currentIssue) {
    this.title = title;
    this.author = author;
    this.description = description;
    this.publisher = publisher;
    this.issn = issn;
    this.coverImageUrl = coverImageUrl;
    this.quantity = quantity;
    this.availableQuantity = availableQuantity;
    this.issueNumber = issueNumber;
    this.currentIssue = currentIssue;
    this.isAvailable = true;
    this.type = MaterialType.Magazine;
  }

  public String getIssn() {
    return issn;
  }

  public void setIssn(String issn) {
    this.issn = issn;
  }

  public int getIssueNumber() {
    return issueNumber;
  }

  public void setIssueNumber(int issueNumber) {
    this.issueNumber = issueNumber;
  }

  public int getCurrentIssue() {
    return currentIssue;
  }

  public void setCurrentIssue(int currentIssue) {
    this.currentIssue = currentIssue;
  }
}
